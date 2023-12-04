package Domain;

public class RelationAnnotation {
	public String fromClass;
	public String message;
	public String arrowType;
	
	public RelationAnnotation(String fromClass, String message, String arrowType) {
		this.fromClass = fromClass;
		this.message = message;
		this.arrowType = arrowType;
	}
}
