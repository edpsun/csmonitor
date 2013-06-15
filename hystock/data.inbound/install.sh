#!/bin/sh

#home dir
target_dir="target"
release_home="${target_dir}/release"
rm -rf ${release_home}
mkdir -p ${release_home}

# compile
mvn clean install
# dependency 
mvn dependency:copy-dependencies -DoutputDirectory=${release_home}/lib -DincludeScope=runtime
set -x
# main jar
cp ${target_dir}/*.jar  ${release_home}/lib

cat <<'EOF' >/tmp/$$.sh
#!/bin/sh

if [ -L $0 ]
then
        #spath=`ls -l $0 | sed 's|^.*-> *||g'`
        spath=`readlink $0`
        if [ ! -e "$spath" ]
        then
                s=`dirname $0`
                spath="$s/$spath"
        fi
else
        spath=$0
fi


exec_home=$(cd "$(dirname "$spath")"; pwd)
work_dir=`pwd`
work_dir=$(cd "$work_dir"; pwd)

echo "exec_home:${exec_home}"
echo "work_dir :${work_dir}"
echo "=========================="

CLASSPATH=""
for jar in `ls ${exec_home}/lib/*.jar`
do  
    if [ "X${CLASSPATH}" = "X" ]
    then
        CLASSPATH="${jar}"
    else
        CLASSPATH="${CLASSPATH}:${jar}"
    fi
done

echo $CLASSPATH
EOF

cp /tmp/$$.sh ${release_home}/hyget.sh 
cp /tmp/$$.sh ${release_home}/hyreport.sh

echo 'java -cp "$CLASSPATH" com.gmail.edpsun.hystock.inbound.Main $@' >> ${release_home}/hyget.sh
echo 'java -cp "$CLASSPATH" com.gmail.edpsun.hystock.select.Main $@' >> ${release_home}/hyreport.sh

echo ${release_home}/hyget.sh
echo ${release_home}/hyreport.sh
