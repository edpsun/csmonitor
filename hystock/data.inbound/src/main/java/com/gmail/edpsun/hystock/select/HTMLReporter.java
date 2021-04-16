/**
 * JavaTools_declaration
 */
package com.gmail.edpsun.hystock.select;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.model.AnalyzeVO;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import com.gmail.edpsun.hystock.util.HtmlUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HTMLReporter {
    static String tecent_url = "http://stockhtm.finance.qq.com/sstock/ggcx/%s.shtml";
    static String sina_url = "http://finance.sina.com.cn/realstock/company/%s/nc.shtml";
    static String hexun = "http://stockdata.stock.hexun.com/2009_cgjzd_%s.shtml";
    static String jrj = "http://stock.jrj.com.cn/share,%s,gdhs.shtml";
    static String stdiv = "<div stockid=\"%s\" id=\"%d\" class=\"stdiv\" tags=\"%s\" hQnum=\"%d\" hCRate=\"%f\" elid=\"%s\" >";

    public void exportReport(final InboundContext ctx) {
        BufferedWriter bww = null;
        final File file = new File(ctx.getSchema(), "report.html");

        try {
            bww = new BufferedWriter(new FileWriter(file));

            bww.write(HtmlUtils.getHeader("Chosen List"));
            bww.write("\n\n");
            bww.write(getFilterHmtl());

            final StringBuilder total = new StringBuilder();
            total.append("<span id='total'></span>").append("\n");
            total.append("<br><br>").append("\n");
            total.append("<div id=\"listdiv\"></div><br><br>").append("\n");
            bww.write(total.toString());

            Collections.sort(ctx.getChosenList(), new Comparator<InboundContext>() {
                @Override
                public int compare(final InboundContext o1, final InboundContext o2) {
                    return o1.getAnalyzeVO().compareTo(o2.getAnalyzeVO());
                }
            });

            final StringBuilder sb = new StringBuilder();
            int p = 0;
            for (final InboundContext stockCtx : ctx.getChosenList()) {
                final Stock stock = stockCtx.getStock();
                final AnalyzeVO analyzeVO = stockCtx.getAnalyzeVO();
                final String tags = analyzeVO.getTags();

                String exportListID = null;
                String sinaId = null;
                if (analyzeVO.getCode().startsWith("6")) {
                    exportListID = "1";
                    sinaId = "sh" + stock.getId();
                } else {
                    exportListID = "0";
                    sinaId = "sz" + stock.getId();
                }
                exportListID += analyzeVO.getCode();

                sb.setLength(0);
                sb.append("[" + (++p) + "] ").append(stock.getName()).append("<br>\n    ");
                sb.append(analyzeVO).append("<br>\n");
                System.out.println(sb.toString());

                sb.append("<br> <a target=\"_blank\"  href=\"" + String.format(sina_url, sinaId) + "\">Sina</a>\n");
                sb.append("&nbsp;&nbsp;&nbsp; <a target=\"_blank\"  href=\"" + String.format(tecent_url, stock.getId())
                        + "\">Tecent</a>\n");
                sb.append("&nbsp;&nbsp;&nbsp; <a target=\"_blank\"  href=\"" + String.format(hexun, stock.getId())
                        + "\">HeXun</a>\n");
                sb.append("&nbsp;&nbsp;&nbsp; <a target=\"_blank\"  href=\"" + String.format(jrj, stock.getId())
                        + "\">JinRongJie</a>\n");

                sb.append("<hr><pre><ul>\n");
                final List<HolderStat> holderStats = stock.getHolderStats();
                int length = holderStats.size();
                final int LEN = 8;
                if (length > LEN) {
                    final int aaNum = analyzeVO.getAverageAmountUpQNum();
                    final int hNum = analyzeVO.getHolderUpQNum();

                    final int maxQnum = hNum > aaNum ? hNum : aaNum;
                    if (maxQnum > LEN) {
                        length = maxQnum;
                    } else {
                        length = LEN;
                    }
                }

                for (int i = 0; i < length; i++) {
                    sb.append("<li>" + holderStats.get(i).toFormattedString()).append("</li>\n");
                }
                sb.append("</ul></pre>\n");

                sb.insert(
                        0,
                        String.format(stdiv, analyzeVO.getCode(), p, tags, analyzeVO.getHolderUpQNum(),
                                analyzeVO.getHolderChange(), exportListID));
                sb.append("\n</div>");
                bww.write(sb.toString());
                bww.write("\n");
            }
            bww.write(HtmlUtils.getFooter());

            copyJs(ctx.getSchema());
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bww.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }

    private void copyJs(final String _path) {
        final String jsDir = "js";
        final File path = new File(_path, jsDir);
        if (!path.exists()) {
            path.mkdirs();
        }
        copyIoToFile(jsDir + "/" + HtmlUtils.bda, new File(path, HtmlUtils.bda));
        copyIoToFile(jsDir + "/" + HtmlUtils.jqery, new File(path, HtmlUtils.jqery));
    }

    private void copyIoToFile(final String source, final File targetPath) {
        final InputStream in = getClass().getClassLoader().getResourceAsStream(source);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetPath);
            IOUtils.copy(in, out);
        } catch (final Exception e) {
            throw new RuntimeException("Error while copy js", e);
        } finally {
            try {
                in.close();
            } catch (final IOException e) {
            }
            try {
                out.close();
            } catch (final IOException e) {
            }
        }
    }

    private String getFilterHmtl() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<span title=\"ID\">ID:</span>").append("\n");
        sb.append("<input type=\"text\" name=\"stockid\" value=\"\"/>").append("\n");
        sb.append("<br>").append("\n");

        sb.append("<span title=\"Tags\">Tags:</span>").append("\n");
        sb.append("<input type=\"text\" name=\"tags\" value=\"\"/>").append("\n");
        sb.append("<br>").append("\n");

        sb.append("<span title=\"Holder num change rate lower limit.\">Rate >=</span>").append("\n");
        sb.append("<input type=\"text\" name=\"change_rate_min\" value=\"\"/>").append("\n");
        sb.append("<br>").append("\n");

        sb.append("<span title=\"Holder num change rate upper limit.\">Rate <=</span>").append("\n");
        sb.append("<input type=\"text\" name=\"change_rate_max\" value=\"\"/>").append("\n");
        sb.append("<br>").append("\n");

        sb.append("<span title=\"Holder change quarter num lower limit.\">Qnum >=</span>").append("\n");
        sb.append("<input type=\"text\" name=\"qnum_min\" value=\"\"/>").append("\n");
        sb.append("<br>").append("\n");

        sb.append("<span title=\"Holder change quarter num upper limit.\">Qnum <=</span>").append("\n");
        sb.append("<input type=\"text\" name=\"qnum_max\" value=\"\"/>").append("\n");
        sb.append("<br>").append("\n");
        sb.append("<button type=\"button\" id=\"filterButton\">Filter</button>").append("\n");
        sb.append("<button type=\"button\" id=\"cleanButton\">Reset</button>").append("\n");
        sb.append("<button type=\"button\" id=\"exportButton\">Export</button>").append("\n");
        sb.append("<button type=\"button\" id=\"showPicButton\">ShowPic</button>").append("\n");
        sb.append("<button type=\"button\" id=\"anyFilterButton\">AnyFilter</button>").append("\n");

        return sb.toString();
    }
}
