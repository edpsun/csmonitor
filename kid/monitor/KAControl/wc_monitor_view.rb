# -*- encoding: utf-8 -*-
require 'erb'

class WcMonitorView
  MONITOR_VIEW_TEMPLATE = %{<!DOCTYPE HTML>
<html>
    <head>
        <title>监控小朋友</title>
        <link rel="shortcut icon" href="/favicon2.ico" />
        <link rel="stylesheet" type="text/css" href="css/kac.css" />
        <script type="text/javascript" src="js/jquery.js"></script>
        <script type="text/javascript" src="js/monitor.js"></script>
        <script type="text/javascript">
            <%=stream_image_url%>
        </script>
<% for head_line in head_lines %>
        <%= head_line %>
<% end %>
    </head>
    <body>
        <div id='welcome_div'>
                <table width='100%'>
                    <tr>
                        <td align='center'><img src="image/welcome.jpg" width="40%"></td>
                    </tr>
                    <tr>
                        <td align='center'><button id='entry_btn'>进入</button></td>
                    </tr>
                </table>
        </div>

        <div id='main_div'>
            <div id='image_div'></div>
            <div id='control_div'>
                <table border="0" width='95%'>
                    <tr>
                        <td style="font-size:150%;color:green" id='current_status'>当前状态:</td>
                        <td align='center'><button id='start'>启动监控</button></td>
                        <td align='center'><button id='shutdown'>关闭监控</button></td><% cs = ($monitor.can_switch ? '':'hide')%>
                        <td align='center' class='<%=cs%>' ><button id='switch_cam'>切换摄像头</button></td>
                        <td align='center'><button id='halt_pc'>电脑关机</button></td>
                    </tr>
                </table>
            </div>
        </div>
        <div id='msg_div'></div>
        <div id='audio_div'>
            <audio src="/a.wav" controls="controls" id="audio">
                 Your browser does not support the audio element.
            </audio>
        </div>
    </body>
</html>
  }

  def WcMonitorView.get_view_monitor
    erb = ERB.new(MONITOR_VIEW_TEMPLATE)
    head_lines = []
    stream_image_url="var stream_image_url = 'http://#{$host}:#{$monitor_port}/?action=stream';"
    erb.result(binding)
  end

end