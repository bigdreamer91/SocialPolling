#!/bin/bash

echo This script will create/overwrite .git/hooks/commit-msg hook, do you want to go ahead? Y/N

read yesno

if [[ $yesno != 'Y'  &&  $yesno != 'y' ]]; then 
	echo aborted!
	exit -1 
fi

cp -f ./agilefant_commit-msg ./.git/hooks/commit-msg

chmod 755 ./.git/hooks/commit-msg

echo commit hook copied!
