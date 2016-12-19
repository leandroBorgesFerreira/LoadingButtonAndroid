if [ "$TRAVIS_TAG" == "" ]; then
  echo -e 'Build Branch with Snapshot => Branch ['$TRAVIS_BRANCH']'
  ./gradlew build
elif [ "$TRAVIS_TAG" != "" ]; then
  echo -e 'Build Branch for Release => Branch ['$TRAVIS_BRANCH']  Tag ['$TRAVIS_TAG']'
  ./gradlew -PbintrayUser="${bintrayUser}" -PbintrayKey="${bintrayKey}" build bintrayUpload --stacktrace --info
else
  echo -e 'WARN: Should not be here ./gradlew clean'
fi