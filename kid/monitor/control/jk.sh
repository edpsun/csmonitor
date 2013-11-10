#!/bin/bash
sleep 15
. /etc/profile.d/rvm.sh
ruby /home/jk/mjpg-streamer/wc.rb 2>&1 > /tmp/eee
