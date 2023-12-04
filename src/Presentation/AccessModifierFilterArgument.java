package Presentation;

import DataSource.ASMParseData;
import DataSource.AbstractParser;
import DataSource.ParseBehavior;
import DataSource.PrivateParse;
import DataSource.ProtectedParse;
import DataSource.PublicParse;

public class AccessModifierFilterArgument implements Argument {

	private String accessLevel;
	
	@Override
	public String getSwitch() {
		return "f";
	}

	@Override
	public AbstractParser getParseBehavior(ASMParseData parseData, AbstractParser parser) {
		switch(this.accessLevel) {
			case "public":
				return new PublicParse(parseData, parser); 
			case "protected":
				return new ProtectedParse(parseData, parser);
			default:
				return new PrivateParse(parseData, parser); //Private
		}
	}

	@Override
	public String getDescription() {
		return "Only render classes, fields, methods, etc at " + this.accessLevel + " level.";
	}

	@Override
	public void setAdditionalInfo(String info) {
		this.accessLevel = info;
	}

}
