package Test;

public class MyAdapter implements MyTargetInterface{
	MyAdaptee adaptee;
	@Override
	public int doA(String a) {
		adaptee.doA();
		return 0;
	}

	@Override
	public String doB(String b) {
		adaptee.doB(1);
		return null;
	}

	@Override
	public char doC() {
		adaptee.doC('A');
		return 0;
	}

}
