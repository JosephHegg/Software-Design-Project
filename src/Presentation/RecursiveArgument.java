package Presentation;

import DataSource.ASMParseData;
import DataSource.AbstractParser;
import DataSource.ParseBehavior;
import DataSource.RecursiveParse;

public class RecursiveArgument implements Argument {

	int additionalInfo = 0;

	@Override
	public String getSwitch() {
		return "r";
	}

	@Override
	public AbstractParser getParseBehavior(ASMParseData parseData, AbstractParser parser) {
		parseData.recursiveDepth = this.additionalInfo;
		return new RecursiveParse(parseData, parser);
	}

	@Override
	public String getDescription() {
		return "Parses classes below it recursively.";
	}

	@Override
	public void setAdditionalInfo(String info) {
		try {
			this.additionalInfo = Integer.parseInt(info);
		} catch (NumberFormatException e) {
			
			
		}
	}
}
