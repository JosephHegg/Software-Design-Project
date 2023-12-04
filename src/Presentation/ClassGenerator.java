package Presentation;

import java.util.ArrayList;
import java.util.Arrays;
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
import Domain.FieldElement;
import Domain.Generator;
import Domain.InterfaceElement;
import Domain.PrimitiveElement;
import Domain.RelationAnnotation;

public class ClassGenerator implements Generator {

	private ClassElement classElement;
	private Set<String> primitives;
	private Set<String> drawnAssociations = new HashSet<>();
	private Set<String> drawnDependencies = new HashSet<>();
	private List<RelationAnnotation> relationAnnotations = new ArrayList<>();
	private Set<String> annotations = new TreeSet<>();

	public ClassGenerator(ClassElement classElement) {
		this.classElement = classElement;
		String[] primitives = new String[] { "int", "short", "byte", "long", "float", "double", "char", "boolean" };
		this.primitives = new HashSet<>(Arrays.asList(primitives));
	}

	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		String name = this.classElement.name;
		String accessModifier = this.classElement.accessModifier;
		String keywordString = "";
		for (String keyword : this.classElement.keywords) {
			if (keyword.equals("abstract"))
				keywordString += keyword + " ";
		}
		
		String allAnnotations = " ";
		
		for(String annotation : annotations) {
			allAnnotations += annotation;
			allAnnotations += " ";
		}
		
		allAnnotations = allAnnotations.substring(0, allAnnotations.length() - 1);
		
		sb.append(getAccessSymbol(accessModifier) + keywordString + "class " + name.replace('/', '.') + " " + allAnnotations + "{\n");
		generateChildren(sb,diagram);
		sb.append("}\n");
		generateRelations(sb, diagram);
	}

	private void generateChildren(StringBuilder sb, Diagram diagram) {
		for (DiagramElement field : classElement.fields)
			field.generate(sb, diagram);
		for (DiagramElement method : classElement.methods)
			method.generate(sb,diagram);
	}
	
	private Map<String, String> createClassNameMap(Map<DiagramElement, String> map) {
		Map<String, String> returnMap = new HashMap<String, String>();
		
		for(DiagramElement element : map.keySet()) {
			returnMap.put(element.name, map.get(element));
		}
		
		return returnMap;
	}

	private void generateRelations(StringBuilder sb,Diagram diagram) {
		for (DiagramElement element : classElement.interfaces)
			this.drawArrow(sb, this.classElement.name, "", "", element.name, ".up.|>");
			//sb.append(classElement.name + " .up.|> " + element.name + "\n");
		if (classElement.superClass != null)
			this.drawArrow(sb, classElement.name, "", "", classElement.superClass.name, "-up-|>");
			//sb.append(classElement.name + " -up-|> " + classElement.superClass.name + "\n");

		Map<String, String> associations = createClassNameMap(this.classElement.associations);
		Map<String, String> dependencies = createClassNameMap(this.classElement.dependencies);
		
		for(String className : associations.keySet()) {
			if(!this.drawnAssociations.contains(className)) {
				ClassElement otherClass = diagram.findDiagramElement(className, ClassElement.class);
				if(otherClass != null) {
					if(otherClass.hasAssociation(this.classElement.name)) {
							if(!otherClass.hasDrawnAssociationTo(this.classElement.name)) {
								if(otherClass.hasMultipleAssociationsTo(this.classElement.name) && associations.get(className).equals("\"*\"")) {
									this.drawArrow(sb, this.classElement.name, "\"*\"", associations.get(className), className, "<-->");
									//sb.append(this.classElement.name + " \"*\"" + " <--> " + associations.get(className) + " " + className + "\n");
								} else if (otherClass.hasMultipleAssociationsTo(this.classElement.name)){
									this.drawArrow(sb, this.classElement.name, "\"*\"", "", className, "<-->");
									//sb.append(this.classElement.name + " \"*\"" + " <--> " + className + "\n");
								} else {
									this.drawArrow(sb, this.classElement.name, "", associations.get(className), className, "<-->");
									//sb.append(this.classElement.name + " <--> " + associations.get(className) + " " + className + "\n");
								}
							} else {
								//Do nothing, it's already been drawn
							}
					} else {
						this.drawArrow(sb, this.classElement.name, "", associations.get(className), className, "-->");
						//sb.append(this.classElement.name + " --> " + associations.get(className) + " " + className + "\n");
					}
					this.drawnAssociations.add(className);
				} else {
					this.drawArrow(sb, this.classElement.name, "", associations.get(className), className, "-->");
					//sb.append(this.classElement.name + " --> " + associations.get(className) + " " + className + "\n");
					this.drawnAssociations.add(className);
				}
			}
		}
		
		for(String className : dependencies.keySet()) {
			if(!this.drawnAssociations.contains(className) && !this.drawnDependencies.contains(className)){
				ClassElement otherClass = diagram.findDiagramElement(className, ClassElement.class);

				
				if(otherClass != null) {
					if(otherClass.hasDependencyTo(this.classElement.name) && !this.classElement.name.equals(className)) {
						
							if(!otherClass.hasDrawnDependencyTo(this.classElement.name)) {
								
								if(otherClass.hasMultipleDependenciesTo(this.classElement.name) && dependencies.get(className).equals("\"*\"")) {
									this.drawArrow(sb, this.classElement.name, "\"*\"", dependencies.get(className), className, "<..>");
									//sb.append(this.classElement.name + " \"*\"" + " <..> " + dependencies.get(className) + " " + className + "\n");
								} else if (otherClass.hasMultipleDependenciesTo(this.classElement.name)){
									this.drawArrow(sb, this.classElement.name, "\"*\"", "", className, "<..>");
									//sb.append(this.classElement.name + " \"*\"" + " <..> " + className + "\n");
								} else {
									this.drawArrow(sb, classElement.name, "", dependencies.get(className), className, "<..>");
									//sb.append(this.classElement.name + " <..> " + dependencies.get(className) + " " + className + "\n");
								}
							} else {
								//Do nothing, it's already been drawn
							}
					} else {
						if(!className.equals(this.classElement.name))
							this.drawArrow(sb, this.classElement.name, "", dependencies.get(className), className, "..>");
							//sb.append(this.classElement.name + " ..> " + dependencies.get(className) + " " + className + "\n");
					}
					this.drawnDependencies.add(className);
				} else {
					InterfaceElement otherInterface = diagram.findDiagramElement(className, InterfaceElement.class);
					if(otherInterface != null) {
						if(otherInterface.hasDependencyTo(this.classElement.name)) {
								if(!otherInterface.hasDrawnDependencyTo(this.classElement.name) && !this.classElement.name.equals(className)) {
									
									if(otherInterface.hasMultipleDependenciesTo(this.classElement.name) && dependencies.get(className).equals("\"*\"")) {
										this.drawArrow(sb, this.classElement.name, "\"*\"", dependencies.get(className), className, "<..>");
										//sb.append(this.classElement.name + " \"*\"" + " <..> " + dependencies.get(className) + " " + className + "\n");
									} else if (otherInterface.hasMultipleDependenciesTo(this.classElement.name)){
										this.drawArrow(sb, this.classElement.name, "\"*\"", "", className, "<..>");
										//sb.append(this.classElement.name + " \"*\"" + " <..> " + className + "\n");
									} else {
										this.drawArrow(sb, this.classElement.name, "", dependencies.get(className), className, "<..>");
										//sb.append(this.classElement.name + " <..> " + dependencies.get(className) + " " + className + "\n");
									}
								} else {
									//Do nothing, it's already been drawn
								}
						} else {
							if(!this.classElement.name.equals(className))
								this.drawArrow(sb, this.classElement.name, "", dependencies.get(className), className, "..>");
								//sb.append(this.classElement.name + " ..> " + dependencies.get(className) + " " + className + "\n");
						}
						this.drawnDependencies.add(className);
					} else {
						if(!classElement.name.equals(className))
							this.drawArrow(sb, this.classElement.name, "", dependencies.get(className), className, "..>");
							
						//sb.append(this.classElement.name + " ..> " + dependencies.get(className) + " " + className + "\n");
						this.drawnDependencies.add(className);
					}
				}
			}
		}
	}
	
	private void drawArrow(StringBuilder sb, String fromClass, String fromMult, String toMult, String toClass, String arrowType) {
		for(RelationAnnotation ra : this.relationAnnotations) {
			if(ra.fromClass.equals(toClass) && ra.arrowType.equals(arrowType)) {
				sb.append(fromClass + " " + fromMult + " " + arrowType + " " + toMult + " " + toClass + " : " + ra.message + "\n");
				return;
			}
		}
			
		sb.append(fromClass + " " + fromMult + " " + arrowType + " " + toMult + " " + toClass + "\n");
	}

	public boolean hasDrawnAssociationTo(String otherClassName) {
		return this.drawnAssociations.contains(otherClassName);
	}

	public boolean hasDrawnDependencyTo(String otherClassName) {
		return this.drawnDependencies.contains(otherClassName);
	}
	
	public void addRelationAnnotation(RelationAnnotation annotation) {
		this.relationAnnotations.add(annotation);
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
