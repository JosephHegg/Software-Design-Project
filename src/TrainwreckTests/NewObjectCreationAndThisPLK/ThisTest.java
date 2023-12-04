package TrainwreckTests.NewObjectCreationAndThisPLK;

public class ThisTest {

	public void callAnotherMethodInThis(){
		this.add2(2);
	}
	
	// It should be noted, that by introducing print statements
	// java/io/printstream is introduced, and is therefore a violation of PLK
	// as we don't have access to that class directly as a field or this
	
	public int add2(int two){
		return 2 + two;
	}
	
	// This is demonstrated in this method, if you wish to see it, replace the above with
	// this version.
	
	/*
	public int add2(int two){
		System.out.println("This will cause a violation.");
	}
	*/
}
