#!/bin/bash
sleep 15
. /etc/profile.d/rvm.sh
cd /home/jk/work/github/csmonitor/kid/monitor/KAControl
ruby ./wc4ka.rb 2>&1 > /tmp/eee



