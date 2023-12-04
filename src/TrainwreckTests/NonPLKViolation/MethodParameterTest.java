package TrainwreckTests.NonPLKViolation;

public class MethodParameterTest {

	B b = new B();
	
	public void testCallParameter(){
		C c = b.getC();
		
		callOnC(c);
	}

	private void callOnC(C c) {
		
		c.call();
	}
}
