package Test;

public class DuelAdapter implements InterfaceA, InterfaceB{
	NeedsAdapting na;
	@Override
	public void doB() {
		na.do2();
	}

	@Override
	public char doA() {
		na.do1("HA");
		return 0;
	}

}
