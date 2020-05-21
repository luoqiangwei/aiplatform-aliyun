$(function () {
    $("#title").blur(function () {
        if($("#title").val().trim().length == 0){
            $("#error").text("必须输入投票标题");
        }else{
            $("#error").text("");
        }
    });

    $("#startDate").blur(function () {
        if($("#startDate").val().trim().length == 0){
            $("#error").text("必须选择开始时间");
        }else{
            $("#error").text("");
        }
    });

    $("#endDate").blur(function () {
        if($("#endDate").val().trim().length == 0){
            $("#error").text("必须选择结束时间");
        }else{
            $("#error").text("");
        }
    });

    $("#option1").blur(function () {
        if($("#option1").val().trim().length == 0){
            $("#error").text("投票选项至少填写两项");
        }else{
            $("#error").text("");
        }
    });

    $("#option2").blur(function () {
        if($("#option2").val().trim().length == 0){
            $("#error").text("投票选项至少填写两项");
        }else{
            $("#error").text("");
        }
    });
});