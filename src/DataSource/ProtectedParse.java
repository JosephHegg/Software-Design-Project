package DataSource;

import java.util.ArrayList;
import java.util.List;

import Domain.AccessModifierFilter;
import Domain.ClassElement;
import Domain.Diagram;
import Domain.FieldElement;
import Domain.InterfaceElement;
import Domain.MethodElement;
import Domain.ProtectedComparator;

public class ProtectedParse extends ParseDecorator {

	public ProtectedParse(ASMParseData parseData, AbstractParser parser) {
		super(parseData, parser);
	}

	@Override
	public Diagram doParseBehavior() {
		Diagram parsedDiagram = this.parser.doParseBehavior();
		parsedDiagram.setDiagramFilter(new AccessModifierFilter(new ProtectedComparator()));
		return parsedDiagram;
	}

	
}
