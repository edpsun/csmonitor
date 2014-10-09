// ==UserScript==
// @name        EdpsunStockPlugin
// @namespace   EdpsunStockPlugin
// @description No
// @include     http://stock.qq.com/i/*
// @require     http://code.jquery.com/jquery-1.9.0.min.js
// @version     1
// ==/UserScript==
    function GM_wait() {
        if ($('.zxg-stocklist').length == 0 || $('#sh000001').length == 0 ) {
            window.setTimeout(GM_wait, 1000);
        }
        else {
            enhance();
        }
    }
    GM_wait();

    var button_div;
    function enhance()
    {
        $('#sh000001').css("border-bottom","solid red");
        $('#sz399001').css("border-top","solid red");

        if($('.tool_col_1').length == 0){
            return;
        }
        button_div = $('.tool_col_1');
        var tabs = $('<ul class="tab market"></ul>');
        var clean_button = $('<li>clean</li>');
        var links_button = $('<li>links</li>');
        tabs.append(clean_button).append(links_button).appendTo(button_div);

        clean_button.click(function(){clean_page();});
        links_button.click(function(){add_link();});
    }

    function clean_page(){
        alert('clean');
    }

    function add_link(){
        var link_a = "http://stockdata.stock.hexun.com/2009_cgjzd_";
        var link_b = "http://f10.eastmoney.com/f10_v2/ShareholderResearch.aspx?code="
        var link_c = "http://stock.finance.qq.com/corp1/stk_holder_count.php?zqdm="
        $('.zxg-stocklist dl').each(function(){
            var line = $(this);
            var vid = line.attr('id');

            if( vid == 'sh000001' || vid == 'sz399001'){
                return;
            }
            
            if( (vid.charAt(0) == 's') && (vid.charAt(1) == 'z' || vid.charAt(1) == 'h') ){
                var id = vid.substr(2,7); 
            }else{
                return;
            }

            var first_th = line.find('dd div:first').empty();

            var aa = $('<a class="ed_link_class"></a>').attr('href', link_a + id + '.shtml').attr("target","_blank").text('H').attr('style', 'padding-left: 5px;padding-right: 5px;');
            first_th.append(aa);

            var ab = $('<a class="ed_link_class"></a>').attr('href', link_b + vid).attr("target","_blank").text('E').attr('style', 'padding-left: 5px;padding-right: 5px;');
            first_th.append(ab);
            
            var ac = $('<a class="ed_link_class"></a>').attr('href', link_c + id).attr("target","_blank").text('Q').attr('style', 'padding-left: 5px;padding-right: 5px;');
            first_th.append(ac);
        });
    }
    
    
