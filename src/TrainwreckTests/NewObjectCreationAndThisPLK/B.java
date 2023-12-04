package TrainwreckTests.NewObjectCreationAndThisPLK;

public class B {

	private C c = new C();

	public void callC() {
		c.call();
	}

	public C getC() {
		return c;
	}
}
