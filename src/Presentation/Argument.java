package Presentation;

import DataSource.ASMParseData;
import DataSource.AbstractParser;
import DataSource.ParseBehavior;

public interface Argument {

	public String getSwitch();
	public AbstractParser getParseBehavior(ASMParseData parseData, AbstractParser parser);
	public String getDescription();
	public void setAdditionalInfo(String info);
}
