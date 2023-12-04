package Presentation;

import DataSource.AbstractParser;
import DataSource.SequenceAbstractParser;
import DataSource.SequenceParseData;

public interface SequenceArgument {
	public String getSwitch();
	public SequenceAbstractParser getParseBehavior(SequenceParseData parseData, SequenceAbstractParser parser);
	public String getDescription();
	public void setAdditionalInfo(String info);
}
