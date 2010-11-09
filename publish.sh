#!/bin/sh
MAIN_SITE=eclipse.infinitest.org:/var/www/html
EXPERIMENTAL_SITE=${MAIN_SITE}/experimental
PLUGIN_NAME="Infinitest"
    
update_site=$EXPERIMENTAL_SITE

# Increment Version
# DEBT Replace some of this hackery with a maven properties file
current_minor_version=`perl -nle 'while(m/(<plugin.version>)(\d\.\d\.)(\d*)(<\/plugin.version>)/g){print $2}' pom.xml`
current_point_version=`perl -nle 'while(m/(<plugin.version>)(\d\.\d\.)(\d*)(<\/plugin.version>)/g){print $3}' pom.xml`
new_version=$current_minor_version`expr $current_point_version + 1`
sed s/\<plugin.version\>$current_minor_version$current_point_version/\<plugin.version\>$new_version/g pom.xml > pom.xml.new
mv pom.xml.new pom.xml

# Move up to root of repo
cd ..

# Get up to date
git pull

# Build it!
mvn clean install -o -Dplugin.name="${PLUGIN_NAME}" -Pintegration
if [ "$?" -ne "0" ]; then
    git checkout infinitest-eclipse/pom.xml
    exit 1
fi

# FIXME Additional integration tests here!

echo "Please enter the newsfeed summary for this release:"
read release_message

# Prompt and update if accepted
echo "Ready to upload ${PLUGIN_NAME} version $new_version to ${update_site} site with message: $release_message"
echo "Publish now?"
select fname in [p]ush,[a]bort;
do
  if [ $REPLY = "p" ]; then
    echo "Pushing to update site..."
    cd infinitest-eclipse
    printf "\n\n%s    %s    %s" "`date`" ${new_version} "${release_message}" >> ReleaseNotes.txt
    ruby update_rss.rb ${new_version} "${release_message}"
    scp rss.xml ${update_site}
    scp ReleaseNotes.txt ${update_site}
    scp eclipse-site/target/classes/site.xml ${update_site}
    scp eclipse-feature/target/*.jar $MAIN_SITE/features/
    scp eclipse-plugin/target/*.jar $MAIN_SITE/plugins/

    git commit -a -m "Incrementing to version $new_version"
    git push origin master
    # DEBT Should probably do some stuff here:
    ## Tag the repo
    ## Update the main site?
    break
  else
    echo "Aborted!"
    git checkout infinitest-eclipse/pom.xml
    break
  fi
  break;
done