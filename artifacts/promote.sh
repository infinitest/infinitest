MAIN_SITE=infinitest.org:/var/www/html
EXPERIMENTAL_SITE=${MAIN_SITE}/experimental

function remote_copy {
  scp ${EXPERIMENTAL_SITE}/$1 .
  scp $1 ${MAIN_SITE}/
  rm $1
}

echo "Promote experimental plugin to main update site?"
select fname in [p]romote,[a]bort;
do
  if [ $REPLY = "p" ]; then
    cd ..
    remote_copy site.xml
    remote_copy ReleaseNotes.txt
    remote_copy rss.xml
    break
  else
    echo "Aborted!"
    break
  fi
echo "Update site content is:"
curl infinitest.guthub.com/site.xml
done
