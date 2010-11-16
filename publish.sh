#!/bin/sh
abspath="$(cd "${0%/*}" 2>/dev/null; echo "$PWD"/"${0##*/}")"
eclipse_root=`dirname $abspath`
project_root=`dirname $eclipse_root`
main_site=$project_root/website/site
update_site=${main_site}/experimental

# Increment Version
# DEBT Replace some of this hackery with a maven properties file
current_minor_version=`perl -nle 'while(m/(<plugin.version>)(\d\.\d\.)(\d*)(<\/plugin.version>)/g){print $2}' pom.xml`
current_point_version=`perl -nle 'while(m/(<plugin.version>)(\d\.\d\.)(\d*)(<\/plugin.version>)/g){print $3}' pom.xml`
new_version=$current_minor_version`expr $current_point_version + 1`
sed s/\<plugin.version\>$current_minor_version$current_point_version/\<plugin.version\>$new_version/g pom.xml > pom.xml.new
mv pom.xml.new pom.xml

# Move up to root of repo
cd $project_root 

# Build it!
mvn clean install -Pintegration
if [ "$?" -ne "0" ]; then
    cd $project_root
    git checkout infinitest-eclipse/pom.xml
    echo "Build Failure. Exiting..."
    exit 1
fi

echo "Please enter the newsfeed summary for this release:"
read release_message

# Prompt and update if accepted
echo "Ready to upload version $new_version to ${update_site} site with message: $release_message"
echo "Publish now?"
select fname in [p]ush,[a]bort;
do
  if [ $REPLY = "p" ]; then
    mkdir -p ${update_site}/features
    mkdir -p ${update_site}/plugins
    echo "Pushing to update site..."
    cd $project_root/infinitest-eclipse
    printf "\n\n%s    %s    %s" "`date`" ${new_version} "${release_message}" >> ReleaseNotes.txt
    ruby update_rss.rb ${new_version} "${release_message}"
    cp rss.xml ${update_site}/
    cp ReleaseNotes.txt ${update_site}/
    cp site.xml ${update_site}/
    cp target/org.infinitest.eclipse.feature_${new_version}.jar ${update_site}/features/
    cp target/org.infinitest.eclipse_${new_version}.jar ${update_site}/plugins/

    #git commit -a -m "Releasing $new_version -- $release_message"
    #git push origin master

    #cd $update_site
    #git commit -am  "Released version $new_version"
    #git push origin master
    break
  else
    echo "Aborted!"
    cd $project_root/infinitest-eclipse
    git checkout pom.xml
    break
  fi
  break;
done
