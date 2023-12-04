package Presentation;

import Domain.Appearance;
import Domain.Diagram;
import Domain.Generator;

public class DoNothingGenerator implements Generator {

	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		//Do nothing
	}

}
