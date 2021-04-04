# GT Class Sign up Bot
 A bot that checks and signs up for classes

NOTE: Duo push needs to be done on start of the program, no way around it unless you buy a physical key
INSTRUCTIONS: Create classesWanted.txt and classesToDrop.txt in the project folder
classesWanted.txt should have this format:


<class wanted #1 (in format for example: MATH 1101 67421>
<class wanted #2>
<class wanted #3>
. . .

classesToDrop.txt should have this format (Classes to add must also be added to classesWanted.txt):
: , , . . . //this will have the effect of if Class A is open, drop Classes B and C
: //this will drop Class B2 if Class A2 is open
. . .