 mvn clean compile exec:java -Dexec.mainClass=com.gmail.edpsun.hystock.inbound.Main  -Dexec.args="-q 2013-01 -ebk ./src/test/resources/own.EBK"
 
 
 
 mvn clean compile exec:java -Dexec.mainClass=com.gmail.edpsun.hystock.select.Main  -Dexec.args="-a -s q1 -ebk ./src/test/resources/own.EBK"