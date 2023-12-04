package Test;


public class AdapterTest {
	target1 myA;
	target2 myB;
	myThing things;
	IH ih;
	H h;
	HAdapter hadapter;
	J j;
	JAdapter jadapter;
	JAdaptee jadaptee;
	otherAdaptee otherAdapp;
	Adaptee manatee;
	Adapter raptor;
	
	interface target1 {
		void myMethod1();
	}
	
	interface target2 {
		void myMethod2();
	}
	
	class Adaptee {
		void doOtherMethod() {
			
		}
		
		void doOtherMethod2() {
			
		}
	}
	
	class myThing {
		void doOtherMethod() {
			
		}
		
		void doOtherMethod2() {
			
		}
	}
	
	class Adapter implements target1, target2 {
		Adaptee a = new Adaptee();
		myThing thing = new myThing();
		
		@Override
		public void myMethod1() {
			a.doOtherMethod();
			thing.doOtherMethod();
		}

		@Override
		public void myMethod2() {
			a.doOtherMethod2();
			thing.doOtherMethod2();
		}
		
	}
	
	
	abstract class IH {
		abstract void myMethod1();
		void myMethod2() {
			
		}
	}
	class HAdapter extends IH{
		H h;
		void myMethod1() {
			h.do1();
		}
	}
	class H {
		void do1() {}
		void do2() {}
	}
	
	
	abstract class J {
		abstract void do1();
		abstract void do2();
	}
	
	class JAdapter extends J {
		JAdaptee j;
		otherAdaptee o;
		@Override
		void do1() {
			j.doOther1();
			o.doOther1();
		}

		@Override
		void do2() {
			j.doOther2();
		}	
	}
	class JAdaptee {
		void doOther1() {}
		void doOther2() {}
	}
	
	class otherAdaptee {
		void doOther1() {}
		void doOther2() {}
	}
	
}
