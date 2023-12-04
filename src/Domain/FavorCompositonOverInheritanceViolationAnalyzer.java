package Domain;

import java.util.List;

public class FavorCompositonOverInheritanceViolationAnalyzer {
	
	private Diagram diagram;
	
	public FavorCompositonOverInheritanceViolationAnalyzer(Diagram diagram) {
		this.diagram = diagram;
	}
	
	public void analyze() {
		List<ClassElement> classElements = diagram.findDiagramElements(ClassElement.class);
		
		for(ClassElement classElement : classElements) {
			if(classElement.superClass != null) {
				boolean allAbstractMethods = checkForAllAbstractMethods(classElement.superClass);
				boolean hasAllMethodsOfSuperClass = checkForAllMethodsOfSuperClass(classElement);
				boolean hasFieldOfSuperClass = checkForFieldOfSuperClass(classElement);
				
//				System.out.println("Class Element Name: " + classElement.name);
//				System.out.println("AllAbstractMethods? = " + allAbstractMethods);
//				System.out.println("HasAllMethodsOfSuperClass? = " + hasAllMethodsOfSuperClass);
//				System.out.println("HasFieldsOfSuperClass? = " + hasFieldOfSuperClass);
				
				if(allAbstractMethods && !hasFieldOfSuperClass) {
					continue;
				}
				
				if(hasFieldOfSuperClass && hasAllMethodsOfSuperClass) {
					continue;
				}
				
				classElement.addAnnotation("<<favorCompositionOverInheritance>>");
				
				classElement.addRelationAnnotation(new RelationAnnotation(classElement.superClass.name, "Violates", "-up-|>"));
			}
		}
	}
	
	private boolean checkForAllMethodsOfSuperClass(ClassElement classElement) {
		List<MethodElement> classMethods = classElement.methods;
		List<MethodElement> superClassMethods = classElement.superClass.methods;
		
		for(MethodElement superClassMethodElement : superClassMethods) {
			boolean hasMatchingElement = false;
			
			if(superClassMethodElement.name.equals("<init>"))
				continue;
			
			for(MethodElement classMethod : classMethods) {
				
				if(superClassMethodElement.name.equals(classMethod.name)) {
					if(sameParameters(classMethod.params, superClassMethodElement.params)) {
						hasMatchingElement = true;
					}
				}
			}
			
			if(!hasMatchingElement) {
				return false;
			}
		}
		
 		return true;
	}

	private boolean sameParameters(List<ParameterElement> method1Params, List<ParameterElement> method2Params) {
		if(method1Params.size() != method2Params.size()) {
			return false;
		}
		
		for(int i = 0; i < method1Params.size(); i++) {
			String firstType = method1Params.get(i).name;
			String secondType = method2Params.get(i).name;
			
			if(!firstType.equals(secondType)) {
				return false;
			}
		}
		
		return true;
	}

	private boolean checkForFieldOfSuperClass(ClassElement classElement) {
		// need to check if it has a list of the super class???
		
		for(DiagramElement association : classElement.associations.keySet()) {
			if(association.name.equals(classElement.superClass.name)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean checkForAllAbstractMethods(ClassElement classElement) {
		for(MethodElement method : classElement.methods) {
			if(!method.name.equals("<init>") && !method.keywords.contains("abstract")) {
				return false;
			}
		}
		
		return true;
	}
}
