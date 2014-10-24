// ==UserScript==
// @name        EdpsunStockPlugin
// @namespace   EdpsunStockPlugin
// @description No
// @include     http://stock.qq.com/i/*
// @require     http://code.jquery.com/jquery-1.9.0.min.js
// @version     1
// ==/UserScript==

var reportTimeList = new Object();

reportTimeList["002107"]="2014-10-09";
reportTimeList["002496"]="2014-10-15";
reportTimeList["600664"]="2014-10-17";
reportTimeList["000892"]="2014-10-17";
reportTimeList["002009"]="2014-10-18";
reportTimeList["600595"]="2014-10-18";
reportTimeList["002366"]="2014-10-20";
reportTimeList["002011"]="2014-10-21";
reportTimeList["002233"]="2014-10-21";
reportTimeList["600114"]="2014-10-21";
reportTimeList["000533"]="2014-10-21";
reportTimeList["002175"]="2014-10-21";
reportTimeList["000708"]="2014-10-21";
reportTimeList["600348"]="2014-10-22";
reportTimeList["002226"]="2014-10-22";
reportTimeList["600674"]="2014-10-23";
reportTimeList["000782"]="2014-10-23";
reportTimeList["600297"]="2014-10-23";
reportTimeList["002169"]="2014-10-23";
reportTimeList["002537"]="2014-10-23";
reportTimeList["600508"]="2014-10-24";
reportTimeList["002209"]="2014-10-24";
reportTimeList["601636"]="2014-10-24";
reportTimeList["002121"]="2014-10-24";
reportTimeList["002189"]="2014-10-24";
reportTimeList["601958"]="2014-10-24";
reportTimeList["002119"]="2014-10-24";
reportTimeList["002208"]="2014-10-24";
reportTimeList["002056"]="2014-10-24";
reportTimeList["600298"]="2014-10-24";
reportTimeList["000910"]="2014-10-24";
reportTimeList["002123"]="2014-10-25";
reportTimeList["000573"]="2014-10-25";
reportTimeList["002028"]="2014-10-25";
reportTimeList["000759"]="2014-10-25";
reportTimeList["601965"]="2014-10-25";
reportTimeList["600378"]="2014-10-25";
reportTimeList["000151"]="2014-10-25";
reportTimeList["002150"]="2014-10-25";
reportTimeList["002101"]="2014-10-25";
reportTimeList["600379"]="2014-10-25";
reportTimeList["002156"]="2014-10-25";
reportTimeList["600714"]="2014-10-25";
reportTimeList["000655"]="2014-10-25";
reportTimeList["000819"]="2014-10-25";
reportTimeList["600888"]="2014-10-25";
reportTimeList["002500"]="2014-10-27";
reportTimeList["000798"]="2014-10-27";
reportTimeList["002296"]="2014-10-27";
reportTimeList["000976"]="2014-10-27";
reportTimeList["000652"]="2014-10-27";
reportTimeList["000532"]="2014-10-27";
reportTimeList["002182"]="2014-10-27";
reportTimeList["002246"]="2014-10-28";
reportTimeList["000707"]="2014-10-28";
reportTimeList["002141"]="2014-10-28";
reportTimeList["002604"]="2014-10-28";
reportTimeList["002128"]="2014-10-28";
reportTimeList["002206"]="2014-10-28";
reportTimeList["000636"]="2014-10-28";
reportTimeList["000756"]="2014-10-28";
reportTimeList["000969"]="2014-10-28";
reportTimeList["000881"]="2014-10-28";
reportTimeList["600828"]="2014-10-28";
reportTimeList["002184"]="2014-10-28";
reportTimeList["002083"]="2014-10-28";
reportTimeList["600529"]="2014-10-28";
reportTimeList["002010"]="2014-10-28";
reportTimeList["000983"]="2014-10-28";
reportTimeList["002171"]="2014-10-28";
reportTimeList["002058"]="2014-10-28";
reportTimeList["000411"]="2014-10-28";
reportTimeList["601137"]="2014-10-28";
reportTimeList["002513"]="2014-10-28";
reportTimeList["600525"]="2014-10-28";
reportTimeList["002332"]="2014-10-29";
reportTimeList["600861"]="2014-10-29";
reportTimeList["600456"]="2014-10-29";
reportTimeList["002032"]="2014-10-29";
reportTimeList["600303"]="2014-10-29";
reportTimeList["002082"]="2014-10-29";
reportTimeList["002480"]="2014-10-29";
reportTimeList["002254"]="2014-10-29";
reportTimeList["002090"]="2014-10-29";
reportTimeList["002034"]="2014-10-29";
reportTimeList["002211"]="2014-10-29";
reportTimeList["601918"]="2014-10-29";
reportTimeList["600328"]="2014-10-29";
reportTimeList["600876"]="2014-10-29";
reportTimeList["601011"]="2014-10-29";
reportTimeList["600780"]="2014-10-29";
reportTimeList["002102"]="2014-10-29";
reportTimeList["600981"]="2014-10-29";
reportTimeList["002078"]="2014-10-30";
reportTimeList["000564"]="2014-10-30";
reportTimeList["000027"]="2014-10-30";
reportTimeList["600723"]="2014-10-30";
reportTimeList["601101"]="2014-10-30";
reportTimeList["002097"]="2014-10-30";
reportTimeList["600351"]="2014-10-30";
reportTimeList["002042"]="2014-10-30";
reportTimeList["002188"]="2014-10-30";
reportTimeList["002103"]="2014-10-30";
reportTimeList["002094"]="2014-10-30";
reportTimeList["600167"]="2014-10-30";
reportTimeList["002425"]="2014-10-30";
reportTimeList["000839"]="2014-10-30";
reportTimeList["000726"]="2014-10-30";
reportTimeList["002245"]="2014-10-30";
reportTimeList["601001"]="2014-10-30";
reportTimeList["002464"]="2014-10-30";
reportTimeList["600290"]="2014-10-31";
reportTimeList["600713"]="2014-10-31";
reportTimeList["600605"]="2014-10-31";
reportTimeList["000410"]="2014-10-31";
reportTimeList["000615"]="2014-10-31";
reportTimeList["600302"]="2014-10-31";
reportTimeList["600782"]="2014-10-31";
reportTimeList["000713"]="2014-10-31";
reportTimeList["600360"]="2014-10-31";
reportTimeList["000903"]="2014-10-31";
reportTimeList["600337"]="2014-10-31";
reportTimeList["600375"]="2014-10-31";
reportTimeList["600005"]="2014-10-31";
reportTimeList["600701"]="2014-10-31";
reportTimeList["000973"]="2014-10-31";
reportTimeList["600883"]="2014-10-31";
reportTimeList["600105"]="2014-10-31";
reportTimeList["600071"]="2014-10-31";
reportTimeList["000880"]="2014-10-31";
reportTimeList["600493"]="2014-10-31";




    /** 
     * 时间对象的格式化; 
     */  
    Date.prototype.format = function(format) {  
        /* 
         * eg:format="yyyy-MM-dd hh:mm:ss"; 
         */  
        var o = {  
            "M+" : this.getMonth() + 1, // month  
            "d+" : this.getDate(), // day  
            "h+" : this.getHours(), // hour  
            "m+" : this.getMinutes(), // minute  
            "s+" : this.getSeconds(), // second  
            "q+" : Math.floor((this.getMonth() + 3) / 3), // quarter  
            "S" : this.getMilliseconds()  
            // millisecond  
        }  
      
        if (/(y+)/.test(format)) {  
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4  
                            - RegExp.$1.length));  
        }  
      
        for (var k in o) {  
            if (new RegExp("(" + k + ")").test(format)) {  
                format = format.replace(RegExp.$1, RegExp.$1.length == 1  
                                ? o[k]  
                                : ("00" + o[k]).substr(("" + o[k]).length));  
            }  
        }  
        return format;  
    }  

var today = new Date().format('yyyy-MM-dd hh:mm:ss');
today = today.substr(0,10); 

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
        $('#sh204004').css("border-top","solid red");

        if($('.tool_col_1').length == 0){
            return;
        }
        button_div = $('.tool_col_1');
        var tabs = $('<ul class="tab market"></ul>');
        var clean_button = $('<li>clean</li>');
        var links_button = $('<li>links</li>');
        var filter_button = $('<li>filter</li>');
        tabs.append(links_button).append(filter_button).appendTo(button_div);

        clean_button.click(function(){clean_page();});
        links_button.click(function(){add_link();});
        filter_button.click(function(){
            $('dl').hide(); $('.hasReport').show();
        });
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
            if(today>reportTimeList[id]){
                first_th.attr('border', 'solid 1px blue');
                first_th.append($('<b>*</b>'));
                line.addClass('hasReport');
            }
            
            var aa = $('<a class="ed_link_class"></a>').attr('href', link_a + id + '.shtml').attr("target","_blank").text('H').attr('style', 'padding-left: 5px;padding-right: 5px;').attr('title', reportTimeList[id]);
            first_th.append(aa);

            var ab = $('<a class="ed_link_class"></a>').attr('href', link_b + vid).attr("target","_blank").text('E').attr('style', 'padding-left: 5px;padding-right: 5px;').attr('title', reportTimeList[id]);
            first_th.append(ab);
            
            var ac = $('<a class="ed_link_class"></a>').attr('href', link_c + id).attr("target","_blank").text('Q').attr('style', 'padding-left: 5px;padding-right: 5px;').attr('title', reportTimeList[id]);
            first_th.append(ac);
        });
    }
    
    
