var audio = '';
$(document).ready(function () {
    bind_button_events();
});

function bind_button_events() {
    // entry button
    $('#entry_btn').click(function () {
        $('#welcome_div').hide();
        $('#main_div').show();

        audio = $('#audio')[0];
        audio.load();

        check_already_running();
    });

    // start button
    $('#start').click(start_monitor);
    $('#shutdown').click(shutdown_monitor);
    $('#switch_cam').click(switch_cam);
    $('#halt_pc').click(halt_pc);
}

function handle_server_failure(){
    clean_up();
    show_msg({code:'监控现在可能无法工作!', msg:'调用服务器失败!'});
}

function start_monitor() {
    $.ajax({
        dataType: "json",
        url: '/action?name=start',
        success: function(data) {
            show_msg(data);
            if(data['code'] == 'SUCC'){
                check_and_enable();
            }else if (data['code'] == 'NOOP'){
                if ($('#image_div img').size() == 0 ){
                    check_already_running();
                }
            }
        },
        error: handle_server_failure
    });
}

function shutdown_monitor() {
    clean_up();
    $.ajax({
        dataType: "json",
        url: '/action?name=shutdown',
        success: function(data) {
            show_msg(data);
        },
        error: handle_server_failure
    });
}

function show_unexpected_result(data){
    alert('不期望的操作结果! Code: ' + data['code'] + ' Msg: ' + data['msg']);
}

function show_msg(data){
    var c = data['code'];
    var m = data['msg'];
    var msg = '';
    if(c != ''){
        msg = c + ' - ';
    }
    msg += m;

    var p = $('<div style="font-size:150%;color:green" >'+msg+'</div>');
    $('#msg_div').empty().append(p);
}

function switch_cam() {
    $.ajax({
        dataType: "json",
        url: '/action?name=switch_cam',
        success: function(data) {
            show_msg(data);
            check_and_enable();
        },
        error: handle_server_failure
    });
}

function halt_pc() {
    $.ajax({
        dataType: "json",
        url: '/action?name=halt_pc',
        success: function(data) {
            show_msg(data);
            clean_up();
            $('body').empty();
            $('body').append($('<p style="font-size:250%;color:red">电脑已经关闭!</P>'));
        },
        error: handle_server_failure
    });
}

function clean_up(){
    $('#image_div').empty();
    cancel_alarm();
    if(set_interval){
        count = 0;
        window.clearInterval(interval_id);
    }
    set_interval = false;
}

var interval_id=0;
var set_interval = false;
function setup(){
    add_stream_image();

    if(!set_interval){
        interval_id = setInterval('loop_check()',5000);
    }
    set_interval = true;
}

function add_stream_image(){
    $('#key_img').remove();
    var img_div = $('#image_div').empty();
    var img = $('<img/>').attr('src', stream_image_url + '&times='+(new Date()).getTime()).attr('id', 'key_img');
    img_div.append(img);
}

var count = 0;
function loop_check(){
    count++;
    var pp = count % 3;
    check_alarm();
    if(pp == 0){
        check_heart_beat();
    }
}

function check_heart_beat(){
    $.ajax({
        dataType: "json",
        url: '/action?name=heart_beat',
        success: function(data) {
            show_msg(data);
            if(data['code'] == 'SUCC'){
                if ($('#image_div img').size() == 0 ){
                    $('#image_div').empty();
                    add_stream_image();
                }
            }else if(data['code'] == 'FAIL'){
                $('#image_div').empty();
                p = $('<p style="font-family:verdana;font-size:250%;color:blue">监控出现问题了, 有可能其他人点击关闭监控了. </p><br>' +
                    '<p style="font-family:verdana;font-size:250%;color:blue">请检查 1.尝试重新启动, 先点 关闭监控, 再点 启动监控 2. 电脑的电源是不是接通了?? </p>');
                $('#image_div').append(p);
            }
        },
        error: handle_server_failure
    });
}

function check_alarm(){
    $.ajax({
        dataType: "json",
        url: '/action?name=get_alarm',
        success: function(data) {
            if(data['code'] == 'SUCC'){
                if ($('#image_div img').size() > 0 && data['is_alarm']){
                    alarm();
                }

                if(!data['is_alarm'] || $('#image_div img').size() == 0){
                    cancel_alarm();
                }
            }
        },
        error: handle_server_failure
    });
}

function check_and_enable(){
    $.ajax({
        dataType: "json",
        url: '/action?name=heart_beat',
        success: function(data) {
            if(data['code'] == 'SUCC'){
                setup();
            }else if(data['code'] == 'FAIL'){
                setTimeout(check_and_enable, 5000);
            }
        },
        error: handle_server_failure
    });
}

function check_already_running(){
    $.ajax({
        dataType: "json",
        url: '/action?name=heart_beat',
        success: function(data) {

            if(data['code'] == 'SUCC'){
                show_msg({code:'', msg:'监控工作正常'});
                setup();
            }else{
                show_msg({code:'', msg:'监控没有启动'});
            }
        }
    });
}

function alarm() {
    $('body').attr('style','background: red');
    audio.play();
}

function cancel_alarm(){
    $('body').attr('style','background: white');
    audio.pause();
}

