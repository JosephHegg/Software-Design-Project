# 374ProjectS1G1

Project repo for our 374 project!

Milestone Contributions
_______________________
All team members strived to contribute equally to our code base. We took turns pair-programming, trying to split the work evenly between turns.
All team members had a hand in designing the system.
All team members met with Dr. Hays if they were available.
All team members worked on the Domain Model, Sequence Diagram, and Class Diagram. However,
Joey Hegg spent more time than his team-mates on the Sequence Diagram for this milestone.

Milestone 1
_______________

How to run and install our design!
1. Import our Eclipse project to your workspace
2. Use your RunConfiguratons to input arguments to our program
3. Make sure the correct main class is selected
--> Possible arguments to program: -p:[private:public:protected],  renders only classes with the provided accessModifier
----> E.x. "-f:public java.lang.String" 
--> Additional Argument: -r, recursively renders superclasses and interfaces of provided classes until it can longer
----> find any. E.x. "-r javax.swing.JComponent"
Once the program has been run, this will output PlantUML syntax to "OutputSyntaxFile".
From here, navigate to the directory where your plantuml.jar file is stored and run the following
command: "java -jar plantuml.jar OutputSyntaxFile -tsvg"

This will render an svg image in that directory. Open it to see your diagram.


_________________

- Discussion as a group
- Andrew White - Domain Model 
- Joey Hegg - Class Diagram 
- Derek Grayless - Sequence Diagram