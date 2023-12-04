package Presentation;

import Domain.Appearance;
import Domain.Diagram;
import Domain.DiagramElement;
import Domain.Generator;
import Domain.ParameterElement;

public class ParameterGenerator implements Generator {

	private ParameterElement parameterElement;
	
	public ParameterGenerator(ParameterElement pe){
		this.parameterElement = pe;
	}
	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		String name = this.parameterElement.name;
		String type = this.parameterElement.type;
		String keywordList = "";
		
		for(String keyword : this.parameterElement.keywords)
			keywordList += keyword + " ";
		
		boolean noKeywords = this.parameterElement.keywords.isEmpty();
		boolean noName = name.equals("");
		
		if(noKeywords && noName)
			sb.append(type);
		else if(noKeywords)
			sb.append(name + " : " + type);
		else if(noName)
			sb.append(keywordList + " : " + type);
		else
			sb.append(keywordList + name + " : " + type);
	}

}
