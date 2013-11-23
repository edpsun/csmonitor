class WcMonitor
  attr_accessor :enabled, :current_device, :can_switch
  def initialize
    @enabled = false

    if can_switch()
      @current_device='1'
    else
      @current_device='0'
    end
  end

  def start
    cmd = 'mjpg_streamer -i "input_uvc.so -d /dev/video' +@current_device+ ' -f 30" -o "output_http.so -w /usr/www -p  9000"'
    puts cmd
    IO.popen(cmd)
    #exec('mjpg_streamer -i "input_uvc.so -d /dev/video0 -f 30" -o "output_http.so -w /usr/www -p  9000"')
  end

  def stop
     system('pkill -2 mjpg_streamer')
  end

  def can_switch
    num = `ls /dev/video* |wc -l`.strip.to_i
    if(num > 1)
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