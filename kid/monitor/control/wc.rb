# -*- encoding: utf-8 -*-
#!/usr/bin/ruby
require 'webrick'
require_relative 'wc_monitor'
include WEBrick


$monitor = WcMonitor.new
puts 'init the monitor control. State:' + $monitor.enabled.to_s

def get_res(msg='', req)
  %{
<html><body>
<center>
      <img src="http://#{req.host}:9000/?action=stream" />
</center>
<table border="0" width='95%'>
<tr>
  <td style="font-family:verdana;font-size:150%;color:green">当前状态: #{$monitor.enabled ? "启动" : "关闭"  }</td>
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=enable">启动监控</a></td>
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=show">开始监控</a></td>
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=disable">关闭监控</a></td>

  #{
    ($monitor.can_switch && $monitor.enabled )?
      "<td style=\"font-family:verdana;font-size:150%;color:red\"><a href=\"/action?t=switch\">切换摄像头</a></td>":""
  }

</tr>
</table>

<p style="font-family:verdana;font-size:150%;color:red"> #{msg} </p>

</body></html>
}
end

def get_content_type
  "text/html;charset=utf-8"
end

start_proc = lambda do |req, resp|
  resp['Content-Type'] = get_content_type
  resp.body = get_res '' ,req
end

action_proc = lambda do |req, resp|
  type = req.query["t"]
  puts type
  msg =''

  if type == 'show'
    if $monitor.enabled
      #resp.status= 301
      #resp['location']=%{http://#{req.host}:9000/javascript_simple.html}
      #return
    else
      msg = "请先启动监控"
    end
  elsif type =='enable'
    if $monitor.enabled
      msg = '已经启动啦! 不用重复启动哦:)'
    else
      $monitor.start
      $monitor.enabled=true
      sleep(2)
      msg ='启动完成!'
    end
  elsif type == 'disable'
    $monitor.stop
    $monitor.enabled=false
    msg = '关闭完成!'
  elsif type == 'switch'
    $monitor.switch
    $monitor.stop
    sleep(1)
    $monitor.start
    $monitor.enabled=true
    sleep(2)
    msg ='切换完成!'
  end

  resp['Content-Type'] = get_content_type
  resp.body = get_res msg , req
end
action = HTTPServlet::ProcHandler.new(action_proc)
start = HTTPServlet::ProcHandler.new(start_proc)

s = HTTPServer.new(:Port => 2000)
s.mount("/start", start)
s.mount("/action", action)
trap("INT") { s.shutdown }
s.start