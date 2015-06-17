#!/bin/bash
# Runs semantic check on all right and wrong programs  inside /input/ folder 

java Main inputs/right/* > right.txt
java Main inputs/wrong/* > wrong.txt

right=$(grep Error right.txt | wc -l)
wrong=$(grep success wrong.txt | wc -l)

if ((("$right" == 0)) && (("$wrong" == 0))) then 
	echo "All tests passed"
else
	echo "Tests failed"
fi

rm right.txt wrong.txt
exit 0