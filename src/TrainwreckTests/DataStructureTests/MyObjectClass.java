package TrainwreckTests.DataStructureTests;

import Test.FavorCompositionOverInheritance.A;

public class MyObjectClass {

	
	// obviously not a data structure
	
	private int myPrivateInt;
	private String myPrivateString;
	private double myPrivateDouble;
	private A myPrivateA;
	
	public void createPrivateFields(){
		this.myPrivateInt = 1;
		this.myPrivateString = "hello";
		this.myPrivateDouble = 2.0;
		this.myPrivateA = new A();
	}
}
