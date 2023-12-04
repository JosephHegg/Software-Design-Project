package Presentation;

import Domain.Appearance;
import Domain.Diagram;
import Domain.Generator;
import Domain.MethodCallElement;

public class MethodCallGenerator implements Generator {
	private MethodCallElement methodCallElement;
	private boolean hasGenerated = false;

	public MethodCallGenerator(MethodCallElement methodCallElement) {
		this.methodCallElement = methodCallElement;
	}

	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		
		if (methodCallElement.callee.equals("NONE")) {
			sb.append("autoactivate on\n");
			sb.append("[-" + "[#" + methodCallElement.appearance.color + "]" + "> " + "\":" + methodCallElement.caller + "\"" + " : " + methodCallElement.name + "\n");
			
			callInnerMethodCalls(sb, diagram);
			
			sb.append("[<-- " + "\":" + methodCallElement.caller + "\"" + ": " + methodCallElement.returnType + "\n");
		} else {
			sb.append("\":" + methodCallElement.caller + "\"");
			sb.append(" -" + "[#" + methodCallElement.appearance.color + "]" + "> ");
			sb.append("\":" + methodCallElement.callee + "\"");
			
			sb.append(": " + methodCallElement.name + "\n");

			callInnerMethodCalls(sb, diagram);
			
			sb.append("\":" + methodCallElement.callee + "\"");
			sb.append(" --> ");
			sb.append("\":" + methodCallElement.caller + "\"");
			sb.append(": " + methodCallElement.returnType + "\n");
		}
		
		hasGenerated = true;
	}

	private void callInnerMethodCalls(StringBuilder sb, Diagram diagram) {
		for(MethodCallElement methodCall : methodCallElement.methodCalls) {
			methodCall.generate(sb, diagram);
		}
	}
}
