class WcHTTPActionDispatcher
  PROCESS_SUCC = 'SUCC'
  PROCESS_FAIL = 'FAIL'
  PROCESS_NOOP = 'NOOP'

  def initialize(prefix='process_')
    @prefix = prefix
  end

  def dispatch(req, resp)
    action_name = req.query["name"]
    if action_name && self.respond_to?(@prefix + action_name)
      resp.body = '[Warn] Default Message for Actoin ' + action_name + '. Please override it in derived class.'
      self.send(@prefix + action_name, req, resp)
    else
      resp.status = 404
      resp.body = "Supported Actions: #{supported_actions()}"
    end
  end

  def supported_actions
    list = self.methods().select!{|x| x.to_s.start_with?(@prefix)}
    puts ':' + @prefix
    list.each {|x| puts x.to_s}
    list.map{|x| x.to_s().gsub( @prefix,'')}
  end

  def get_content_type
    "text/html;charset=utf-8"
  end

end