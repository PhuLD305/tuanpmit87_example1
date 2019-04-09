$(document).ready(function(){
    $("#btn_login").click(function(){
        window.f_login.submit();
    });
    $("#btn_search").click(function(){
        window.f_search.submit();
    });

    $("#company").change(function(){
        window.location.href = "/user-list?companyId="+this.value;
    });

    if ($("#sort").attr("alt") == "desc") {
        $("#sort").html("<a href=\"/user-list\">▲</a> ▼");
    } else {
        $("#sort").html("▲ <a href=\"/user-list?sort=desc\">▼</a>");
    }
});