


$(function () {
    var $pinInput = $('#pin');
    var $pinSendButton = $('#send_pin');
    var $sendAgainButton = $('#send_pin_again');
    var $resultBlock = $('#send_pin_res');

    $pinInput.on('input', function () {
        checkPinInput()
    });

    function checkPinInput() {
       if ($pinInput.val().length > 3 && $pinInput.val().length < 20) {
           $pinSendButton.prop('disabled', false);
       } else {
           $pinSendButton.prop('disabled', true);
       }
    }

    $sendAgainButton.on('click', function(e) {
        var $this = $(this);

        $this.addClass('disabled');

        // разблокировка
        setTimeout(function() {
            $this.removeClass('disabled');
        }, 15*1000);
    });

    $sendAgainButton.on('click', function () {
        $.ajax('/login/new_pin_send', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $('#pin_code').serialize()
        }).success(function (response) {
            $('#res').text(response);
            $resultBlock.text('ok');
        }).fail(function (error, jqXHR, textStatus) {
           $resultBlock.text(error.responseText).css('color', 'red');
        });
    });

});


function close() {
    var banner = document.getElementById("banner");
    console.log(banner);
    banner.style.display = "none"
}

function sendLoginSuccessGtag() {
    gtag('event', 'login-success', {'event_category': 'login-form','event_label': 'login-success'});
}