package Presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import Domain.Appearance;
import Domain.ClassElement;
import Domain.Diagram;
import Domain.DiagramElement;
import Domain.Generator;
import Domain.InterfaceElement;
import Domain.RelationAnnotation;

public class InterfaceGenerator implements Generator {

	private InterfaceElement interfaceElement;
	private Set<String> drawnDependencies = new HashSet<>();
	private List<RelationAnnotation> relationAnnotations = new ArrayList<>();
	private Set<String> annotations = new TreeSet<>();

	public InterfaceGenerator(InterfaceElement interfaceElement) {
		this.interfaceElement = interfaceElement;
	}

	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		String name = this.interfaceElement.name;
		String accessModifier = this.interfaceElement.accessModifier;
		String keywordString = "";
		for (String keyword : this.interfaceElement.keywords)
			if (!keyword.equals("abstract"))
				keywordString += "{" + keyword + "}";
		
		String allAnnotations = " ";
		
		for(String annotation : annotations) {
			allAnnotations += annotation;
			allAnnotations += " ";
		}
		
		allAnnotations = allAnnotations.substring(0, allAnnotations.length() - 1);

		sb.append(
				keywordString + getAccessSymbol(accessModifier) + "interface " + name.replace('/', '.') + " " + allAnnotations + "{\n");
		this.interfaceElement.generateChildren(sb, diagram);
		sb.append("}\n");
		this.generateRelations(sb, diagram);

	}

	private Map<String, String> createClassNameMap(Map<DiagramElement, String> map) {
		Map<String, String> returnMap = new HashMap<String, String>();

		for (DiagramElement element : map.keySet()) {
			returnMap.put(element.name, map.get(element));
		}

		return returnMap;
	}

	public void generateRelations(StringBuilder sb, Diagram diagram) {
		Map<String, String> dependencies = createClassNameMap(this.interfaceElement.dependencies);
		
		for(InterfaceElement otherInterface : this.interfaceElement.extendedInterfaces) {
			sb.append(this.interfaceElement.name + " --|> " + otherInterface.name + "\n");
		}
		
		for (String className : dependencies.keySet()) {
			if (!this.drawnDependencies.contains(className)) {
				ClassElement otherClass = diagram.findDiagramElement(className, ClassElement.class);
				if (otherClass != null) {
					generateDependenciesToClass(sb, dependencies, className, otherClass);
				} else {
					InterfaceElement otherInterface = diagram.findDiagramElement(className, InterfaceElement.class);
					generateDependenciesToInterface(sb, dependencies, className, otherInterface);
				}
			}
		}

	}

	private void generateDependenciesToInterface(StringBuilder sb, Map<String, String> dependencies, String className,
			InterfaceElement otherInterface) {
		if (otherInterface != null) {
			if (otherInterface.hasDependencyTo(this.interfaceElement.name)
					&& !className.equals(this.interfaceElement.name)) {
				if (!otherInterface.hasDrawnDependencyTo(this.interfaceElement.name)) {
					if (otherInterface.hasMultipleDependenciesTo(this.interfaceElement.name)
							&& dependencies.get(className).equals("\"*\"")) {
						sb.append(this.interfaceElement.name + " \"*\"" + " <..> "
								+ dependencies.get(className) + " " + className + "\n");
					} else if (otherInterface.hasMultipleDependenciesTo(this.interfaceElement.name)) {
						sb.append(this.interfaceElement.name + " \"*\"" + " <..> " + className + "\n");
					} else {
						sb.append(this.interfaceElement.name + " <..> " + dependencies.get(className) + " "
								+ className + "\n");
					}
				} else {
					// Do nothing, it's already been drawn
				}
			} else {
				if (!className.equals(this.interfaceElement.name))
					sb.append(this.interfaceElement.name + " ..> " + dependencies.get(className) + " "
							+ className + "\n");
			}
			this.drawnDependencies.add(className);
		} else {

			if (!className.equals(this.interfaceElement.name))
				sb.append(this.interfaceElement.name + " ..> " + dependencies.get(className) + " " + className + "\n");
			this.drawnDependencies.add(className);
		}
	}

	private void generateDependenciesToClass(StringBuilder sb, Map<String, String> dependencies, String className,
			ClassElement otherClass) {
		if (otherClass.hasDependencyTo(this.interfaceElement.name) && !this.interfaceElement.name.equals(className)) {
			if (!otherClass.hasDrawnDependencyTo(this.interfaceElement.name)) {
				if (otherClass.hasMultipleDependenciesTo(this.interfaceElement.name)
						&& dependencies.get(className).equals("\"*\"")) {
					sb.append(this.interfaceElement.name + " \"*\"" + " <..> " + dependencies.get(className) + " "
							+ className + "\n");
				} else if (otherClass.hasMultipleDependenciesTo(this.interfaceElement.name)) {
					sb.append(this.interfaceElement.name + " \"*\"" + " <..> " + className + "\n");
				} else {
					sb.append(this.interfaceElement.name + " <..> " + dependencies.get(className) + " " + className
							+ "\n");
				}
			} else {
				// Do nothing, it's already been drawn
			}
		} else {
			if (!className.equals(this.interfaceElement.name))
				sb.append(this.interfaceElement.name + " ..> " + dependencies.get(className) + " " + className + "\n");
		}

		this.drawnDependencies.add(className);
	}

	public boolean hasDrawnDependencyTo(String otherClassName) {
		return this.drawnDependencies.contains(otherClassName);
	}
	
	public void addRelationAnnotation(RelationAnnotation ra) {
		this.relationAnnotations.add(ra);
	}
	
	public void addAnnotation(String annotation) {
		this.annotations.add(annotation);
	}

	private String getAccessSymbol(String accessMod) {
		switch (accessMod) {
		case "public":
			return "+";
		case "private":
			return "-";
		case "protected":
			return "#";
		default:
			return "~";
		}
	}

}
