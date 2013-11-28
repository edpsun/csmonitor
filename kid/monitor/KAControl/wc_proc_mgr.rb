# -*- encoding: utf-8 -*-
require_relative 'wc_monitor_view'
require_relative 'wc_control_action_dispatcher'
class WcProcMgr
  def initialize
  end

  def bind_resource(server)
    mount(server, '/monitor', prepare_proc(monitor_proc))
    mount(server, '/action', prepare_proc(action_proc))
  end

  def mount(server, path, proc_handler)
    server.mount(path, proc_handler)
  end

  def monitor_proc
    lambda do |req, resp|
      resp['Content-Type'] = get_content_type
      resp.body = WcMonitorView.get_view_monitor
    end
  end

  def action_proc
    lambda do |req, resp|
      WcControlActionDispatcher.new().dispatch(req, resp)
    end
  end

  def prepare_proc (proc)
    HTTPServlet::ProcHandler.new(proc)
  end

  def get_content_type
    "text/html;charset=utf-8"
  end
end