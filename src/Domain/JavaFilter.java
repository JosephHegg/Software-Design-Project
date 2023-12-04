package Domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaFilter implements DiagramFilter {
	private Set<String> noParseNames = new HashSet<>();
	private Diagram diagram;
	
	public JavaFilter(Diagram diagram) {
		buildNoParseNames();
		this.diagram = diagram;
	}

	private void buildNoParseNames() {
		noParseNames.add("java");
		noParseNames.add("javax");
		noParseNames.add("sun");
	}

	@Override
	public void filterDiagram(List<DiagramElement> diagramElements) {
		List<MethodCallElement> methodCallsToRemove = new ArrayList<>();
		
		methodCallsToRemove = removeMethodCallElements(diagram.findDiagramElements(MethodCallElement.class));
		
		diagramElements.remove(methodCallsToRemove);
	}
	
	private List<MethodCallElement> removeMethodCallElements(List<MethodCallElement> methodCallsToCheck) {
		List<MethodCallElement> callsToRemove = new ArrayList<>();
		
		for(MethodCallElement methodCall : methodCallsToCheck) {
			if(invalidParseName(methodCall.callee)){
				callsToRemove.add(methodCall);
			}
			
			List<MethodCallElement> toRemove = new ArrayList<>();
			toRemove = removeMethodCallElements(methodCall.methodCalls);
			methodCall.methodCalls.removeAll(toRemove);
		}
		
		return callsToRemove;
	}

	private boolean invalidParseName(String className) {
		for(String badName : noParseNames) {
			if(className.startsWith(badName)) {
				return true;
			}
				
		}
		return false;
	}
}
