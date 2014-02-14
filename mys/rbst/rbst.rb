# -*- coding:utf-8 -*-
module RBST
  class ReportInfoRetriever
    def get_report_info(id)
      url = get_url(id)
      str = get_content(url)
      parse_content(str)
    end

    def get_url(id)
      '/wholemarket/html/cbpl.aspx?Ndate=2013-12-31&code=' + id
    end

    def get_content(uri)
      require 'net/http'
      res = Net::HTTP.get_response('datainfo.hexun.com', uri)
      res.body.encode("utf-8", 'GBK')
    end

    def parse_content(str)
      s = str.index('table_x2')
      e =str.index('</table>', s)

      str = str[s...e]
      re = />(.*?)</

      list = []
      str.scan(re).each {|item| list << item[0] if item[0].length > 0}
      return list[0,3]
    end

  end
end
