package Domain;

import java.util.List;

public class DependencyInversionViolationAnalyzer {
	
	private Diagram diagram;
	
	public DependencyInversionViolationAnalyzer(Diagram diagram) {
		this.diagram = diagram;
	}
	
	public void analyze() {
		List<MethodCallElement> methodCalls = this.diagram.findDiagramElements(MethodCallElement.class);
		
		for(MethodCallElement methodCall : methodCalls) {
			recursivelyCheckMethodCall(methodCall);
		}
	}
	
	private void recursivelyCheckMethodCall(MethodCallElement methodCall){
		checkDependencyInversionViolation(methodCall);
		for(MethodCallElement childCall : methodCall.methodCalls){
			recursivelyCheckMethodCall(childCall);
		}
	}

	private void checkDependencyInversionViolation(MethodCallElement methodCall) {
		checkSuperClassesForViolation(methodCall);
		checkImplementedInterfacesForViolation(methodCall);
	}

	private void checkImplementedInterfacesForViolation(MethodCallElement methodCall) {
		for(ClassElement superClass : methodCall.superClasses) {
			List<MethodElement> superClassMethods = superClass.methods;
			for(MethodElement method : superClassMethods) {
				List<ParameterElement> methodParameters = method.params;
				if(this.getMethodNameFromMethodSignature(methodCall.name).equals(method.name))
					if(!methodCall.isAbstract)
						findViolation(methodCall, methodParameters);
			}
		}
	}
	
	private void checkSuperClassesForViolation(MethodCallElement methodCall) {
		for(InterfaceElement implementedInterface : methodCall.implementedInterfaces) {
			List<MethodElement> interfaceMethods = implementedInterface.methods;
			for(MethodElement method : interfaceMethods) {
				List<ParameterElement> methodParameters = method.params;
				if(this.getMethodNameFromMethodSignature(methodCall.name).equals(method.name))
					if(!methodCall.isAbstract)
						findViolation(methodCall, methodParameters);
			}
		}
	}

	private void findViolation(MethodCallElement methodCall, List<ParameterElement> methodParameters) {
		if(methodCall.methodParams.size() != methodParameters.size()) {
			return;
		}
		
		List<String> methodCallParameterNames = methodCall.methodParams;
		int parameterIndex = 0;
		
		while(parameterIndex != methodParameters.size()) {
			ParameterElement parameter = (ParameterElement) methodParameters.get(parameterIndex);
			
			boolean sameParameter = methodCallParameterNames.get(parameterIndex).equals(parameter.type);
			if(!sameParameter) {
				return;
			}
		
			parameterIndex++;
		}
		
		String methodName = this.getMethodNameFromMethodSignature(methodCall.name);
		if(!methodName.equals("<init>"))
			setViolationAppearance(methodCall);
	}

	private void setViolationAppearance(DiagramElement methodCall) {
		methodCall.setAppearance(new DependencyInversionViolationAppearance("orange"));
	}
	
	public String getMethodNameFromMethodSignature(String methodSignature) {
		int parenthIndex = methodSignature.indexOf('(');
		String toParentheses = methodSignature.substring(0, parenthIndex);
		int index = toParentheses.lastIndexOf('.');
		
		return methodSignature.substring(index + 1, parenthIndex);

	}
}
