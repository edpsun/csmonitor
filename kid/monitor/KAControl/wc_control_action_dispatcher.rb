# -*- encoding: utf-8 -*-
require_relative 'wc_action_dispatcher'
require 'json'
require 'date'

class WcControlActionDispatcher < WcHTTPActionDispatcher

  def process_start(req, resp)
    if $monitor.enabled
      data = {code: PROCESS_NOOP, msg: '已经启动啦! 不用重复启动哦:)'}
    else
      $monitor.start
      sleep(2)
      data = {code: PROCESS_SUCC, msg: '启动完成!'}
    end
    resp.body=data.to_json
  end

  def process_shutdown(req, resp)
    $monitor.stop
    data = {code: PROCESS_SUCC, msg: '关闭完成!'}
    resp.body=data.to_json
  end

  def process_switch_cam(req, resp)
    $monitor.switch
    $monitor.stop
    sleep(2)
    $monitor.start

    data = {code: PROCESS_SUCC, msg: '切换完成!'}
    resp.body=data.to_json
  end

  def process_halt_pc(req, resp)
    $monitor.stop
    $monitor.shutdown_pc

    data = {code: PROCESS_SUCC, msg: '电脑关机完成!'}
    resp.body=data.to_json
  end

  def process_heart_beat(req, resp)
    if ($monitor.check_snapshot_ok?($host, $monitor_port, '/?action=snapshot'))
      data = {code: PROCESS_SUCC, msg: '监控工作正常:)'}
    else
      data = {code: PROCESS_FAIL, msg: '监控无法工作:('}
    end
    resp.body=data.to_json
  end

  def process_set_alarm(req, resp)
    v = req.query['val']
    if (v)
      $monitor.is_in_alarm = (v=='true')
      data = {code: PROCESS_SUCC, msg: 'Alarm: '+ $monitor.is_in_alarm.to_s}
    else
      data = {code: PROCESS_NOOP, msg: 'val is not specified.'}
    end
    resp.body=data.to_json
  end

  def process_get_alarm(req, resp)
    data = {code: PROCESS_SUCC, msg: 'Alarm: '+ $monitor.is_in_alarm.to_s,is_alarm:$monitor.is_in_alarm}
    resp.body=data.to_json
  end

  def process_fpt(req, resp)
    v = req.query['val']
    if (!v || v.to_i == 0)

      if(!@not_auto_set_fpt)
        d = DateTime.now
        if d.hour > 17
          $monitor.fp_threshold = 7
        else
          $monitor.fp_threshold = 4
        end
      end

      data = {code: PROCESS_NOOP, msg: "Current FP Threshold: #{$monitor.fp_threshold}",FPT:"#{$monitor.fp_threshold}"}
    else
      $monitor.fp_threshold = v.to_i
      data = {code: PROCESS_SUCC, msg: "NEW FP Threshold: #{$monitor.fp_threshold}"}
      @not_auto_set_fpt = true
    end
    resp.body=data.to_json
  end
end
