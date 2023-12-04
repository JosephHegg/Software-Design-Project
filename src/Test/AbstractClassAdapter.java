package Test;

public class AbstractClassAdapter extends MyAbstractClass{
	MyAdaptee ma;

	@Override
	void method1() {
		ma.doA();
	}
	
	
}
