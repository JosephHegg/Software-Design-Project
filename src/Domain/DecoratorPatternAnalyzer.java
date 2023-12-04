package Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecoratorPatternAnalyzer {

	private Diagram diagram;

	public DecoratorPatternAnalyzer(Diagram d) {
		this.diagram = d;
	}

	public void analyze() {
		List<ClassElement> classElements = this.diagram.findDiagramElements(ClassElement.class);
		for (ClassElement classElement : classElements) {
			checkDecorator(classElement, classElements);
		}
	}

	private void checkDecorator(ClassElement classElement, List<ClassElement> classElements) {
		List<ClassElement> allSuperClasses = this.getSuperClasses(classElement);
		List<InterfaceElement> allImplementedInterfaces = this.getInterfaces(classElement);

		for(FieldElement field : classElement.fields) {
			ClassElement fieldClassElement = findClassElement(allSuperClasses, field.type);
			
			if(fieldClassElement != null) {
				List<MethodElement> potentialComponentMethods = fieldClassElement.methods;
				List<MethodElement> potentialDecoratorMethods = classElement.methods;
				
				if(this.hasSameMethods(potentialComponentMethods, potentialDecoratorMethods, classElement)) {
					addAnnotations(classElement, fieldClassElement);
				}
			}
		}
		
		for(FieldElement field : classElement.fields) {
			InterfaceElement fieldInterfaceElement = findInterfaceElement(allImplementedInterfaces, field.type);
			
			if(fieldInterfaceElement != null) {
				List<MethodElement> potentialComponentMethods = fieldInterfaceElement.methods;
				List<MethodElement> potentialDecoratorMethods = classElement.methods;
				
				if(this.hasSameMethods(potentialComponentMethods, potentialDecoratorMethods, classElement)) {
					addAnnotations(classElement, fieldInterfaceElement);
				}
			}
		}

		checkSuperClasses(classElement, allSuperClasses);
	}
	
	private void checkSuperClasses(ClassElement classElement, List<ClassElement> superClassElements) {
		for(ClassElement superClass : superClassElements) {
			
			List<ClassElement> allSuperClasses = this.getSuperClasses(superClass);
			List<InterfaceElement> superImplementedInterfaces = this.getInterfaces(superClass);
			
			for(FieldElement field : superClass.fields) {
				if(field.type.equals(superClass.name) || !fieldIsAccessibleBySubClass(field)) 
					continue;
				
				ClassElement fieldClassElement = findClassElement(allSuperClasses, field.type);
				
				if(fieldClassElement != null) {
					List<MethodElement> potentialComponentMethods = fieldClassElement.methods;
					List<MethodElement> potentialDecoratorMethods = classElement.methods;
					
					if(hasSameMethods(potentialComponentMethods, potentialDecoratorMethods, classElement)) {
						addAnnotations(classElement, fieldClassElement);
					}
				}
			}
			
			for(FieldElement field : superClass.fields) {
				if(!fieldIsAccessibleBySubClass(field))
					continue;
				
				InterfaceElement fieldInterfaceElement = findInterfaceElement(superImplementedInterfaces, field.type);
				
				if(fieldInterfaceElement != null) {
					List<MethodElement> potentialComponentMethods = fieldInterfaceElement.methods;
					List<MethodElement> potentialDecoratorMethods = classElement.methods;
					
					if(this.hasSameMethods(potentialComponentMethods, potentialDecoratorMethods, classElement)) {
						addAnnotations(classElement, fieldInterfaceElement);
					}
				}
			}
			
		}
	}

	private boolean fieldIsAccessibleBySubClass(FieldElement field) {
		return field.accessModifier.equals("public") || field.accessModifier.equals("protected");
	}

	private InterfaceElement findInterfaceElement(List<InterfaceElement> elements, String nameToFind) {
		for (InterfaceElement element : elements) {
			if (element.name.equals(nameToFind)) {
				return element;
			}
		}

		return null;
	}

	private ClassElement findClassElement(List<ClassElement> elements, String nameToFind) {
		for (ClassElement element : elements) {
			if (element.name.equals(nameToFind)) {
				return element;
			}
		}

		return null;
	}

	private void addAnnotations(ClassElement decorator, ClassElement component) {
		decorator.addAnnotation("<<decorator>>");
		component.addAnnotation("<<component>>");
		decorator.addRelationAnnotation(new RelationAnnotation(component.name, "decorates", "-->"));
	}

	private void addAnnotations(ClassElement decorator, InterfaceElement component) {
		decorator.addAnnotation("<<decorator>>");
		component.addAnnotation("<<component>>");
		decorator.addRelationAnnotation(new RelationAnnotation(component.name, "decorates", "-->"));
	}

	private boolean hasSameMethods(List<MethodElement> componentMethods, List<MethodElement> classMethods,
			ClassElement classElement) {

		for (MethodElement componentMethod : componentMethods) {
			boolean hasMatchingElement = false;

			if (componentMethod.name.equals("<init>"))
				continue;

			for (MethodElement classMethod : classMethods) {

				if (componentMethod.name.equals(classMethod.name)) {
					if (sameParameters(componentMethod.params, classMethod.params)) {
						hasMatchingElement = true;
					}
				}
			}

			if (!hasMatchingElement) {
				boolean superClassHasAbstractMethod = checkSuperClassForAbstractMethod(classElement, componentMethod);

				if (superClassHasAbstractMethod) {
					continue;
				}

				return false;
			}
		}

		return true;
	}

	private boolean checkSuperClassForAbstractMethod(ClassElement classElement, MethodElement componentMethod) {
		ClassElement currentClass = classElement.superClass;

		while (currentClass != null) {
			for (MethodElement method : currentClass.methods) {
				if (method.name.equals(componentMethod.name)) {
					if (sameParameters(componentMethod.params, method.params) && method.keywords.contains("abstract")) {
						return true;
					}
				}
			}

			currentClass = currentClass.superClass;
		}

		return false;
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

	private List<InterfaceElement> getInterfaces(ClassElement fieldType) {
		
		
		Set<InterfaceElement> noDuplicateInterfaces = new HashSet<>();

		noDuplicateInterfaces.addAll(fieldType.interfaces);
		for (InterfaceElement ie : fieldType.interfaces) {
			List<InterfaceElement> extendedInterfaces = this.getInterfacesForInterace(ie);
			noDuplicateInterfaces.addAll(extendedInterfaces);
		}

		ClassElement currentClass = fieldType.superClass;

		while (currentClass != null) {
			noDuplicateInterfaces.addAll(currentClass.interfaces);
			for (InterfaceElement ie : currentClass.interfaces) {
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
