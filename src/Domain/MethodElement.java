package Domain;

import java.util.ArrayList;
import java.util.List;

public class MethodElement extends DiagramElement{
	
	public String returnType;
	public String accessModifier;
	public List<String> keywords;
	
	public List<ParameterElement> params;
	public List<DiagramElement> methodOwners = new ArrayList<>();
	
	public MethodElement(Appearance appearance, String name, String returnType, String accessModifier, List<String> keywords, List<ParameterElement> params) {
		super(appearance, name);
		this.returnType = returnType;
		this.accessModifier = accessModifier;
		this.keywords = keywords;
		this.params = params;
	}
	
	public void generateParams(StringBuilder sb, Diagram diagram){
		for(int i = 0; i < params.size(); i++){
			params.get(i).generate(sb, diagram);
			if(i != params.size() -1)
				sb.append(", ");
		}
	}
	
	public void setMethodOwners(List<DiagramElement> methodOwners) {
		this.methodOwners = methodOwners;
	}
}
