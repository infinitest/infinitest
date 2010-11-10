#!/bin/bash

function alert_user {
	echo "${1}"
	if [ ! -z `which growlnotify` ]; then
		growlnotify `basename $0` -m "${1}"
	fi	
}

function exit_ko {
	alert_user "${1}"; exit 1
}

function exit_ok {
	alert_user "${1}"; exit 0
}

BRANCH=$(git branch --no-color | awk '$1=="*" {print $2}')
ORIGIN=$(git remote -v | awk '$1=="origin" && $3=="(push)" {print $2}')

# Git black magic to pull rebase even with uncommited work in progress
git fetch
git add -A; git ls-files --deleted -z | xargs -0 -I {} git rm {}; git commit -m "wip"
git rebase origin/${BRANCH}

if [ "$?" -ne 0 ]
then
	git rebase --abort
	git log -n 1 | grep -q -c "wip" && git reset HEAD~1
	exit_ko "Unable to rebase. please pull or rebase and fix conflicts manually."
fi
git log -n 1 | grep -q -c "wip" && git reset HEAD~1

# Private build
rm -Rf .privatebuild
git clone -slb "${BRANCH}" . .privatebuild
cd .privatebuild

# Build with maven
mvn install
if [ $? -ne 0 ]; then
	exit_ko "Unable to build"
fi

# Push
git push $ORIGIN $BRANCH
if [ $? -ne 0 ]; then
	exit_ko "Unable to push"
fi

cd .. && git fetch
exit_ok "Yet another successful push!"
