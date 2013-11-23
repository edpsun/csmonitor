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

url = "http://#{$host}:#{$monitor_port}/?action=snapshot"
puts check_snapshot_ok?('192.168.1.14', 9000, '/?action=snapshot')