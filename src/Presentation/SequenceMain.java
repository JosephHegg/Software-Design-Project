package Presentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DataSource.ASM;
import DataSource.ASMBaseParser;
import DataSource.ASMParseData;
import DataSource.AbstractParser;
import DataSource.FileHandler;
import DataSource.JavaFilterSequenceDecorator;
import DataSource.SequenceAbstractParser;
import DataSource.SequenceBaseParser;
import DataSource.SequenceParseData;
import Domain.DependencyInversionViolationAnalyzer;
import Domain.Diagram;
import Domain.TrainwreckDetector;

public class SequenceMain {
	private List<SequenceArgument> arguments;
	private FrontEnd frontEnd;
	private FileHandler fileHandler = new FileHandler();

	public static void main(String[] args) {
		SequenceMain main = new SequenceMain(args);
	}

	public SequenceMain(String[] args) {
		this.arguments = new ArrayList<SequenceArgument>();
		this.arguments.add(new RecursiveSequenceArgument());
		this.arguments.add(new NoJavaArgument());

		this.frontEnd = new Console();
		this.parseArgs(args);
	}

	private void parseArgs(String[] args) {
		List<String> argumentSwitches = new ArrayList<>();
		String methodSignature = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				argumentSwitches.add(args[i].substring(1));
			} else {
				methodSignature = args[i];
			}
		}

		Set<SequenceArgument> parseArguments = new HashSet<>();

		for (int i = 0; i < arguments.size(); i++) {
			for (int j = 0; j < argumentSwitches.size(); j++) {
				if (argumentSwitches.get(j).contains(":")) {
					String additionalInfo = argumentSwitches.get(j).substring(argumentSwitches.get(j).indexOf(":") + 1);
					arguments.get(i).setAdditionalInfo(additionalInfo);
				}

				String switchWithoutAdditionalInfo = argumentSwitches.get(j);
				if (argumentSwitches.get(j).contains(":"))
					switchWithoutAdditionalInfo = argumentSwitches.get(j).substring(0,
							argumentSwitches.get(j).indexOf(":"));

				if (arguments.get(i).getSwitch().equals(switchWithoutAdditionalInfo)) {
					parseArguments.add(this.arguments.get(i));
				}
			}
		}

		SequenceParseData parseData = new SequenceParseData(new ASM());
		SequenceAbstractParser sequenceParser = new SequenceBaseParser(parseData);

		for (SequenceArgument parseArg : parseArguments) {
			sequenceParser = parseArg.getParseBehavior(parseData, sequenceParser);
		}

		doParse(sequenceParser, methodSignature);
	}

	public void doParse(SequenceAbstractParser sequenceParser, String methodSig) {
		//Diagram diagram = ((JavaFilterSequenceDecorator) sequenceParser).doParseBehavior(methodSig);
		
		Diagram diagram = sequenceParser.doParseBehavior(methodSig);

		DependencyInversionViolationAnalyzer violationChecker = new DependencyInversionViolationAnalyzer(diagram);
		TrainwreckDetector trainwreckDetector = new TrainwreckDetector(diagram, new ASM());

		violationChecker.analyze();
		trainwreckDetector.analyze();

		StringBuilder sb = new StringBuilder();

		diagram.generate(sb);

		fileHandler.writeToFile("./docs/OutputSyntaxFile", sb);

	}
}
