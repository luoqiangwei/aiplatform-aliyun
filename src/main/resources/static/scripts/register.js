$(function () {
    $("#submit").click(function () {
        $.ajax({
            type:"post",
            url:"register?method=register",
            data:$("#form").serialize(),
            dataType:"json",
            success:function (data) {
                if(data.error){
                    $("#error").html(data.msg);
                }else{
                    window.location.href = "login?method=forward";
                }
            },
            error:function(msg){
                console.log(msg);
            }
        });
    });

    $("#name").blur(function () {
         $.ajax({
             type: "post",
             url: "register?method=checkUsername",
             data: $("#name").serialize(),
             dataType: "json",
             success:function(data){
                 if(data.error){
                     $("#error").text(data.msg);
                 }else{
                     $("#error").text("");
                 }
             },
             error:function (msg) {
                 console.log(msg);
             }
        
         });
    });

    $("#pwd").blur(function () {
        if($("#pwd").val().length < 8){
            $("#error").text("密码过短（至少为8位）");
        }else{
            $("#error").text("");
        }
    });
    
    $("#confirmPwd").blur(function () {
        if($("#pwd").val() != $("#confirmPwd").val()){
            $("#error").text("两次密码不一致");
        }else{
            $.ajax({
                type: "post",
                url: "register?method=checkPassword",
                data: {"pwd": $("#pwd").val(), "confirmPwd" : $("#confirmPwd").val()},
                dataType: "json",
                success:function(data){
                    if(data.error){
                        $("#error").text(data.msg);
                    }else{
                        $("#error").text("");
                    }
                },
                error:function (msg) {
                    console.log(msg);
                }
            });
        }
    });
});