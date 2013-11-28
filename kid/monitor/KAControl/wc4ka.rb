# -*- encoding: utf-8 -*-
#!/usr/bin/ruby
require 'webrick'
require_relative 'wc_monitor'
require_relative 'wc_proc_mgr'
include WEBrick

$monitor = WcMonitor.new

$host='localhost'
$control_port=2000
$monitor_port=9000
s = HTTPServer.new(:Port => $control_port, DocumentRoot: File.join(Dir.pwd, "/www"))

proc_mgr = WcProcMgr.new
proc_mgr.bind_resource(s)

trap("INT") { s.shutdown }
s.start
