# AbstractedDefinitionsGenerator
In the commit: cycles between existential restrictions are detected, equivalences too

By cycles I mean things like: r some A <= r some A, r some A in the RHS is removed. 

These cycles are result of normalising equivalent axioms: e.g., A == B -> A <= B, B <= A.

By equivalences I mean things like: r some A == r some B, r some A and r some B are both regarded as required restrictions in the abstracted definition. (need to check) 
