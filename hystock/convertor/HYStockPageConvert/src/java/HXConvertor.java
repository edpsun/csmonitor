/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.gmail.edpsun.hystock.inbound.collect.HTTPDataRetriever;
import com.gmail.edpsun.hystock.inbound.parser.HexunParser;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.model.Stock;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author esun
 */
@WebServlet(name = "HXConvertor", urlPatterns = {"/hxc"})
public class HXConvertor extends HttpServlet {

    private static final String style = "<style type=\"text/css\">"
            + "td {font-size:14px;white-space: nowrap;border:1px solid #EDEDED;padding: 2px;}"
            + ".bgcolor, .bgcolor1{background-color: #EDEDED; }"
            + "</style>";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Stock stock = getStock(id);
        showPage(response, stock);
    }

    private void showPage(HttpServletResponse response, Stock stock) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Stock</title>");
            out.println(style);
            out.println("</head>");
            out.println("<body>");
            out.println(stock.getName());
            out.println("    <table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"web2\">");
            out.println("        <tr align=\"center\"> ");
            out.println("        <td class=\"bgcolor\"><strong>报告期</strong></td>  ");
            out.println("        <td class=\"bgcolor\"><strong>流通</strong></td> ");
            out.println("        <td class=\"bgcolor\"><strong>股东</strong></td> ");
            out.println("        <td class=\"bgcolor\"><strong>人均持股变动</strong></td> ");
            out.println("        <td class=\"bgcolor\"><strong>人均持股</strong></td> ");
            out.println("        <td class=\"bgcolor\"><strong>总股本</strong></td> ");
            out.println("        </tr> ");

            int p =0;
            for (HolderStat stat : stock.getHolderStats()) {
                String c = "";
                if((++p)%2 == 0) {
                    c = "bgcolor1";
                }
                out.println("        <tr align=\"center\"> ");
                out.println("        <td class=\""+c+"\"><strong>" + stat.getYear() + "-" + stat.getQuarter() + "</strong></td>  ");
                out.println("        <td class=\""+c+"\">" + stat.getCirculatingShare() + "</td> ");
                out.println("        <td class=\""+c+"\">*" + stat.getHolderNum() + "</td> ");
                out.println("        <td class=\""+c+"\">" + stat.getDeltaInFloat() + "</td> ");
                out.println("        <td class=\""+c+"\">" + stat.getAverageHolding() + "</td> ");
                out.println("        <td class=\""+c+"\">" + stat.getTotalShare() + "</td> ");
                out.println("        </tr> ");
            }


            out.println("    </table>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private Stock getStock(String id) {
        HexunParser parser = new HexunParser();
        String url = parser.getTargetURL(id);
        try {
            System.out.println("====>%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            System.out.println(url);
            String content = new HTTPDataRetriever().getData(url);
            Stock stock = parser.parse(content);
            return stock;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
