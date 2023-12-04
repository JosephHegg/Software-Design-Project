package DataSource;

import java.util.ArrayList;
import java.util.List;

import Domain.ClassElement;
import Domain.Diagram;
import Domain.FieldElement;
import Domain.InterfaceElement;
import Domain.MethodElement;
import Domain.PublicComparator;
import Domain.AccessModifierFilter;

public class PublicParse extends ParseDecorator {

	public PublicParse(ASMParseData parseData, AbstractParser parser) {
		super(parseData, parser);
	}

	@Override
	public Diagram doParseBehavior() {
		Diagram parsedDiagram = this.parser.doParseBehavior();
		parsedDiagram.setDiagramFilter(new AccessModifierFilter(new PublicComparator()));
		return parsedDiagram;
	}


}
