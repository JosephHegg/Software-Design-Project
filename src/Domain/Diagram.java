package Domain;

import java.util.ArrayList;
import java.util.List;

public class Diagram {
	private List<DiagramElement> diagramElements;
	private DiagramFilter filter;
	
	public Diagram(List<DiagramElement> diagramElements, DiagramFilter filter){
		this.diagramElements = diagramElements;
		this.filter = filter;
	}
	
	public void generate(StringBuilder sb){
		sb.append("@startuml\n");
		sb.append("skinparam class {\n");
		sb.append("       BackgroundColor<<component>> LightGreen\n");
		sb.append("       BackgroundColor<<decorator>> LightGreen\n");
		sb.append("       BackgroundColor<<target>> Red\n");
		sb.append("       BackgroundColor<<adapter>> Red\n");
		sb.append("       BackgroundColor<<adaptee>> Red\n");
		sb.append("       BackgroundColor<<favorCompositionOverInheritance>> Yellow\n");
		sb.append("}\n");
		this.filter.filterDiagram(diagramElements);
		for(DiagramElement element : this.diagramElements)
			element.generate(sb,this);
		sb.append("@enduml\n");
	}
	
	public void setDiagramFilter(DiagramFilter filter) {
		this.filter = filter;
	}
	
	public <T> T findDiagramElement(String nameToFind, Class<T> type) {
		for(DiagramElement element : this.diagramElements) {
			if(type.isInstance(element)){
				if(element.name.equals(nameToFind))
					return (T)element;
			}
		}
		
		return null;
	}
	
	// Credit: https://stackoverflow.com/questions/17840483/how-to-have-java-method-return-generic-list-of-any-type/17840541
	
	public <T> List<T> findDiagramElements(Class<T> type) {
		List<T> elements = new ArrayList<>();
		for(DiagramElement element : this.diagramElements) {
			if(type.isInstance(element)) {
				elements.add((T) element);
			}
		}
		
		return elements;
	}
	
	
}
