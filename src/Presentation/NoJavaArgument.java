package Presentation;

import DataSource.JavaFilterSequenceDecorator;
import DataSource.SequenceAbstractParser;
import DataSource.SequenceParseData;

public class NoJavaArgument implements SequenceArgument {

	@Override
	public String getSwitch() {
		return "nojava";
	}

	@Override
	public SequenceAbstractParser getParseBehavior(SequenceParseData parseData, SequenceAbstractParser parser) {
		return new JavaFilterSequenceDecorator(parser);
	}

	@Override
	public String getDescription() {
		return "Filters calls to methods on Java objects out of the generated sequence Diagram";
	}

	@Override
	public void setAdditionalInfo(String info) {
		return;
	}
}
