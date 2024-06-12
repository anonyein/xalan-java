mvn clean package site
mvn install:install-file -Dfile=./xalan/lib/xpath31_types.jar -DgroupId=xml.xpath31 -DartifactId=xpath31_types -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./xalan/lib/json-20200518.jar -DgroupId=org.json -DartifactId=json-20200518 -Dversion=1.0 -Dpackaging=jar

