 mvn clean compile exec:java -Dexec.mainClass=com.gmail.edpsun.hystock.inbound.Main  -Dexec.args="-q 2013-01 -ebk ./src/test/resources/own.EBK"
 
 
 
 mvn clean compile exec:java -Dexec.mainClass=com.gmail.edpsun.hystock.select.Main  -Dexec.args="-a -ebk ./src/test/resources/own.EBK -s q1"
 
 HY_OPT=" -DTHRESHOLDER_HOLDER_INCREASE_RATE_IGNORE=0.05f \
         -DTHRESHOLDER_AVG_AMOUNT_INCREASE_RATE_AS_INCREASE=-6.0f \
         -DTHRESHOLDER_AVG_AMOUNT_DECREASE_TIMES=2 \
         -DTHRESHOLDER_HOLDER_CHANGE_RATE=0.85f \
         -DTHRESHOLDER_HOLDER_QNUM=2 \
         -DTHRESHOLDER_AVG_AMOUNT_QNUM=5 \
         -DTRESHOLDER_CANNON=30.0f "
         
         
#!/bin/sh
export DERBY_HOME=/export/tools/jdk/db
export PATH="$DERBY_HOME/bin:$PATH"

netstat -ntl|grep 1527 && exit 0

cd /data/depot/derby/hystock
startNetworkServer -h 0.0.0.0 &

$('.stdiv').hide();
$('li:contains("2013-2")').parents('.stdiv').show();



init db:
open ij and create database:
----
connect 'jdbc:derby://127.0.0.1:1527/hystock;user=admin;password=admin123;create=true';
connect 'jdbc:derby://127.0.0.1:1527/hystock-test;user=admin;password=admin123;create=true';


create table STOCK(id varchar(32) primary key, name varchar(256), comment varchar(1024));

create table memreport(id varchar(32) primary key, name varchar(256));

create table HOLDER_STAT(id varchar(32) primary key, STOCK_ID varchar(32), FISCAL_YEAR int,
QUARTER int,  TOTAL_SHARE BIGINT, CIRCULATING_SHARE BIGINT, HOLDER_NUM BIGINT, AVERAGE_HOLDING BIGINT, DELTA varchar(256) );

