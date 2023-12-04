package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Domain.ClassElement;
import Domain.DefaultAppearance;
import Domain.DiagramElement;
import Domain.FieldElement;
import Domain.Generator;
import Domain.InterfaceElement;
import Domain.MethodElement;
import Presentation.FieldGenerator;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class InheritanceRecursion {
	private ASM asm;
	private ASMBaseParser asmParser;
	
	public InheritanceRecursion(ASM asm, ASMBaseParser asmParser){
		this.asm = asm;
		this.asmParser = asmParser;
	}
	
	private ClassNode getClassNodeWithName(String className) {
		ClassNode classNode = null;
		try {
			classNode = this.asm.getClassNode(className);
		} catch (IOException e) {
			throw new RuntimeException("Unable to make classNode from " + className);
		}
		return classNode;
	}
	
	private String replaceSlashes(String className) {
		return className.replace('/', '.');
	}
	
	public ClassElement getBaseClass(ClassNode c){
		List<FieldNode> fields = c.fields;
		List<FieldElement> fieldElements = new ArrayList<>();
		for(FieldNode f : fields){
			
			String type = this.replaceSlashes(Type.getType(f.desc).getClassName());
			List<String> innerFieldTypes = asmParser.visitAndReturnClasses(f.signature, f.desc, new ArrayList<String>());		
			//System.out.println("-->" + array.getClass().getSimpleName());
			FieldElement fe = new FieldElement(new DefaultAppearance(), f.name, type, innerFieldTypes, null, null);
			fieldElements.add(fe);
			Generator fieldGenerator = new FieldGenerator(fe);
			fe.setGenerator(fieldGenerator);
		}
		
		ClassElement cElement = this.asmParser.createClassElementFromClassNode(c);
		
		cElement.setFields(fieldElements);
		
		return cElement;
	}
	
	
	public List<ClassElement> getSuperClasses(ClassNode c) {
		List<ClassElement> superClasses = new ArrayList<>();
		
		Set<String> seenClasses = new HashSet<>();
		seenClasses.add("java.lang.Object");
		String curSuperClass = c.superName == null ? null : replaceSlashes(c.superName);
		while((curSuperClass != null) && !(seenClasses.contains(curSuperClass))) {
			ClassNode curSuperClassNode = this.getClassNodeWithName(curSuperClass);
			ClassElement element = this.asmParser.createClassElementFromClassNode(curSuperClassNode);
			List<MethodElement> methods = this.asmParser.createMethodElementsForClassNode(curSuperClassNode);
			element.setMethods(methods);
			superClasses.add(element);	
			seenClasses.add(curSuperClass);
			
			curSuperClass = replaceSlashes(curSuperClassNode.superName);
		}
		
		return superClasses;
	}
	
	public List<InterfaceElement> getInterfaces(ClassNode c) {
		List<InterfaceElement> interfaces = new ArrayList<>();
		
		Set<String> seenInterfaces = new HashSet<>();
		
		List<String> curInterfaces = c.interfaces;
		for(String s : curInterfaces) {
			s = replaceSlashes(s);
		}
		
		String curSuperClass = c.superName;
		
		while(curSuperClass != null) {
				curSuperClass = replaceSlashes(curSuperClass);
				ClassNode curSuperNode = this.getClassNodeWithName(curSuperClass);
				List<String> tempInterfaces = curSuperNode.interfaces;
				for(String s : tempInterfaces) {
					s = replaceSlashes(s);
				}
				curInterfaces.addAll(tempInterfaces);
				
				curSuperClass = curSuperNode.superName;
		}
		
		
		while(!(curInterfaces.isEmpty()) && !(seenInterfaces.containsAll(curInterfaces))) {
			List<String> nextSetOfInterfaces = new ArrayList<>();
			
			for(String interfaceName : curInterfaces) {
				if(seenInterfaces.contains(interfaceName))
					continue;
				
				ClassNode interfaceNode = this.getClassNodeWithName(interfaceName);
				List<MethodElement> methods = this.asmParser.createMethodElementsForClassNode(interfaceNode);
				InterfaceElement element = this.asmParser.createInterfaceElementFromClassNode(interfaceNode);
				element.setMethods(methods);
				interfaces.add(element);
				
				List<String> tempInterfaces = interfaceNode.interfaces;
				for(String s : tempInterfaces) {
					s = replaceSlashes(s);
				}
				seenInterfaces.add(interfaceName);
				
				nextSetOfInterfaces.addAll(tempInterfaces);
			}
			
			curInterfaces = nextSetOfInterfaces;
		}
		return interfaces;
	}
}
