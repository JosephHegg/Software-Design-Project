RUN CONFIGURATIONS & JUSTIFICATION

Data Structure Test Config

	TrainwreckTests.DataStructureTests.MyDataStructureClass.getMyArray()

	Justification: This is the test to show how we recognize a data structure.

Hybrid Structure Test Config

	TrainwreckTests.DataStructureTests.MyHybridClass.initializeData()
	
	Justification: This shows how the interpeter can recognize a hybrid, and allow for PLK violations to still be tracked.
	
Hybrid Structure Test Config

	TrainwreckTests.DataStructureTests.MyHybridClass.initializeData()
	
	Justification: This shows how the interpreter can recognize a hybrid, and allow for PLK violations to still be tracked.
	
Standard Example of NON PLK Violation Test Config

	TrainwreckTests.NonPLKViolation.A.callC()
	
	Justification: This is the in class example on the slides of how to use PLK
	
Standard Example of PLK Violation Config

	TrainwreckTests.PLKViolation.A.callC()
	
	Justification: This is the in class example of a PLK violation.
	
Example of how the method parameter comes in to effect Config

	TrainwreckTests.NonPLKViolation.MethodParameterTest.testCallParameter()
	
	Justification: Demonstrates functionality of PLK being upheld by using method params.
	
Demonstrating how calling methods on this isn't a violation Config

	TrainwreckTests.NewObjectCreationAndThisPLK.ThisTest.callAnotherMethodInThis()
	
	Justification: Demonstrates functionality of PLK being upheld by calling methods on this
	
Test on an Object Config

	TrainwreckTests.DataStructureTests.MyObjectClass.createPrivateFields()
	
	Justification: Shows how an object is determined.
	
	
	
Shows how creating an object via new doesn't violate PLK Config

	TrainwreckTests.NewObjectCreationAndThisPLK.A.callC()
	
	Justification: This upholds the rule that any object created via new isn't a PLK violation.
	
	
I believe my demos are interesting because each one serves a purpose in demonstrating how my implemented detectors addresses each of the rules,
and how it will react accordingly.

--- CODE ORGANIZATION DESCRIPTION ---

The main code is in Domain.TrainwreckDetector
It's appearance is at Domain.TrainWreckAppearance

I had to modify various aspects of ClassElement / FieldElement / MethodCallElement in order to successfully implement the detector.

I had to include extra information gathered in DataSource.InheritanceRecursion as well.

Upon researching both the BasicInterpeter and SourceInterpreter classes, I discovered that the Basic & Source value objects
they created were extremely similar to the MethodCallElement class we had created.

I decided to implement the MethodCallElement version, as it allowed for the reusability of code and data gathered in SequenceBaseParser.
 

