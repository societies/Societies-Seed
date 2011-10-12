If you need to download artifacts from maven.ict-societies.eu you need to set the appropriate properties when running Maven:

mvn package -Djavax.net.ssl.trustStore=../societies-java-keystore.jks -Djavax.net.ssl.trustStorePassword=societies