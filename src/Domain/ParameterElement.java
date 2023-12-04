package Domain;

import java.util.List;

public class ParameterElement extends DiagramElement{
	public List<String> keywords;
	public String type;
	
	public ParameterElement(Appearance appearance, String name, String type, List<String> keywords){
		super(appearance, name);
		this.type = type;
		this.keywords = keywords;
	}
}
