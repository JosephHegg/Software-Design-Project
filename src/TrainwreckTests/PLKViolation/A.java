package TrainwreckTests.PLKViolation;

public class A {

	
	
	B b = new B();
	
	// This should be a violation!
	public void callC(){
		C c = b.getC();
		
		//This is a violation as well because the method is being accessed
		//via a train wreck!
		c.call();
	}
}
