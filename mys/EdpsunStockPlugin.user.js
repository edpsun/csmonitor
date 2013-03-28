// ==UserScript==
// @name        EdpsunStockPlugin
// @namespace   EdpsunStockPlugin
// @description No
// @include     http://stockapp.finance.qq.com/pstock/*
// @require     http://code.jquery.com/jquery-1.9.0.min.js
// @version     1
// ==/UserScript==

    function add_link(){
        if($('.ed_link_class').length > 0){
            return;
        }
        var link_a = "http://stockdata.stock.hexun.com/2009_cgjzd_";
        var link_b = "http://f10.eastmoney.com/f10_v2/ShareholderResearch.aspx?code="
        $('.du-sd-item').each(function(){
            var first_th = $(this).find('th:first');
            var id = first_th.find('a:first').text();

            if( id == '000001' || id == '399001'){
                return;
            }

            var vid = '';
            if(id.charAt(0) == '6'){
                vid='sh'+ id;
            }else if(id.charAt(0) == '0'){
                vid='sz'+ id;
            }else{
                return;
            }

            var aa = $('<a class="ed_link_class"></a>').attr('href', link_a + id + '.shtml').attr("target","_blank").text('H').attr('style', 'padding-left: 5px;padding-right: 5px;');
            first_th.append(aa);

            var ab = $('<a class="ed_link_class"></a>').attr('href', link_b + vid).attr("target","_blank").text('E').attr('style', 'padding-left: 5px;padding-right: 5px;');

            first_th.append(ab);
        });
    }

    function clean_page(){
        $('#mini_nav_qq_wrap').hide();
        $('#Nav-logo').hide();
        $('#pstockBannerAdv').hide();
        $('#box3').hide();
        $('.Nav').hide();
        $('#QQFooter').hide();  
    }
    
    var clean_button = $('<input name="clean_button" class="add" type="button" id="clean_button" value="clean" />');
    clean_button.click(function(){clean_page();});
    
    var add_link_button = $('<input name="add_link_btn" class="add" type="button" id="add_link_btn" value="link" />');
    add_link_button.click(function(){add_link();});
    
//    var dowork_button = $('<input name="dowork_button" class="add" type="button" id="dowork_button" value="do" />');
//    dowork_button.click(function(){dowork();});

//    var go_button = $('<input name="go_button" class="add" type="button" id="go_button" value="go" />');
//    go_button.click(function(){gowork();});

    var button_area = $("#ad-nav");
    button_area.empty();
    button_area.append(add_link_button);
    button_area.append(clean_button);
//    button_area.append(dowork_button);
//    button_area.append(go_button);