# -*- coding:utf-8 -*-
require 'test/unit'
require_relative '../rbst'

class TestReportInfoRetriever < Test::Unit::TestCase
  def setup
    @retriever = RBST::ReportInfoRetriever.new
  end

  def test_get_data
    ret = @retriever.get_report_info('002094')
    assert_equal('002094',ret[0])
    assert_equal('青岛金王',ret[1])
    assert_equal('2013-10-26',ret[2])
  end


  def test_parse_content
    str = <<'HERE'
            HERE</table>
						<!--数据段-->
						<table id="table_x2" width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr><td width='70' align='center' class='noBorder_L'><a href='http://stockdata.stock.hexun.com/002094.shtml' class='a1' target='_blank'>002094</a></td><td width='70' align='center'><a href='http://stockdata.stock.hexun.com/002094.shtml' class='a1' target='_blank'>青岛金王</a></td><td width='100' align='center'>2013-10-26</td><td width='140' align='center'><font color='red'>0.14</font></td><td width='300' align='center'>--</td><td width='140' align='center'>--</td><td align='center'><a href='http://guba.hexun.com/002094,guba.html' class='a1' target='_blank'>股吧</a>　<a href='http://stockdata.stock.hexun.com/002094.shtml' class='a1' target='_blank'>行情</a>　<a href='http://ggzx.stock.hexun.com/more.jsp?t=0&k=002094' class='a1' target='_blank'>资讯</a></td></tr>
						</table>
						<!--数据段结束-->
						<table id="FY" width="100%"
HERE
    ret = @retriever.parse_content(str)
    assert_equal('002094',ret[0])
    assert_equal('青岛金王',ret[1])
    assert_equal('2013-10-26',ret[2])
  end
end
