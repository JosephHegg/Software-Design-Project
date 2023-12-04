package Presentation;

import Domain.Appearance;
import Domain.Diagram;
import Domain.DiagramElement;
import Domain.Generator;
import Domain.MethodElement;

public class MethodGenerator implements Generator {
	
	private MethodElement methodElement;
	
	public MethodGenerator(MethodElement methodElement) {
		this.methodElement = methodElement;
	}

	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		String name = this.methodElement.name;
		String accessMod = getAccessSymbol(this.methodElement.accessModifier);
		String keywordString = "";
		String returnType = this.methodElement.returnType;
		for(String keyword : this.methodElement.keywords){
			if(!keyword.equals("final"))
				keywordString += "{" + keyword + "}";
		}
		
		sb.append(keywordString + accessMod + name + "(");
		this.methodElement.generateParams(sb, diagram);
		sb.append(") : " + returnType + "\n");
	}
	
	private String getAccessSymbol(String accessMod){
		switch(accessMod){
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
