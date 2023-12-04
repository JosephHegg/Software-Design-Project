package DataSource;

import Domain.Diagram;

public abstract class ParseDecorator extends AbstractParser {
	protected AbstractParser parser;
	
	public ParseDecorator(ASMParseData parseData, AbstractParser parser) {
		super(parseData);
		this.parser = parser;
	}
}
