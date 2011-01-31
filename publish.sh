#!/bin/sh
abspath="$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"
project_root=`dirname ${abspath}`
main_site=${project_root}/../infinitest.github.com
update_site=${main_site}/experimental

# Test for web project existence
if [ ! -e ${main_site} ]; then
	echo "You should clone infinitest.github.com project at the same level as infinitest project."
	exit 1
fi

# Clean working directory
rm -Rf .privatebuild > /dev/null
cd ${project_root}
git stash save --quiet "Before publish"
git clean -xdf

# Ask for version summary
echo "Please enter the newsfeed summary for this release:"
read release_message

# Increment Version
CURRENT=`grep "<version>" -i pom.xml --max-count 1 | sed -e "s/.*<version>\(.*\)<\/version>/\1/" | sed -e "s/-SNAPSHOT//"`
MAJOR_VERSION=`echo $CURRENT | sed -e "s/\([0-9]*\.[0-9]*\.\)\([0-9]*\)/\1/"`
MINOR_VERSION=`echo $CURRENT | sed -e "s/\([0-9]*\.[0-9]*\.\)\([0-9]*\)/\2/"`
NEXT=$MAJOR_VERSION`expr $MINOR_VERSION + 1`-SNAPSHOT
TAG=VERSION${CURRENT}

find . -name "*.xml" | xargs perl -pi -e "s/${CURRENT}-SNAPSHOT/${NEXT}/g"
git stash save "Preparing ${NEXT}"
find . -name "*.xml" | xargs perl -pi -e "s/${CURRENT}-SNAPSHOT/${CURRENT}/g"

# Commit main project
git commit -am "${release_message}"
git tag -f "$TAG" -m "$TAG"

# Build it!
mvn clean install -Pintegration
if [ "$?" -ne 0 ]; then
	echo "ERROR> Maven build failed aborting."
	git reset --hard HEAD~1
	git tag -d "$TAG"
	exit 1
fi

# Prepare next version
git stash pop --quiet
git checkout --theirs "*.xml"
git commit -am "${NEXT}"

# Publish web site
cd ${main_site}
git clean -df
cp -r ${project_root}/infinitest-eclipse/target/update_site/* ${update_site}
printf "\n\n%s    %s    %s" "`date`" ${CURRENT} "${release_message}" >> ${update_site}/ReleaseNotes.txt

cd  ${update_site}
ruby update_rss.rb ${CURRENT} "${release_message}"
rm update_rss.rb

cd ${main_site}
git add .
git commit -am "${release_message}"
