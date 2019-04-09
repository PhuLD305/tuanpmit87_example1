$(document).ready(function(){
    getCompanyInfo($("#companyInternalId option:selected").val());
    $("#companyInternalId").change(function(){
        $("#hasExist").prop("checked", true);
        changeRadio(1);
        getCompanyInfo(this.value);
    });
    $("#btn_update").click(function(){
        window.f_new.submit();
    });
    $(".hasExist").change(function(){
        changeRadio((this).value);
    });
});

function getCompanyInfo(companyId) {
    $.ajax({
        url: "/company-info?id="+ companyId,
        success: function(result){
            $(".company_name").html(result.companyName);
            $(".address").html(result.address);
            $(".email").html(result.email);
            $(".telephone").html(result.telephone);
            $("#box-company-info").show();
        }
    });
}

function changeRadio(hasExist) {
    if (hasExist == 1) {
        $("#box-company-info").show();
        $("#box-company-new").hide();
    } else {
        $("#box-company-info").hide();
        $("#box-company-new").show();
    }
}