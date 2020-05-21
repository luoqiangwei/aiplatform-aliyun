$(function () {

   $("#name").blur(function () {
       if($("#name").val().trim().length == 0){
           $("#error").text("用户名不能为空");
       }else{
           $("#error").text("");
       }
   });

   $("#pwd").blur(function () {
      if($("#pwd").val().trim().length < 8){
          $("#error").text("密码至少为8位");
      } else{
          $("#error").text("");
      }
   });

   $("#submit").click(function () {
       $.ajax({
           type:"post",
           url:"login?method=login",
           data:$("#form").serialize(),
           dataType:"json",
           success:function (data) {
               if(data.error){
                   $("#error").text(data.msg);
               }else {
                   window.location.href  = "list?method=forward";
               }
           },
           error:function(msg){
               console.log(msg);
           }
       });
   });

});