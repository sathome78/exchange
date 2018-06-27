/**
 * Created by ValkSam
 */

$(function(){
    $(".currency_permissions__item").find("input[type=radio]").each(function (i, e) {
        e.checked=($(e).attr("data-checked")=="true");
    });

    $("#currency_permissions").find(".currency_permissions_btnOk").on("click", function(event){
        event.preventDefault();
        var $items = $(this).closest("#currency_permissions").find(".currency_permissions__item");
        var resultArray = [];
        var userId;
        for (var i = 0; i<$items.length; i++){
            var $checked = $($items.get(i)).find("input").filter(function(idx, elem){return elem.checked});
            var currency = {
                userId: $checked.attr("data-userId"),
                currencyId: $checked.attr("data-id"),
                invoiceOperationDirection: $checked.attr("data-direction"),
                invoiceOperationPermission: $checked.attr("value"),
            };
            resultArray.push(currency);
            if (!userId) {
                userId = $checked.attr("data-userId");
            };
        };
        $.ajax({
            url: '/2a8fy7b07dxe44/editCurrencyPermissions/submit',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
            },
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(resultArray),
            success: function () {
                window.location ="/2a8fy7b07dxe44/userInfo?id="+userId;
            }
        });
    });

    $(".sel_col").on("click", function () {
         var id = $(this).data('col');
         var $closestTable = $(this).closest('table');
         var $childsInputs = $closestTable.find('.col' + id).find('input');
         if(!$childsInputs.prop('disabled')) {
             $childsInputs.prop("checked", true).trigger("click");
         }
    });

});



$(".dropdown dt a").on('click', function() {
    $(".dropdown dd ul").slideToggle('fast');
});

$(".dropdown dd ul li a").on('click', function() {
    $(".dropdown dd ul").hide();
});

function getSelectedValue(id) {
    return $("#" + id).find("dt a span.value").html();
}

$(document).bind('click', function(e) {
    var $clicked = $(e.target);
    if (!$clicked.parents().hasClass("dropdown")) $(".dropdown dd ul").hide();
});

$('.mutliSelect input[type="checkbox"]').on('click', function() {

    var title = $(this).closest('.mutliSelect').find('input[type="checkbox"]').val(),
        title = $(this).val() + ",";

    if ($(this).is(':checked')) {
        var html = '<span title="' + title + '">' + title + '</span>';
        $('.multiSel').append(html);
        $(".hida").hide();
    } else {
        $('span[title="' + title + '"]').remove();
        var ret = $(".hida");
        $('.dropdown dt a').append(ret);

    }
});
