// 使用心跳来判断用户是否直接关闭浏览器
$(function () {
    var tickFun = function () {
        $.ajax({
            type:"post",
            url:"/heartbeat/tick",
            data:{"tick" : 1},
            dataType:"json",
            success:function (data) {
                if(data.tick){
                    setTimeout(tickFun, data.timeout);
                }
            },
            error:function(msg){
                console.log(msg);
            }
        });
    };
    tickFun();
});