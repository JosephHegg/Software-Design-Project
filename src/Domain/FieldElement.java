package Domain;

import java.util.List;

public class FieldElement extends DiagramElement {
	
	public List<String> keywords;
	public String type;
	public String accessModifier;
	public List<String> innerTypes = null;
	
	public FieldElement(Appearance appearance, String name, String type, String accessModifier, List<String> keywords){
		super(appearance, name);
		this.type = type;
		this.accessModifier = accessModifier;
		this.keywords = keywords;
	}
	
	// Needed for data structures that have inner parts --> Map<Integer, String>
	public FieldElement(Appearance appearance, String name, String type, List<String> innerTypes, String accessModifier, List<String> keywords){
		super(appearance, name);
		this.type = type;
		this.accessModifier = accessModifier;
		this.keywords = keywords;
		this.innerTypes = innerTypes;
	}
}
