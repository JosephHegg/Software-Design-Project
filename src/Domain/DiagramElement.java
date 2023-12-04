package Domain;

public abstract class DiagramElement {
	public Appearance appearance;
	protected Generator generator;
	public String name;
	
	public DiagramElement(Appearance appearance, String name) {
		this.appearance = appearance;
		this.name = name;
	}
	public void setAppearance(Appearance appearance){
		this.appearance = appearance;
	}
	
	public void setGenerator(Generator generator){
		this.generator = generator;
	}
	
	public void generate(StringBuilder sb, Diagram diagram){
		this.generator.generate(appearance, sb, diagram);
	}
}
