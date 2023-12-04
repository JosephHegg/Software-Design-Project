package Domain;

public abstract class Appearance {

	public String color; // this will be changed
	
	public Appearance(String color){
		this.color = color;
	}
	
	public Appearance(){
		this.color = "FireBrick";
	}
}
