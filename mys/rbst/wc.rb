# -*- encoding: utf-8 -*-
#!/usr/bin/ruby
require 'webrick'
require_relative 'wc_monitor'
include WEBrick


$monitor = WcMonitor.new
$host='localhost'
$control_port=2000
$monitor_port=9000
puts 'init the monitor control. State:' + $monitor.enabled.to_s

def get_check_code
  %{
  <script type="text/javascript" src="/js/jquery.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      setInterval('hearbeat()',15000);
    });

    function hearbeat(){
      $.ajax({
        url: "/heart_beat",
        success: function( data ) {
          if ($('#stream_img img').size() == 0 ){
             $('#stream_img').empty();
             img = $('<img src="http://#{$host}:#{$monitor_port}/?action=stream" />');
             $('#stream_img').append(img);
          }
        },
        error: function(){
          $('#stream_img').empty();
          p = $('<p style="font-family:verdana;font-size:250%;color:red">监控出现问题了, 有可能其他人点击关闭监控了. </p><br>' +
          '<p style="font-family:verdana;font-size:250%;color:red">请检查 1.尝试重新启动, 先点 关闭监控, 再点 启动监控 2. 电脑的电源是不是接通了?? </p>');
          $('#stream_img').append(p);
        }
      });
    }
  </script>
  }
end

def get_header(refresh_type)
  extra_head = case refresh_type
                 when 'NONE'
                   ''
                 when 'SWITCH_AND_REFRESH'
                   '<meta http-equiv="refresh" content="10;url=/action?t=show">'
                 when 'REFRESH'
                   #'<meta http-equiv="refresh" content="15">'
                   get_check_code
               end

  %{
<html>
  <head>
    <title>监控小朋友</title>
    #{extra_head}
  </head>
    }
end

def get_res(msg='', req)
  %{
<body>
<center>
      <dev id='stream_img'>
      <img src="http://#{req.host}:#{$monitor_port}/?action=stream" />
      </dev>
</center>
<table border="0" width='95%'>
<tr>
  <td style="font-family:verdana;font-size:150%;color:green">当前状态: #{$monitor.enabled ? "启动" : "关闭"  }</td>
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=enable">启动监控</a></td>
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=show">开始监控</a></td>
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=disable">关闭监控</a></td>

  #{
  ($monitor.can_switch && $monitor.enabled) ?
      "<td style=\"font-family:verdana;font-size:150%;color:red\"><a href=\"/action?t=switch\">切换摄像头</a></td>" : ""
  }
  <td style="font-family:verdana;font-size:150%;color:red"><a href="/action?t=shutdown_pc">电脑关机</a></td>

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
  $host = req.host
  resp['Content-Type'] = get_content_type
  if($monitor.enabled)
    resp.status= 301
    resp['location']=%{http://#{req.host}:#{$control_port}/action?t=show}
  else
    resp.body = get_header('NONE') + get_res('', req)
  end

end

$return_succ = true
heart_beat_proc = lambda do |req, resp|
  $host = req.host

  if($monitor.check_snapshot_ok?($host, $monitor_port, '/?action=snapshot'))
    $return_succ = true
  else
    $return_succ = false
  end

  type = req.query["t"]
  if (type == 'fail')
    $return_succ = false
  elsif (type == 'succ')
    $return_succ = true
  end

  resp['Content-Type'] = get_content_type
  if(!$return_succ)
    resp.status= 404
    resp.body = 'FAILED'
  else
    resp.body = 'OK'
  end



end

action_proc = lambda do |req, resp|
  $host = req.host
  type = req.query["t"]
  puts type
  msg =''

  refresh_type = 'SWITCH_AND_REFRESH'
  if type == 'show'
    if $monitor.enabled
      #resp.status= 301
      #resp['location']=%{http://#{req.host}:#{$monitor_port}/javascript_simple.html}
      #return
      refresh_type = 'REFRESH'
    else
      refresh_type='NONE'
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
  elsif type == 'shutdown_pc'
    $monitor.stop
    $monitor.enabled=false
    $monitor.shutdown_pc
    sleep(2)
    msg ='电脑关机完成!'
  end

  resp['Content-Type'] = get_content_type
  resp.body = get_header(refresh_type) + get_res(msg, req)
end
action = HTTPServlet::ProcHandler.new(action_proc)
start = HTTPServlet::ProcHandler.new(start_proc)
heart_beat = HTTPServlet::ProcHandler.new(heart_beat_proc)

s = HTTPServer.new(:Port => $control_port, DocumentRoot: File.join(Dir.pwd, "/www"))
s.mount("/start", start)
s.mount("/action", action)
s.mount("/heart_beat", heart_beat)

trap("INT") { s.shutdown }
s.start