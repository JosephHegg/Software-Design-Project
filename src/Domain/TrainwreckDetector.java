package Domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_COLOR_BURNPeer;

import DataSource.ASM;
import jdk.internal.org.objectweb.asm.Opcodes;

public class TrainwreckDetector {

	private Diagram diagram;
	private ASM asm;

	private Map<String, HashSet<String>> classMapWhiteListClasses = new HashMap<>();

	private List<ClassElement> diagramClassElements;
	
	private HashSet<String> whiteListClasses;

	public TrainwreckDetector(Diagram diagram, ASM asm) {
		this.diagram = diagram;
		this.asm = asm;
		this.whiteListClasses = buildWhiteList();
	}

	private HashSet<String> buildWhiteList(){
		HashSet<String> whiteList = new HashSet<String>();
		whiteList.add("[]");
		whiteList.add("void");
		whiteList.add("char");
		whiteList.add("long");
		whiteList.add("int");
		whiteList.add("boolean");
		whiteList.add("short");
		whiteList.add("byte");
		whiteList.add("float");
		whiteList.add("double");
		whiteList.add("java.util.List");
		whiteList.add("java.util.Set");
		whiteList.add("java.util.Map");
		
		// add classes of primitives
		
		whiteList.add("java.lang.Character");
		whiteList.add("java.lang.Long");
		whiteList.add("java.lang.Boolean");
		whiteList.add("java.lang.Integer");
		whiteList.add("java.lang.Double");
		whiteList.add("java.lang.Float");
		whiteList.add("java.lang.Short");
		whiteList.add("java.lang.Byte");
		
	
		
		return whiteList;
	}
	public void analyze() {

		List<MethodCallElement> methodCalls = this.diagram.findDiagramElements(MethodCallElement.class);

		for (MethodCallElement methodCall : methodCalls) {
			recursivelyCheckMethodCall(methodCall);
		}
		/*
		 * this.diagramClassElements =
		 * this.diagram.findDiagramElements(ClassElement.class); for
		 * (ClassElement classElement : this.diagramClassElements) { ClassNode
		 * classNode = getClassNode(classElement);
		 * populateClassWhiteListWithSelfAndFields(classElement);
		 * populateMethodElementsWithClassDependencies(classElement, classNode);
		 * }
		 */

	}

	private void recursivelyCheckMethodCall(MethodCallElement methodCall) {
		populateClassElementWhiteListWithSelfAndSupersAndFields(methodCall);
		checkForTrainWreck(methodCall);
		for (MethodCallElement childCall : methodCall.methodCalls) {
			recursivelyCheckMethodCall(childCall);
		}
	}

	private void populateClassElementWhiteListWithSelfAndSupersAndFields(MethodCallElement methodCallElement) {
		HashSet<String> values = new HashSet<>();
		values.add(methodCallElement.ownerClassElement.name);
		values.add("java.lang.Object");

		for (ClassElement c : methodCallElement.superClasses) {
			values.add(c.name);
		}

		for (FieldElement field : methodCallElement.ownerClassElement.fields) {

			for (String s : field.innerTypes) {
				values.add(s);

			}
			values.add(field.type);

		}

		this.classMapWhiteListClasses.put(methodCallElement.ownerClassElement.name, values);

	}

	private void checkForTrainWreck(MethodCallElement methodCallElement) {

		ClassElement classElement = methodCallElement.ownerClassElement;
		ClassNode classNode = getClassNode(classElement);

		for (MethodNode method : classNode.methods) {

			Iterator<AbstractInsnNode> instructionIterator = method.instructions.iterator();

			while (instructionIterator.hasNext()) {
				AbstractInsnNode nextNode = instructionIterator.next();

				if (!(nextNode instanceof MethodInsnNode))
					continue;

				MethodInsnNode methodInsnNode = (MethodInsnNode) nextNode;

				ClassElement methodInsnNodeClassElementType = methodCallElement.ownerClassElement;

				// if data structure

				if (isDataStructure(methodInsnNodeClassElementType)) {
					HashSet<String> mapSet = this.classMapWhiteListClasses.get(classElement.name);
					mapSet.add(this.replaceSlashes(methodInsnNode.owner));
					this.classMapWhiteListClasses.put(classElement.name, mapSet);
					continue;

				}

				// if it is an object created via NEW
				if (methodInsnNode.name.equals("<init>")) {
					HashSet<String> mapSet = this.classMapWhiteListClasses.get(classElement.name);
					mapSet.add(this.replaceSlashes(methodInsnNode.owner));
					this.classMapWhiteListClasses.put(classElement.name, mapSet);
					continue;
				}
				// it is either this or a field of this

				if (this.classMapWhiteListClasses.get(classElement.name)
						.contains(this.replaceSlashes(methodInsnNode.owner))) {
					continue;
				}

				// if it is a type of parameter passed in the method

				Type[] parameterTypes = Type.getArgumentTypes(method.desc);

				boolean foundParameterType = false;

				for (Type t : parameterTypes) {
					if (t.getClassName().equals(this.replaceSlashes(methodInsnNode.owner))) {
						foundParameterType = true;
						continue;
					}
				}

				if (!foundParameterType && !this.whiteListClasses.contains(this.replaceSlashes(methodInsnNode.owner))) {

					setViolationAppearance(methodCallElement);
					
					System.out.println("Violation caused by: " + methodInsnNode.owner);

					break;
				}

			}

		}

	}

	private void setViolationAppearance(DiagramElement methodCall) {
		methodCall.setAppearance(new TrainWreckAppearance("blue"));
	}

	// Objects hide data & compute with it
	// Data Structures expose but don't compute
	// Hybrids expose & compute
	// If Hybrid, can violate PLK
	// We must check the fields that aren't private to see if they compute
	// anything within themselves

	private boolean isDataStructure(ClassElement classElement) {

		ClassNode classNode = getClassNode(classElement);

		if(this.whiteListClasses.contains(this.replaceSlashes(classNode.name))){
			
			return true;
		}
		
		List<FieldElement> fields = filterNonPrivateFields(classElement.fields);
		if (fields.isEmpty()) {
			return false; // everything hidden
		}

		for (FieldElement field : fields) {
			FieldNode fieldNode = findFieldNodeFromClassNode(field, classNode);
			if (fieldComputes(classNode.methods, fieldNode, classNode))
				return false;
		}

		return true;
	}

	// Credits entirely to
	// https://stackoverflow.com/questions/9634349/detecting-field-mutation-using-asm
	// for this method

	// We are treating mutation as computation for this definition
	private boolean fieldComputes(List<MethodNode> methods, FieldNode field, ClassNode fieldOwner) {

		for (MethodNode methodNode : methods) {
			Iterator<AbstractInsnNode> instructionIterator = methodNode.instructions.iterator();

			while (instructionIterator.hasNext()) {
				AbstractInsnNode abstractInsNode = instructionIterator.next();

				if (abstractInsNode.getType() != AbstractInsnNode.FIELD_INSN) {
					continue;
				}

				FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsNode;

				if (fieldInsnNode.getOpcode() != Opcodes.PUTFIELD) {
					continue;
				}

				if (fieldInsnNode.name.equals(field.name) && fieldInsnNode.owner.equals(fieldOwner.name)) {
					return true;
				}
			}
		}
		return false;

	}

	private FieldNode findFieldNodeFromClassNode(FieldElement f, ClassNode c) {
		FieldNode returnNode = null;
		for (FieldNode fieldNode : c.fields) {
			if (f.name.equals(fieldNode.name)) {
				return fieldNode;
			}
		}

		return returnNode;

	}

	private List<FieldElement> filterNonPrivateFields(List<FieldElement> fields) {
		List<FieldElement> returnFields = new ArrayList<FieldElement>();
		for (FieldElement f : fields) {
			if (f.accessModifier != "private") {
				returnFields.add(f);
			}
		}

		return returnFields;
	}

	private ClassElement findClassElementWithName(String owner) {
		for (ClassElement c : this.diagramClassElements) {
			if (owner.equals(c.name)) {
				return c;
			}
		}
		return null;

	}

	private ClassNode getClassNode(ClassElement classElement) {
		try {
			return asm.getClassNode(classElement.name);
		} catch (IOException e) {
			System.err.println("Name doesn't exist.");
			e.printStackTrace();
		}
		return null;
	}

	public String getClassNameFromCallSignature(String callSignature) {

		return callSignature.substring(callSignature.lastIndexOf('.') + 1, callSignature.length());

	}

	public String getMethodNameFromMethodSignature(String methodSignature) {
		int parenthIndex = methodSignature.indexOf('(');
		String toParentheses = methodSignature.substring(0, parenthIndex);
		int index = toParentheses.lastIndexOf('.');

		return methodSignature.substring(index + 1, parenthIndex);

	}

	public String replaceSlashes(String className) {
		return className.replace('/', '.');
	}

}
