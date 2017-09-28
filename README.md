# SimProgeny
A flexible forward simulation framework to model a variety of study populations and sampling approaches to estimate the amount of relatedness researchers should expect in a genetic cohort


========================================================================================

Copyright (c) 2017 Regeneron Genetics Center LLC
 
License:
Permission is hereby granted, free of charge, by Regeneron Genetics Center LLC (the “Licensor”) to any person obtaining a copy of this software (“you”) and any associated documentation files (the "Software"), to use, publish, copy, distribute or redistribute this material in any medium or format, and to adapt the Software ("Adaptations").
 
You may publish the use of or distribute the Software or Adaptations, but you must give appropriate credit to Licensor by citing the following publication:  
INSERT CITATION TO MANUSCRIPT
 
Your citation of that publication does not in any way suggest that the Licensor endorses you or your use.
 
This license is subject to the following conditions:
(i) the above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software or Adaptations;
(ii) you are not permitted to sublicense, and/or sell copies of the Software or Adaptations, and are not permitted to permit persons to whom the Software or Adaptations are furnished to sublicense, and/or sell copies of the Software or Adaptations;
(iii) you may not apply legal terms or technological measures that restrict others from doing anything this license permits; and
(iv) you are not permitted to use, publish, copy, distribute or redistribute the Software or Adaptations in a manner that is intended for or directed toward commercial advantage or monetary compensation.
 
Note that this license may not give you all of the permissions necessary for your intended use.  For example, other rights, such as publicity, privacy or moral rights, may limit how you use the material.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT REPRESENTATION OR WARRANTY OF ANY KIND, EXPRESS, IMPLIED, STATUTORY OR OTHERWISE, INCLUDING BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT, AND LICENSOR EXPRESSLY DISCLAIMS ALL SUCH REPRESENTATION AND WARRANTIES.
 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR LICENSOR BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR ADAPTATIONS OR THE USE IN THE SOFTWARE OR ADAPTATIONS.

=======================================================================================
 
Adjust the provided pops.txt file or create your own in the same format. Adjsut the config_file.txt or create your own in same parameter names and in the same format. We recommend copying the provided file to a new file and making the edits there.

Compile the code:

> javac SimProgeny.java 

Run the code with example data:

> java -Xmx6g SimProgeny pops.txt config_file.txt




