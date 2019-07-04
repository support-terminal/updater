pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh './gradlew clean bootJar'
      }
    }
    stage('upload') {
      steps {
        sh '''HOST=vh112.timeweb.ru
USER=cm41657_ftp
PASS=FUP3qNjA

PROJECT_VERSION=$(./gradlew properties | grep ^version: | awk \'{print $2}\')
echo ${PROJECT_VERSION}

FILE_HASH=$(sha1sum build/libs/updater-${PROJECT_VERSION}.jar | awk \'{print $1}\')

cat >./index.php <<EOF
<?php
use \\Psr\\Http\\Message\\ServerRequestInterface as Request;
use \\Psr\\Http\\Message\\ResponseInterface as Response;

require \'../../vendor/autoload.php\';

EOF

echo \'$app = new \\Slim\\App;\' >> ./index.php
echo \'$app ->get("/actual-version", function (Request $request, Response $response, array $args) {\' >> ./index.php
echo \'$object = new stdClass();\' >> ./index.php
echo \'$object->version = "\'${PROJECT_VERSION}\'";\' >> ./index.php
echo \'$object->fileName = "updater-\'${PROJECT_VERSION}\'.jar";\' >> ./index.php
echo \'$object->artifactLink = "http://nixvision.com/artifacts/updater/updater-\'${PROJECT_VERSION}\'.jar";\' >> ./index.php
echo \'$object->artifactHash = "\'${FILE_HASH}\'";\' >> ./index.php
echo \'return $response ->withStatus(200)->withHeader("Content-Type", "application/json")->write(json_encode($object));\' >> ./index.php

echo \'});\' >> ./index.php
echo \'$app ->run();\' >> ./index.php





ftp -invp $HOST << EOF
user $USER $PASS
cd public_html
mkdir artifacts
cd artifacts
mkdir updater
cd updater
put build/libs/updater-${PROJECT_VERSION}.jar updater-${PROJECT_VERSION}.jar
put index.php index.php

bye
EOF'''
      }
    }
  }
}