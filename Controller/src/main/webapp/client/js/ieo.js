jQuery(document).ready(function($) {
    /**
     * Pattern for email field
     */
    var rexexpEmail = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\\])";
    var emailPattern = new RegExp(rexexpEmail);

    $("#ieo-email").on("change keyup", function(){
        if(emailPattern.test($("#ieo-email").val())) {
            $("#subscribe-btn-id").prop('disabled', false);
            $("#subscribe-btn-id").css("background-color","#1b5ff8");
        }else{
            $("#subscribe-btn-id").prop('disabled', true);
            $("#subscribe-btn-id").css("background-color","#b7caf7");
        }
    });
});

function subscribeOnInitialExchangeOfferings() {
    var data = $('#subscribe-form-id').serialize();

    $.ajax({
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        url: '/ieo/subscribe',
        type: 'POST',
        data: data,
        success: function (data) {
            successNoty(data.message)
        }, error: function (data) {
            errorNoty(data.responseJSON.message);
        }, complete : function () {
            $("#ieo-email").val('');

            $("#subscribe-btn-id").prop('disabled', true);
            $("#subscribe-btn-id").css("background-color","#b7caf7");
        }
    });
}




