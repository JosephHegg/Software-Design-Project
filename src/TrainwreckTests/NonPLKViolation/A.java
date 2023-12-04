package TrainwreckTests.NonPLKViolation;

public class A {

	
	
	B b = new B();
	
	// This isn't a PLK violation, as we
	// are calling methods on our field, b!
	public void callC() {
		b.callC();
    }
	
	

}
