package DataSource;

import Domain.Diagram;
import Domain.JavaFilter;

public class JavaFilterSequenceDecorator extends SequenceAbstractParser {
	
	private SequenceAbstractParser parser;
	
	public JavaFilterSequenceDecorator(SequenceAbstractParser parser) {
		this.parser = parser;
	}

	@Override
	public Diagram doParseBehavior(String methodSignature) {
		Diagram parsedDiagram = ((SequenceBaseParser)this.parser).doParseBehavior(methodSignature);
		//Diagram parsedDiagram = this.parser.doParseBehavior(methodSignature);
		parsedDiagram.setDiagramFilter(new JavaFilter(parsedDiagram));
		return parsedDiagram;
	}
	
	

}
