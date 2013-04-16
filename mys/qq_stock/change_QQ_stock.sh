#!/bin/bash
cmd="google-chrome http://stockapp.finance.qq.com/pstock/view/manage.php?stktype=all&grpid=0&_r=0.48738989466801286&action=add&uin=2951072&code="
skip_list="sh000001,sz399001"

if [ "X$1" = "X" ]
then
    echo "[exit] please give EBK file. "
    exit 1
fi

echo $1 |grep ".EBK"
if [ "$?" -ne 0 ]
then
	echo "[exit] please give EBK file. "
	exit 1
else
	echo '\n' >> $1
fi

while read line
do
	id=`echo "${line}" |tr -d '\n'|tr -d '\r'|sed 's|^1|sh|g;s|^0|sz|g'`
	test -n "$id" || continue

	echo "${skip_list}"|grep "$id"
	test $? -eq 0 && continue
	
	echo "[+] deal with $id"

	cmd1="${cmd}${id}"
	$cmd1
	sleep 10
done < $1

