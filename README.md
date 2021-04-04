# GT Class Sign up Bot
 A bot that checks and signs up for classes

NOTE: Duo push needs to be done on start of the program, no way around it unless you buy a physical key

INSTRUCTIONS: Create classesWanted.txt and classesToDrop.txt in the project folder

classesWanted.txt should have this format:

<class wanted #1> (in format for example: MATH 1101 67421> </br>
<class wanted #2> </br>
<class wanted #3> </br>
. . . </br>

classesToDrop.txt should have this format (Classes to add must also be added to classesWanted.txt):

 //this will drop classes 2 and 3 after signing up for class 1</br>
<class 1 CRN> : <class 2 CRN>, <class 3 CRN> </br>
 //this will drop class 5 after signing up for class 4 </br>
<class 4 CRN> : <class 5 CRN> </br>
. . .
