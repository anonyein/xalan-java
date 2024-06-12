mvn clean package site
mvn install:install-file -Dfile=./xalan-java/xalan/lib/xpath31_types.jar -DgroupId=xml.xpath31 -DartifactId=xpath31_types -Dversion=1.0 -Dpackaging=jar

