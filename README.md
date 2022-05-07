# grepy
A grep alternative. 

Can recognize these special symbols: <br />
\+ (the OR operator) <br />
\* (the Kleene Star) <br />
() (Brackets) <br />

Go to: grepy > src > main/java/com/grepy <br />
App.java is the main file containing all the main programs. <br />
FiveTuple.java contains the frame for the NFA Five Tuple and DFA.java contains the frame for the DFA Five Tuple.<br />

Go to: grepy > src > test/java/com/grepy <br /> 
alphaTest.java contains the file with the text strings

NOTE: (aa+ab) will be processed as a(a+a)b and not (aa)+(ab) <br />
NOTE: Might contain a portal to the multiverse

