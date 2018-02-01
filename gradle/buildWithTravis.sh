if [ "$TRAVIS_TAG" == "" ]; then
  echo -e 'Build Branch'
  ./gradlew build
elif [ "$TRAVIS_TAG" != "" ]; then
  echo -e 'Build Branch for tag: Tag ['$TRAVIS_TAG']'
  ./gradlew build
  ./gradlew -PbintrayUser="${bintrayUser}" -PbintrayKey="${bintrayKey}" build bintrayUpload
else
  echo -e 'WARN: Should not be here ./gradlew clean'
fi