package Domain;

import java.util.ArrayList;
import java.util.List;

public class MethodCallElement extends DiagramElement {

	public String callee;
	public String caller;
	public String returnType;
	public List<MethodCallElement> methodCalls;
	public ClassElement ownerClassElement;
	public List<ClassElement> superClasses;
	public List<InterfaceElement> implementedInterfaces;
	public List<String> methodParams;
	public boolean isAbstract = false;
	
	public MethodCallElement(Appearance appearance, String methodSignature, String callee, String caller, String returnType, List<String> methodParams) {
		super(appearance, methodSignature);
		this.callee = callee;
		this.caller = caller;
		this.returnType = returnType;
		this.methodParams = methodParams;
	}

	public void setMethodCallElements(List<MethodCallElement> methodCallElements) {
		this.methodCalls = methodCallElements;
	}
	
	public void setSuperClasses(List<ClassElement> superClasses) {
		this.superClasses = superClasses;
	}
	
	public void setOwnerClass(ClassElement owner){
		this.ownerClassElement = owner;
	}
	public void setImplementedInterfaces(List<InterfaceElement> implementedInterfaces) {
		this.implementedInterfaces = implementedInterfaces;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

}
