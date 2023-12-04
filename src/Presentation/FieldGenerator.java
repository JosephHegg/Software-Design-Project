package Presentation;


import Domain.Appearance;
import Domain.Diagram;
import Domain.DiagramElement;
import Domain.FieldElement;
import Domain.Generator;

public class FieldGenerator implements Generator {

	private FieldElement fieldElement;

	public FieldGenerator(FieldElement fieldElement) {
		this.fieldElement = fieldElement;
	}

	@Override
	public void generate(Appearance appearance, StringBuilder sb, Diagram diagram) {
		String type = this.fieldElement.type;
		String name = this.fieldElement.name;
		String accessMod = getAccessSymbol(this.fieldElement.accessModifier);
		String keywordString = "";

		for (String keyword : this.fieldElement.keywords)
			if (!keyword.equals("final"))
				keywordString += "{" + keyword + "}";

		sb.append(keywordString + accessMod + name + " : " + type + "\n");

	}

	private String getAccessSymbol(String accessMod) {
		switch (accessMod) {
		case "public":
			return "+";
		case "private":
			return "-";
		case "protected":
			return "#";
		default:
			return "~";

		}
	}

}
