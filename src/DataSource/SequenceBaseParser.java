package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import Domain.AccessModifierFilter;
import Domain.Appearance;
import Domain.ClassElement;
import Domain.DefaultAppearance;
import Domain.Diagram;
import Domain.DiagramElement;
import Domain.DiagramFilter;
import Domain.InterfaceElement;
import Domain.MethodCallElement;
import Domain.MethodElement;
import Domain.PrivateComparator;
import Presentation.MethodCallGenerator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SequenceBaseParser extends SequenceAbstractParser{
	
	SequenceParseData parseData;
	List<DiagramElement> elements = new ArrayList<>();
	ASMBaseParser asmParser;
	int topLevel = 0;
	InheritanceRecursion classRecurser;

	public SequenceBaseParser(SequenceParseData parseData) {
		this.parseData = parseData;
		this.asmParser = new ASMBaseParser(new ASMParseData(this.parseData.asm, new HashSet<String>()));
		this.classRecurser = new InheritanceRecursion(this.parseData.asm, this.asmParser);
	}
	
	@Override
	public Diagram doParseBehavior(String methodSignature) {
		String className = this.getClassNameFromMethodSignature(methodSignature);
		this.topLevel = this.parseData.recursiveDepth;
		this.parseMethodCalls(methodSignature, this.parseData.recursiveDepth, "NONE", className);
		return new Diagram(this.elements, new AccessModifierFilter(new PrivateComparator()));
	}

	public MethodCallElement parseMethodCalls(String methodSignature, int recursiveDepth, String callee, String caller) {
		String methodName = getMethodNameFromMethodSignature(methodSignature);
		
		ClassNode classNode;
		if(!callee.equals("NONE"))
			classNode = getClassNodeWithName(callee);
		else
			classNode = getClassNodeWithName(this.getClassNameFromMethodSignature(methodSignature));
		
		List<String> methodArgs = getMethodArgsFromMethodSignature(methodSignature);
		
		MethodNode method = getMethodNodeWithMatchingArgs(classNode, methodName, methodArgs);
		
		if(method == null) {
			throw new RuntimeException("Method " + methodName +" does not exist");
		}	
		
		String returnType = this.parseData.replaceSlashes(Type.getReturnType(method.desc).getClassName());
		InsnList insns = method.instructions;
		
		List<MethodInsnNode> methodInstructionNodes = new ArrayList<>();
		
		for(int i = 0; i < insns.size(); i++){
			AbstractInsnNode insn = insns.get(i);
			
			if(isMethodCall(insn))
				methodInstructionNodes.add((MethodInsnNode)insn);
			
		}
		
		MethodCallElement element = new MethodCallElement(new DefaultAppearance(), methodSignature, callee, caller, returnType, methodArgs);
		element.setGenerator(new MethodCallGenerator(element));
		if(recursiveDepth == topLevel)
			this.elements.add(element);
		List<MethodCallElement> methodCallElements = new ArrayList<>();
		
		if(callee.equals("NONE"))
			callee = this.getClassNameFromMethodSignature(methodSignature);
		
		
		ClassNode calleeNode = this.getClassNodeWithName(callee);
		
		List<InterfaceElement> interfaces = this.classRecurser.getInterfaces(calleeNode);
		List<ClassElement> superClasses = this.classRecurser.getSuperClasses(classNode);
		ClassElement baseClass = this.classRecurser.getBaseClass(classNode);
		
		element.setImplementedInterfaces(interfaces);
		element.setSuperClasses(superClasses);
		element.setOwnerClass(baseClass);
		
		element.setAbstract(((calleeNode.access & Opcodes.ACC_ABSTRACT) != 0));
		
		for(MethodInsnNode insnNode : methodInstructionNodes){
			String nextCalleeName = this.parseData.replaceSlashes(insnNode.owner);
			
			ClassNode nextCalleeClassNode = getClassNodeWithName(nextCalleeName);
			
			MethodNode nextCalleeMethod = getMethodNodeWithName(nextCalleeClassNode, insnNode.name);
			
			Type[] nextCalleeTypes = Type.getArgumentTypes(nextCalleeMethod.desc);
			
			String result = buildMethodSignature(nextCalleeTypes, insnNode, nextCalleeName);
			
			if(recursiveDepth != 0)
				methodCallElements.add(this.parseMethodCalls(result, recursiveDepth-1, nextCalleeName, callee));
			
}
		
		element.setMethodCallElements(methodCallElements);
		return element;
	}

	private MethodNode getMethodNodeWithName(ClassNode classNode, String name) {
		while(classNode != null){
			List<MethodNode> methods = classNode.methods;
			for(MethodNode method : methods) {
				if(method.name.equals(name)) {
					return method;
				}
			}
			classNode = classNode.superName == null ? null : getClassNodeWithName(this.parseData.replaceSlashes(classNode.superName));
		}
		
		return null;
	}

	private ClassNode getClassNodeWithName(String className) {
		ClassNode classNode = null;
		try {
			classNode = this.parseData.getClassNode(className);
		} catch (IOException e) {
			throw new RuntimeException("Unable to make classNode from " + className);
		}
		return classNode;
	}
	
	private MethodNode getMethodNodeWithMatchingArgs(ClassNode classNode, String methodName, List<String> methodArgs) {
		MethodNode desiredMethod = null;
		boolean needToBreak = false;
		while(!needToBreak && (classNode != null)){
			
			List<MethodNode> methods = classNode.methods;
			
			for(MethodNode method : methods){
				if(method.name.equals(methodName)){
					Type[] paramTypes = Type.getArgumentTypes(method.desc);
					
					if(paramTypes.length == methodArgs.size()) {
						int typeIndex = 0;
						boolean foundDesired = true;
						
						for(String arg : methodArgs) {
							// below checks the following: java.util.List.contains(List)
						
							if(!this.parseData.replaceSlashes(paramTypes[typeIndex].getClassName()).contains(arg)) {
								foundDesired = false;
							}
							
							typeIndex++;
						}
						
						if(foundDesired) {
							desiredMethod = method;
							needToBreak = true;
							break;
						}
					}
				}
			}
			
			classNode = classNode.superName == null ? null : getClassNodeWithName(this.parseData.replaceSlashes(classNode.superName));
		}
		
		return desiredMethod;
	}

	private boolean isMethodCall(AbstractInsnNode insn) {
		return insn instanceof MethodInsnNode;
	}
	
	
	private String buildMethodSignature(Type[] types, MethodInsnNode insnNode, String callee) {
		String methodSignature = "";
			
		methodSignature += callee + ".";
		methodSignature += insnNode.name + "(";
			
		for(Type currType : types){
			methodSignature += this.parseData.replaceSlashes(currType.getClassName()) + ",";
		}
			
		if(types.length != 0)
			methodSignature = methodSignature.substring(0, methodSignature.length() -1);
		methodSignature += ")";
		
		return methodSignature;
	}

	
	public String getClassNameFromMethodSignature(String methodSignature) {
		int parenthesesIndex = methodSignature.indexOf('(');
		String toParentheses = methodSignature.substring(0, parenthesesIndex);
		int classCutoffIndex = toParentheses.lastIndexOf('.');
		return methodSignature.substring(0, classCutoffIndex);
	}

	private List<String> getMethodArgsFromMethodSignature(String methodSignature) {
		int openParenthesesIndex = methodSignature.indexOf('(');
		int closeParenthesesIndex = methodSignature.lastIndexOf(')');
		
		String[] args = methodSignature.substring(openParenthesesIndex + 1, closeParenthesesIndex).split(",");
		List<String> returnArgs = new ArrayList<String>();
		
		for(String arg : Arrays.asList(args)) {
			if(!arg.equals(""))
				returnArgs.add(arg);
		}
		return returnArgs;
	}
	
	public String getMethodNameFromMethodSignature(String methodSignature) {
		int parenthIndex = methodSignature.indexOf('(');
		String toParentheses = methodSignature.substring(0, parenthIndex);
		int index = toParentheses.lastIndexOf('.');
		
		return methodSignature.substring(index + 1, parenthIndex);

	}
	
//	List<ClassElement> getSuperClasses(ClassNode c) {
//		List<ClassElement> superClasses = new ArrayList<>();
//		
//		Set<String> seenClasses = new HashSet<>();
//		seenClasses.add("java.lang.Object");
//		String curSuperClass = c.superName == null ? null : this.parseData.replaceSlashes(c.superName);
//		while((curSuperClass != null) && !(seenClasses.contains(curSuperClass))) {
//			ClassNode curSuperClassNode = this.getClassNodeWithName(curSuperClass);
//			ClassElement element = this.asmParser.createClassElementFromClassNode(curSuperClassNode);
//			List<MethodElement> methods = this.asmParser.createMethodElementsForClassNode(curSuperClassNode);
//			element.setMethods(methods);
//			superClasses.add(element);	
//			seenClasses.add(curSuperClass);
//			
//			curSuperClass = this.parseData.replaceSlashes(curSuperClassNode.superName);
//		}
//		
//		return superClasses;
//	}
	
//	List<InterfaceElement> getInterfaces(ClassNode c) {
//		List<InterfaceElement> interfaces = new ArrayList<>();
//		
//		Set<String> seenInterfaces = new HashSet<>();
//		
//		List<String> curInterfaces = c.interfaces;
//		for(String s : curInterfaces) {
//			s = this.parseData.replaceSlashes(s);
//		}
//		
//		String curSuperClass = c.superName;
//		
//		while(curSuperClass != null) {
//				curSuperClass = this.parseData.replaceSlashes(curSuperClass);
//				ClassNode curSuperNode = this.getClassNodeWithName(curSuperClass);
//				List<String> tempInterfaces = curSuperNode.interfaces;
//				for(String s : tempInterfaces) {
//					s = this.parseData.replaceSlashes(s);
//				}
//				curInterfaces.addAll(tempInterfaces);
//				
//				curSuperClass = curSuperNode.superName;
//		}
		
		
//		while(!(curInterfaces.isEmpty()) && !(seenInterfaces.containsAll(curInterfaces))) {
//			List<String> nextSetOfInterfaces = new ArrayList<>();
//			
//			for(String interfaceName : curInterfaces) {
//				if(seenInterfaces.contains(interfaceName))
//					continue;
//				
//				ClassNode interfaceNode = this.getClassNodeWithName(interfaceName);
//				List<MethodElement> methods = this.asmParser.createMethodElementsForClassNode(interfaceNode);
//				InterfaceElement element = this.asmParser.createInterfaceElementFromClassNode(interfaceNode);
//				element.setMethods(methods);
//				interfaces.add(element);
//				
//				List<String> tempInterfaces = interfaceNode.interfaces;
//				for(String s : tempInterfaces) {
//					s = this.parseData.replaceSlashes(s);
//				}
//				seenInterfaces.add(interfaceName);
//				
//				nextSetOfInterfaces.addAll(tempInterfaces);
//			}
//			
//			curInterfaces = nextSetOfInterfaces;
//		}
//		return interfaces;
//	}
 }
