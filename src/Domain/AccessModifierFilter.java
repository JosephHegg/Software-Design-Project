package Domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccessModifierFilter implements DiagramFilter {

	private Set<String> touchedElementNames = new HashSet<String>();

	private AccessModifierComparator comparator;
	
	public AccessModifierFilter(AccessModifierComparator comparator) {
		this.comparator = comparator;
	}
	
	@Override
	public void filterDiagram(List<DiagramElement> diagramElements) {
		List<DiagramElement> elementsToRemove = new ArrayList<>();

		for (DiagramElement diagramElement : diagramElements) {
			if (isClass(diagramElement)) {
				ClassElement classElement = (ClassElement) diagramElement;
				if (comparator.compare(classElement.accessModifier)) {
					filterClass((ClassElement) diagramElement);
				} else {
					elementsToRemove.add(diagramElement);
				}
			}
			if (isInterface(diagramElement)) {
				InterfaceElement interfaceElement = (InterfaceElement) diagramElement;
				if (comparator.compare(interfaceElement.accessModifier)) {
					filterInterface(interfaceElement);
				} else {
					elementsToRemove.add(diagramElement);
				}
				filterInterface((InterfaceElement) diagramElement);
			}
		}
	}

	
	private void filterInterface(InterfaceElement interfaceElement) {
		if (!this.touchedElementNames.contains(interfaceElement.name)) {
			touchedElementNames.add(interfaceElement.name);
			removeMethods(interfaceElement.methods);
		}

	}

	private void filterClass(ClassElement classElement) {
		if (!this.touchedElementNames.contains(classElement.name)){
			touchedElementNames.add(classElement.name);
			removeFields(classElement.fields);
			removeMethods(classElement.methods);

			
			
		}
	}

	

	private void removeFields(List<FieldElement> fields) {
		List<DiagramElement> fieldElementsToRemove = new ArrayList<>();

		for (DiagramElement field : fields) {
			FieldElement fieldElement = (FieldElement) field;
			if (!comparator.compare(fieldElement.accessModifier)) {
				fieldElementsToRemove.add(fieldElement);
			}
		}

		for (DiagramElement fieldElement : fieldElementsToRemove) {
			fields.remove(fieldElement);
		}
	}

	private void removeMethods(List<MethodElement> methods) {
		List<DiagramElement> classMethodsToRemove = new ArrayList<>();

		for (DiagramElement method : methods) {
			MethodElement methodElement = (MethodElement) method;
			if (!comparator.compare(methodElement.accessModifier)) {
				classMethodsToRemove.add(methodElement);
			}
		}

		for (DiagramElement methodElement : classMethodsToRemove) {
			methods.remove(methodElement);
		}
	}

	private boolean isClass(DiagramElement diagramElement) {
		return diagramElement instanceof ClassElement;
	}

	private boolean isInterface(DiagramElement diagramElement) {
		return diagramElement instanceof InterfaceElement;
	}

}
