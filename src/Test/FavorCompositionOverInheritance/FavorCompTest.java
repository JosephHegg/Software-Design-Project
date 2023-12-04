package Test.FavorCompositionOverInheritance;

import java.util.List;

public class FavorCompTest {
	private A a;
	private B b;
	private D d;
	private F f;
	private G g;
	private H h;
	private I i;
	private J j;

	public FavorCompTest() {
			this.a = new A();
			this.b = new B();
			this.d = new D();
			this.f = new F();
			this.g = new G();
			this.h = new H();
			this.i = new I();
			this.j = new J();
		}

	private class A {

	}

	private class B extends A {
		private A a;
	}

	private abstract class C {
		abstract void myMethod1();

		abstract void myMethod2();
	}

	private class D extends C {
		void myMethod1() {

		}

		void myMethod2() {

		}
	}

	private abstract class E {
		abstract void myMethod1();

		void myMethod2() {

		}
	}

	private class F extends E {
		void myMethod1() {

		}
	}

	private class G {
		void myMethod1() {

		}

		List<A> myMethod2() {
			return null;
		}
	}

	private class H extends G {

	}
	
	private class I {
		void method1() {
			
		}
		
		void method2() {
			
		}
		
	}
	
	private class J extends I {
		void method1() {
			
		}
	}
}
