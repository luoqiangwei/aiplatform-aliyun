$(function () {
    $(".show").height($(".show").width());
    $(".show").css('line-height', $(".show").width() + "px");
});

$(window).resize(function() {
    $(".show").height($(".show").width());
    $(".show").css('line-height', $(".show").width() + "px");
});