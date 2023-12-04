package DataSource;

import java.util.List;
import java.util.Map;

import Domain.AccessModifierFilter;
import Domain.Appearance;
import Domain.Diagram;
import Domain.PrivateComparator;
import Domain.PublicComparator;

public class PrivateParse extends ParseDecorator {

	public PrivateParse(ASMParseData parseData, AbstractParser parser) {
		super(parseData, parser);
	}

	@Override
	public Diagram doParseBehavior() {
		Diagram parsedDiagram = this.parser.doParseBehavior();
		parsedDiagram.setDiagramFilter(new AccessModifierFilter(new PrivateComparator()));
		return parsedDiagram;
	}
}
