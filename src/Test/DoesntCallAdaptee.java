package Test;

public class DoesntCallAdaptee implements MyTargetInterface{
	MyAdaptee ma;
	@Override
	public int doA(String a) {
		ma.doA();
		return 0;
	}

	@Override
	public String doB(String b) {
		ma.doB(0);
		return null;
	}

	@Override
	public char doC() {
		
		return 0;
	}

}
