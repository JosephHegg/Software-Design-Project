package DataSource;

import Domain.Diagram;

public abstract class AbstractParser {
	protected ASMParseData parseData;
	
	public AbstractParser(ASMParseData parseData) {
		this.parseData = parseData;
	}
	
	abstract public Diagram doParseBehavior();
}
