# -*- encoding: utf-8 -*-
class WcMonitor
  attr_accessor :enabled, :current_device, :can_switch, :is_in_alarm

  def initialize
    @enabled = false
    @is_in_alarm =false

    require 'pathname'
    @current_path = Pathname.new(__FILE__).parent.realpath
    puts @current_path
    if can_switch()
      @current_device='1'
    else
      @current_device='0'
    end
  end

  def start
    puts '-----------[start]------------'
    @is_in_alarm =false
    cmd = 'mjpg_streamer -i "input_uvc.so -d /dev/video' +@current_device+ ' -f 30" -o "output_http.so -w /usr/www -p  9000"'
    puts cmd
    IO.popen(cmd)

    java_cmd = "/export/tools/jdk/bin/java -cp #{@current_path}/kidalarm.jar -DALARM_THRESHOLD=5 com.hylps.alarm.KidAlarm 2>&1 >/tmp/KAJava.log"
    puts java_cmd
    IO.popen(java_cmd)
    @enabled = true
  end

  def stop
    puts '-----------[stop]------------'
    system('pkill -2 mjpg_streamer')
    system('kill -9 `ps -ef |grep java |grep kidalarm |awk \'{print $2}\'`')
    @enabled = false
  end

  def can_switch
    num = `ls /dev/video* |wc -l`.strip.to_i
    if (num > 1)
      @can_switch = true
    else
      @can_switch = false
      @current_device='0'
    end

    @can_switch
  end

  def shutdown_pc
    puts '-----------[shutdown]------------'
    cmd = 'sleep 10; sudo halt -p'
    puts cmd
    IO.popen(cmd)
  end

  def switch
    puts '-----------[switch]------------'
    if @can_switch == false
      return
    end

    if (@current_device=='0')
      @current_device ='1'
    else
      @current_device ='0'
    end
  end

  def check_snapshot_ok?(host, port, uri)
    require 'net/http'
    site = Net::HTTP.new(host, port)

    ret = true
    begin
      response = site.get2(uri);
      puts response.code
      if response.code != '200'
        ret = false
      end
    rescue Exception
      ret = false
    end
    ret
  end
end
