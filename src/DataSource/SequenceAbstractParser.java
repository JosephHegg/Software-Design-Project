package DataSource;

import Domain.Diagram;

public abstract class SequenceAbstractParser {
	public abstract Diagram doParseBehavior(String methodSignature);
}
