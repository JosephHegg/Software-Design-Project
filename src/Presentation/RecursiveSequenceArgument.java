package Presentation;

import DataSource.AbstractParser;
import DataSource.SequenceAbstractParser;
import DataSource.SequenceParseData;

public class RecursiveSequenceArgument implements SequenceArgument{
	int additionalInfo = 0;
	
	@Override
	public String getSwitch() {
		return "r";
	}
	
	public SequenceAbstractParser getParseBehavior(SequenceParseData parseData, SequenceAbstractParser parser) {
		parseData.recursiveDepth = this.additionalInfo;
		return parser;
	}

	@Override
	public String getDescription() {
		return "Recursively parses method calls in a sequence diagram";
	}

	@Override
	public void setAdditionalInfo(String info) {
		try {
			this.additionalInfo = Integer.parseInt(info);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
