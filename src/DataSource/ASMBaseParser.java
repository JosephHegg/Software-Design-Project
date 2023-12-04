package DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Domain.AccessModifierFilter;
import Domain.Appearance;
import Domain.ClassElement;
import Domain.DefaultAppearance;
import Domain.Diagram;
import Domain.DiagramElement;
import Domain.FieldElement;
import Domain.Generator;
import Domain.InterfaceElement;
import Domain.MethodElement;
import Domain.ParameterElement;
import Domain.PrivateComparator;
import Presentation.ClassGenerator;
import Presentation.DoNothingGenerator;
import Presentation.FieldGenerator;
import Presentation.InterfaceGenerator;
import Presentation.MethodGenerator;
import Presentation.ParameterGenerator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

public class ASMBaseParser extends AbstractParser {

	private ASMParseData asmParseData;
	private List<ClassNode> classNodes = new ArrayList<>();
	private Map<String, DiagramElement> classesAndInterfaces = new HashMap<>();
	private List<DiagramElement> elements = new ArrayList<>();
	private Set<String> primitives = new HashSet<>();
	private List<String> collections = new ArrayList<>();
	private InheritanceRecursion classRecurser;

	public ASMBaseParser(ASMParseData parseData) {
		
		super(parseData);
		this.asmParseData = parseData;
		this.primitives.add("void");
		this.primitives.add("char");
		this.primitives.add("long");
		this.primitives.add("int");
		this.primitives.add("boolean");
		this.primitives.add("short");
		this.primitives.add("byte");
		this.primitives.add("float");
		this.primitives.add("double");
		collections.add("java.util.List");
		collections.add("java.util.Set");
		collections.add("java.util.Map");
		this.classRecurser = new InheritanceRecursion(this.asmParseData.asm, this);
	}

	public Diagram doParseBehavior() {

		Set<String> classNames = this.asmParseData.classNamesToParse;

		for (String className : classNames) {
			ClassNode classNode = this.getClassNode(className);
			this.classNodes.add(classNode);
		}

		// Make class elements and add to diagram
		// ---> make method and field elements
		// ------>Add dependencies to class

		for (ClassNode c : this.classNodes) {
			DiagramElement de = createClassOrInterfaceElementFromClassNode(c);
			this.classesAndInterfaces.put(de.name, de);
			elements.add(de);
		}

		for (ClassNode c : this.classNodes) {
			if ((c.access & Opcodes.ACC_INTERFACE) != 0)
				addDiagramElementsToInterface(c);
			else
				addDiagramElementsToClass(c);
		}

		return new Diagram(elements, new AccessModifierFilter(new PrivateComparator()));
	}

	public ClassNode getClassNode(String className) {
		ClassNode c = null;
		try {
			c = this.asmParseData.getClassNode(className);
		} catch (IOException e) {
			if (className.endsWith("[]")) {
				String cutName = className.substring(0, className.length() - 2);
				try {
					c = this.asmParseData.getClassNode(cutName);
				} catch (IOException e1) {
				}
			}
		}

		return c;
	}

	private DiagramElement createClassOrInterfaceElementFromClassNode(ClassNode c) {
		DiagramElement element = null;
		if ((c.access & Opcodes.ACC_INTERFACE) != 0)
			element = createInterfaceElementFromClassNode(c);
		else
			element = createClassElementFromClassNode(c);

		return element;
	}

	private void addDiagramElementsToInterface(ClassNode c) {
		InterfaceElement interfaceElement = (InterfaceElement) this.classesAndInterfaces
				.get(this.asmParseData.replaceSlashes(c.name));

		Map<DiagramElement, String> dependencies = createDependenciesForInterface(c);

		List<MethodElement> methods = createMethodElementsForClassNode(c);

		List<InterfaceElement> interfaces = createInterfaceElementsForClassNode(c);

		interfaceElement.setDependencies(dependencies);
		interfaceElement.setMethods(methods);
		interfaceElement.setInterfaces(interfaces);
		interfaceElement.setSuperClass(createClassElementFromClassNode(c));
	}

	private Map<DiagramElement, String> createDependenciesForInterface(ClassNode c) {
		Map<DiagramElement, String> dependencies = new HashMap<>();

		for (MethodNode method : c.methods) {
			addMethodSignatureDependencies(method, dependencies);
		}

		return dependencies;
	}

	public void addElementToRelationMap(DiagramElement element, String val, Map<DiagramElement, String> relationMap) {
		if (relationMap.containsKey(element)) {
			String oldVal = relationMap.get(element);
			if (val.equals("*") && oldVal.equals("")) { // empty string
														// corresponds to 1
														// cardinality
				relationMap.put(element, val);
			}
		} else {
			relationMap.put(element, val);
		}
	}

	public boolean isPrimitive(String className) {
		return this.primitives.contains(className);
	}

	public void visitNodesAndAddToMap(String sig, String desc, Map<DiagramElement, String> map) {
		// String name =
		// this.asmParseData.replaceSlashes(Type.getType(desc).getClassName());
		List<String> oneTimeOccurrences = new ArrayList<String>();
		List<String> baseClasses = this.visitAndReturnClasses(sig, desc, oneTimeOccurrences);
		for (String className : baseClasses) {
			if (this.classesAndInterfaces.containsKey(className))
				this.addElementToRelationMap(this.classesAndInterfaces.get(className), "\"*\"", map);
		}
		for (String className : oneTimeOccurrences) {
			if (!this.isPrimitive(className) && this.classesAndInterfaces.containsKey(className))
				this.addElementToRelationMap(this.classesAndInterfaces.get(className), "", map);
		}

	}

	private void addDiagramElementsToClass(ClassNode c) {
		ClassElement classElement = (ClassElement) this.classesAndInterfaces
				.get(this.asmParseData.replaceSlashes(c.name));

		Map<DiagramElement, String> associations = new HashMap<>();
		List<FieldElement> fields = createFieldElementsForClassNode(c, associations);

		Map<DiagramElement, String> dependencies = createDependenciesForClassNode(c);
		List<MethodElement> methods = createMethodElementsForClassNode(c); // this
																			// creates
																			// ParamElements
																			// as
																			// well
																			// and
																			// adds
																			// to
																			// MethodElement
		List<InterfaceElement> interfaces = createInterfaceElementsForClassNode(c);

		classElement.setFields(fields);
		classElement.setMethods(methods);
		classElement.setInterfacesList(interfaces);
		classElement.setSuperClass(createSuperClassElementForClassNode(c));
		classElement.setAssociations(associations);
		classElement.setDependencies(dependencies);
	}

	private List<InterfaceElement> createInterfaceElementsForClassNode(ClassNode c) {
		List<InterfaceElement> interfaces = new ArrayList<>();
		
		for (String key : c.interfaces) {
			InterfaceElement val = (InterfaceElement) this.classesAndInterfaces.get(this.asmParseData.replaceSlashes(key));

			if (val != null)
				interfaces.add(val);

		}

		return interfaces;
	}

	private ClassElement createSuperClassElementForClassNode(ClassNode c) {
		if (c.superName == null) {
			return null;
		}

		return (ClassElement) this.classesAndInterfaces.get(this.asmParseData.replaceSlashes(c.superName));
	}

	private Map<DiagramElement, String> createDependenciesForClassNode(ClassNode c) {
		Map<DiagramElement, String> classNodeDependencies = new HashMap<>();

		for (MethodNode m : c.methods) {
			addMethodSignatureDependencies(m, classNodeDependencies);
			addMethodInsnNodeDependencies(m, classNodeDependencies);
		}
		return classNodeDependencies;
	}
	
	private void addMethodOwnersToMethodElement(MethodNode methodNode, MethodElement methodElement) {
		InsnList instructions = methodNode.instructions;
		List<DiagramElement> methodOwners = new ArrayList<>();
		
		for (int i = 0; i < instructions.size(); i++) {
			AbstractInsnNode methodCall = instructions.get(i);
			if (!(methodCall instanceof MethodInsnNode))
				continue;

			MethodInsnNode methodInsnNode = (MethodInsnNode) methodCall;
			if(methodInsnNode.name.equals("<init>"))
				continue;

			String ownerName = this.parseData.replaceSlashes(methodInsnNode.owner);
			DiagramElement owner = this.classesAndInterfaces.get(ownerName);
			if(owner != null)
				methodOwners.add(owner);
		}
		
		methodElement.setMethodOwners(methodOwners);
	}

	private void addMethodSignatureDependencies(MethodNode m, Map<DiagramElement, String> dependencies) {

		String methodDesc = m.desc.substring(m.desc.indexOf(")") + 1);
		String methodSig = null;
		if (m.signature != null)
			methodSig = m.signature.substring(m.signature.indexOf(")") + 1);


		this.visitNodesAndAddToMap(methodSig, methodDesc, dependencies);

		 Type[] parameterTypes = Type.getArgumentTypes(m.desc);
		 if(parameterTypes == null)
			 parameterTypes = new Type[0];
		 
		 
		 for (int i = 0; i < parameterTypes.length; i++) {
			 String parameterTypeName = this.asmParseData.replaceSlashes(parameterTypes[i].getClassName());
			 ClassNode classNode = this.getClassNode(parameterTypeName);
			 if(classNode != null) {
				 String classDesc = "L" + classNode.name + ";";
				 this.visitNodesAndAddToMap(classNode.signature, classDesc, dependencies);
			 }
			 
		 }
	}

	private void addMethodInsnNodeDependencies(MethodNode m, Map<DiagramElement, String> dependencies) {

		InsnList instructions = m.instructions;
		for (int i = 0; i < instructions.size(); i++) {
			AbstractInsnNode methodCall = instructions.get(i);

			if (!(methodCall instanceof MethodInsnNode))
				continue;

			MethodInsnNode methodNode = (MethodInsnNode) methodCall;
			if (methodNode.name.equals("<init>")) {
				ClassNode classNode = this.getClassNode(methodNode.owner);
				String classDesc = "L" + methodNode.owner + ";";
				this.visitNodesAndAddToMap(classNode.signature, classDesc, dependencies);
			} else {
				ClassNode classNode = this.getClassNode(this.parseData.replaceSlashes(Type.getReturnType(methodNode.desc).getClassName()));
				if(classNode != null){
					String classDesc = "L" + classNode.name + ";";
					this.visitNodesAndAddToMap(classNode.signature, classDesc, dependencies);
				}
			}
			
			
			
		}

	}

	

	List<MethodElement> createMethodElementsForClassNode(ClassNode classNode) {
		List<MethodElement> methodElements = new ArrayList<>();

		for (MethodNode method : classNode.methods) {
			String methodName = method.name;
			Appearance appearance = new DefaultAppearance();
			String accessMod = this.getAccessModifierFromAccess(method.access);
			List<String> keywords = this.getKeywordsFromAccess(method.access);
			String returnType = this.asmParseData.replaceSlashes(Type.getReturnType(method.desc).getClassName());
			List<ParameterElement> methodParameters = createParametersForMethodNode(method, classNode.name);

			MethodElement methodElement = new MethodElement(appearance, methodName, returnType, accessMod, keywords,
					methodParameters);
			Generator methodGenerator = new MethodGenerator(methodElement);
			methodElement.setGenerator(methodGenerator);
			methodElements.add(methodElement);
			
			if((classNode.access & Opcodes.ACC_INTERFACE) == 0) //If not an interface
				addMethodOwnersToMethodElement(method, methodElement);
		}

		return methodElements;
	}

	private List<FieldElement> createFieldElementsForClassNode(ClassNode c, Map<DiagramElement, String> associations) {
		List<FieldElement> fields = new ArrayList<>();
		List<FieldNode> nodes = c.fields;

		for (FieldNode field : nodes) {
			String name = field.name;
			String accessMod = this.getAccessModifierFromAccess(field.access);
			List<String> keywords = this.getKeywordsFromAccess(field.access);
			String type = this.asmParseData.replaceSlashes(Type.getType(field.desc).getClassName());

			FieldElement fe = new FieldElement(new DefaultAppearance(), name, type, accessMod, keywords);
			fields.add(fe);
			Generator fieldGenerator = new FieldGenerator(fe);
			fe.setGenerator(fieldGenerator);

			this.visitNodesAndAddToMap(field.signature, field.desc, associations);
		}

		return fields;
	}public String replaceSlashes(String className) {
		return className.replace('/', '.');
	}

	InterfaceElement createInterfaceElementFromClassNode(ClassNode c) {
		String interfaceName = this.asmParseData.replaceSlashes(c.name);
		List<String> keywords = this.getKeywordsFromAccess(c.access);
		String accessModifier = this.getAccessModifierFromAccess(c.access);
		Appearance appearance = new DefaultAppearance();

		InterfaceElement interfaceElement = new InterfaceElement(appearance, interfaceName, accessModifier, keywords);
		Generator interfaceGenerator = new InterfaceGenerator(interfaceElement);
		interfaceElement.setGenerator(interfaceGenerator);

		return interfaceElement;
	}

	ClassElement createClassElementFromClassNode(ClassNode c) {
		String accessMod = this.getAccessModifierFromAccess(c.access);
		List<String> keywords = this.getKeywordsFromAccess(c.access);
		Appearance appearance = new DefaultAppearance();
		String name = this.asmParseData.replaceSlashes(c.name);

		ClassElement returnElement = new ClassElement(appearance, name, accessMod, keywords);
		Generator generator = new ClassGenerator(returnElement);
		returnElement.setGenerator(generator);

		return returnElement;
	}

	private List<ParameterElement> createNamelessParameters(MethodNode node, String className) {
		List<ParameterElement> params = new ArrayList<>();

		Type[] types = Type.getArgumentTypes(node.desc);

		for (int i = 0; i < types.length; i++) {
			String type = this.asmParseData.replaceSlashes(types[i].getClassName());
			ParameterElement methodParameter = new ParameterElement(new DefaultAppearance(), "", type,
					new ArrayList<String>());
			Generator parameterGenerator = new ParameterGenerator(methodParameter);
			methodParameter.setGenerator(parameterGenerator);
			params.add(methodParameter);
		}

		return params;
	}

	private List<ParameterElement> createParametersForMethodNode(MethodNode node, String className) {

		List<ParameterNode> params = node.parameters;
		if (params == null) {
			return createNamelessParameters(node, className);
		}

		Type[] types = Type.getArgumentTypes(node.desc);

		List<ParameterElement> parameterElements = new ArrayList<>();

		for (int i = 0; i < params.size(); i++) {
			ParameterNode p = params.get(i);
			String name = p.name == null ? "" : p.name;
			String type = this.asmParseData.replaceSlashes(types[i].getClassName());

			List<String> keywords = getKeywordsFromAccess(p.access);

			ParameterElement methodParameter = new ParameterElement(new DefaultAppearance(), name, type, keywords);
			Generator parameterGenerator = new ParameterGenerator(methodParameter);
			methodParameter.setGenerator(parameterGenerator);
			parameterElements.add(methodParameter);
		}

		return parameterElements;
	}

	private String getAccessModifierFromAccess(int access) {
		if ((access & Opcodes.ACC_PRIVATE) != 0)
			return "private";
		if ((access & Opcodes.ACC_PROTECTED) != 0)
			return "protected";
		if ((access & Opcodes.ACC_PUBLIC) != 0)
			return "public";

		return "package-private";
	}

	private List<String> getKeywordsFromAccess(int access) {
		List<String> keywords = new ArrayList<>();

		if ((access & Opcodes.ACC_FINAL) != 0)
			keywords.add("final");
		if ((access & Opcodes.ACC_STATIC) != 0)
			keywords.add("static");
		if ((access & Opcodes.ACC_ABSTRACT) != 0)
			keywords.add("abstract");

		return keywords;
	}

	/**
	 * 
	 * @param signature
	 * @param desc
	 * @param visitor
	 * @return List full of classes that the signature has multiple instances of
	 */
	public List<String> visitAndReturnClasses(String signature, String desc, List<String> oneTimers) {
		MyVisitor visitor = new MyVisitor();
		String className = this.asmParseData.replaceSlashes(Type.getType(desc).getClassName());
		if (desc.contains("[")) {
			String arrClassName = className.substring(0, className.indexOf("["));
			ArrayList<String> returnList = new ArrayList<String>();
			returnList.add(arrClassName);
			return returnList;
		}
		if (signature == null) { // Return empty list so we know that we don't
									// have multiple
			oneTimers.add(className);
			return new ArrayList<String>();
		}

		SignatureReader reader = new SignatureReader(signature);
		reader.accept(visitor);
		return getBaseClasses(visitor, oneTimers);
	}

	public boolean hasTargetInterface(String className, String targetInterface) {
		ClassNode classNode = this.getClassNode(className);
		
		List<InterfaceElement> interfaces = this.classRecurser.getInterfaces(classNode);
		
		for(InterfaceElement element : interfaces) {
			if(element.name.equals(targetInterface))
				return true;
		}

		return className.equals(targetInterface);
	}

	public List<String> getBaseClasses(MyVisitor visitor, List<String> oneTimers) {

		List<String> returnList = new ArrayList<String>();
		int numClasses = visitor.seenClasses.size();
		boolean seenMap = false;
		boolean seenCollection = false;
		for (int i = 0; i < numClasses; i++) {
			String formattedName = this.asmParseData.replaceSlashes(visitor.seenClasses.get(i));
			if (this.hasTargetInterface(formattedName, "java.util.Map")) {
				seenMap = true;
				oneTimers.add(formattedName);
				continue;
			}
			if (this.hasTargetInterface(formattedName, "java.util.Collection")) {
				oneTimers.add(formattedName);
				seenCollection = true;
				continue;
			}
			if (seenMap) {
				returnList.add(formattedName);
				continue;
			}
			if (i == numClasses - 1) {
				if(seenMap || seenCollection)
					returnList.add(formattedName);
				else
					oneTimers.add(formattedName);
				continue;
			}
			oneTimers.add(formattedName);
		}

		return returnList;
	}

	public Map<String, DiagramElement> getClassesAndInterfaces(){
		return this.classesAndInterfaces;
	}
}
