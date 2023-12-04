package Test;

public class Decorator {
	
	private IComponent component;
	private ConcreteComponent concreteComponent;
	private IntermediateInterface abstractDecorator;
	private ConcreteDecorator1 concrete1;
	private ConcreteDecorator2 concrete2;
	
	public Decorator() {
		this.concreteComponent = new ConcreteComponent();
		this.concrete1 = new ConcreteDecorator1();
		this.concrete2 = new ConcreteDecorator2();
	}
	
	interface IComponent {
		public void method1();
		public void method2();
	}
	
	class ConcreteComponent implements IComponent {

		@Override
		public void method1() {
			
		}

		@Override
		public void method2() {
			
		}
	}
	
	interface IntermediateInterface extends IComponent {

	}
	
	class ConcreteDecorator1 implements IntermediateInterface {
		public void method1() {
			
		}
		
		public void method2() {
			
		}
	}
	
	class ConcreteDecorator2 implements IntermediateInterface {
		private IComponent component;
		
		public void method1() {
			
		}
		
		public void method2() {
			
		}
	}
}
