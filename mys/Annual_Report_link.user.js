// ==UserScript==
// @name        Annual Report Link
// @namespace   https://edpsun.google.com/annual_report
// @description Annual Report Link
// @include     http://datainfo.hexun.com/wholemarket/html/cbcx.aspx
// @require     http://code.jquery.com/jquery-1.9.0.min.js
// @version     1.0
// ==/UserScript==

function GM_DJS_running_job_info_enhance() {
    // wait the jquery to be loaded
    function GM_wait() {
        //if (typeof unsafeWindow.jQuery == 'undefined') {
        //    window.setTimeout(GM_wait, 100);
        //}
        //else {
        //    $ = unsafeWindow.jQuery;
        //}
    }
    GM_wait();

    function enhance_ui(){
        //alert();
        $('#table_x2 tr .noBorder_L a').each(function (){
            var url = 'http://stockdata.stock.hexun.com/2009_cgjzd_';
            url += $(this).text();
            url += ".shtml";
            $(this).attr('href', url);
        });
    }
    
    enhance_ui();
}

GM_DJS_running_job_info_enhance();
