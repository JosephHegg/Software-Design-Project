package Domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class AdapterPatternAnalyzer {

	private Diagram diagram;

	public AdapterPatternAnalyzer(Diagram diagram) {
		this.diagram = diagram;
	}

	public void analyze() {
		List<ClassElement> classElements = this.diagram.findDiagramElements(ClassElement.class);

		// For each class
		// ---For each field on class
		// -----For every superclass and interface( gotten recursively)
		// -------For every abstract method on the superclass
		// ---------If any of the following are not true, this field is not adapted
		// --------- 1.) Does this class have a matching method(name, args, return type)?
		// --------- 2.) Does this class' implementation of the method have a methodOwner of
		// --------- 		the same type as the current field?
		// ---------If both those conditions were true for every method, then this field is adapted
		for (ClassElement classElement : classElements) {
			List<ClassElement> superClasses = this.getSuperClasses(classElement);
			List<InterfaceElement> interfaces = this.getInterfaces(classElement);

			for (FieldElement field : classElement.fields) {
				List<DiagramElement> adaptedInterfaces = new ArrayList<>(); // Interface
																			// that's
																			// adapted

				DiagramElement fieldType = this.diagram.findDiagramElement(field.type, ClassElement.class);
				if (fieldType == null) {
					fieldType = this.diagram.findDiagramElement(field.type, InterfaceElement.class);
					if (fieldType == null)
						continue;
				}
				
				for (ClassElement superClass : superClasses) {
					boolean isAdapted = true;
					boolean hasAbstractMethods = false;
					
					List<MethodElement> abstractMethods = this.getAbstractMethods(superClass);
					
					for (MethodElement abstractMethod : abstractMethods) {
						hasAbstractMethods = true;
						MethodElement matchingMethod = this.getMatchingMethod(abstractMethod, classElement);
						if (matchingMethod == null) {
							isAdapted = false;
							break;
						}
						
						if (!matchingMethod.methodOwners.contains(fieldType)) { // Makes
																			// on
																			// call
																			// on
																			// this
																			// field
																			// in
																			// matching
																			// method
							isAdapted = false;
							break;
						}
						
					}

					if (hasAbstractMethods && isAdapted) {
						adaptedInterfaces.add(superClass);
					}
					
				}

				
				for (InterfaceElement interfaceElement : interfaces) {
					boolean isAdapted = true;
					for (MethodElement abstractMethod : interfaceElement.methods) {
						MethodElement matchingMethod = this.getMatchingMethod(abstractMethod, classElement);
						if (matchingMethod == null) {
							isAdapted = false;
							break;
						}
						
						
						if (!matchingMethod.methodOwners.contains(fieldType)) { // Makes
																			// on
																			// call
																			// on
																			// this
																			// field
																			// in
																			// matching
																			// method
							isAdapted = false;
							break;
						}
					}

					if (isAdapted)
						adaptedInterfaces.add(interfaceElement);
				}
				
				
				
				boolean hasTargetAsSuper = false;
				if (fieldType instanceof ClassElement)
					hasTargetAsSuper = filterAdaptedInterfaces((ClassElement) fieldType, adaptedInterfaces);
				else
					hasTargetAsSuper = filterAdaptedInterfaces((InterfaceElement) fieldType, adaptedInterfaces);

				if(!hasTargetAsSuper && !adaptedInterfaces.isEmpty())
					showPattern(classElement, fieldType, adaptedInterfaces);
			}
		}
	}

	private boolean filterAdaptedInterfaces(InterfaceElement fieldType,
			List<DiagramElement> adaptedInterfaces) {
		List<InterfaceElement> interfaces = this.getInterfacesForInterace(fieldType);

		for (DiagramElement adaptedInterface : adaptedInterfaces) {
			if (interfaces.contains(adaptedInterface))
				return true;

		}

		return false;
	}

	private List<InterfaceElement> getInterfacesForInterace(InterfaceElement fieldType) {
		List<InterfaceElement> interfaces = new ArrayList<>();
		List<InterfaceElement> curInterfaces = new ArrayList<>();
		curInterfaces.addAll(fieldType.extendedInterfaces);
		Set<InterfaceElement> seenInterfaces = new HashSet<>();

		while (!curInterfaces.isEmpty() && !seenInterfaces.containsAll(curInterfaces)) {
			List<InterfaceElement> nextSetOfInterfaces = new ArrayList<>();

			for (InterfaceElement element : curInterfaces) {
				if (seenInterfaces.contains(element))
					continue;

				interfaces.add(element);
				List<InterfaceElement> tempInterfaces = new ArrayList<>();
				tempInterfaces.addAll(element.extendedInterfaces);
				seenInterfaces.add(element);
				nextSetOfInterfaces.addAll(tempInterfaces);
			}

			curInterfaces = nextSetOfInterfaces;
		}

		return interfaces;
	}

	private boolean filterAdaptedInterfaces(ClassElement fieldType, List<DiagramElement> adaptedInterfaces) {
		List<ClassElement> superClasses = this.getSuperClasses(fieldType);
		List<InterfaceElement> interfaces = this.getInterfaces(fieldType);

		for (DiagramElement adaptedInterface : adaptedInterfaces) {
			if (superClasses.contains(adaptedInterface) || interfaces.contains(adaptedInterface)) {
				return true;
			}
		}

		return false;
	}

	private void showPattern(ClassElement classElement, DiagramElement field, List<DiagramElement> adaptedInterfaces) {
		classElement.addAnnotation("<<adapter>>");
		classElement.addRelationAnnotation(new RelationAnnotation(field.name, "adapts", "-->"));
		if(field instanceof ClassElement) {
			((ClassElement)field).addAnnotation("<<adaptee>>");
		}
		else {
			((InterfaceElement)field).addAnnotation("<<adaptee>>");
		}
		
		
		for(DiagramElement adaptedInterface : adaptedInterfaces) {
			if(adaptedInterface instanceof ClassElement)
				((ClassElement)adaptedInterface).addAnnotation("<<target>>");
			else
				((InterfaceElement)adaptedInterface).addAnnotation("<<target>>");
		}
	}

	private MethodElement getMatchingMethod(MethodElement methodElement, ClassElement classElement) {

		for (MethodElement classMethod : classElement.methods) {
			boolean sameParams = this.sameParameters(methodElement.params, classMethod.params);
			boolean sameName = methodElement.name.equals(classMethod.name);
			boolean sameType = methodElement.returnType.equals(classMethod.returnType);

			if (sameParams && sameName && sameType)
				return classMethod;
		}

		return null;
	}

	private boolean sameParameters(List<ParameterElement> method1Params, List<ParameterElement> method2Params) {
		if (method1Params.size() != method2Params.size()) {
			return false;
		}

		for (int i = 0; i < method1Params.size(); i++) {
			String firstType = method1Params.get(i).name;
			String secondType = method2Params.get(i).name;

			if (!firstType.equals(secondType)) {
				return false;
			}
		}

		return true;
	}

	private List<MethodElement> getAbstractMethods(ClassElement classElement) {
		List<MethodElement> abstractMethods = new ArrayList<>();
		for (MethodElement me : classElement.methods)
			if (me.keywords.contains("abstract")) {
				abstractMethods.add(me);
			}

		return abstractMethods;
	}

	private List<InterfaceElement> getInterfaces(ClassElement fieldType) {
		Set<InterfaceElement> noDuplicateInterfaces = new HashSet<>();

		noDuplicateInterfaces.addAll(fieldType.interfaces);
		for (InterfaceElement ie : fieldType.interfaces) {
			List<InterfaceElement> extendedInterfaces = this.getInterfacesForInterace(ie);
			noDuplicateInterfaces.addAll(extendedInterfaces);
		}

		ClassElement currentClass = fieldType;

		while (currentClass != null) {
			noDuplicateInterfaces.addAll(currentClass.interfaces);
			for (InterfaceElement ie : currentClass.interfaces) {
				noDuplicateInterfaces.add(ie);
				List<InterfaceElement> extendedInterfaces = this.getInterfacesForInterace(ie);
				noDuplicateInterfaces.addAll(extendedInterfaces);
			}
			
			currentClass = currentClass.superClass;
		}

		List<InterfaceElement> implementedInterfaces = new ArrayList<>();
		implementedInterfaces.addAll(noDuplicateInterfaces);
		
		return implementedInterfaces;
	}

	private List<ClassElement> getSuperClasses(ClassElement fieldType) {
		Set<ClassElement> noDuplicateSuperClasses = new HashSet<>();

		ClassElement currentSuperClass = fieldType.superClass;

		while (currentSuperClass != null) {
			noDuplicateSuperClasses.add(currentSuperClass);
			currentSuperClass = currentSuperClass.superClass;
		}

		List<ClassElement> superClasses = new ArrayList<>();
		superClasses.addAll(noDuplicateSuperClasses);

		return superClasses;
	}
}
