/**
 * Created by Valk on 20.06.2016.
 */
function SettingsClass() {
    if (SettingsClass.__instance) {
        return SettingsClass.__instance;
    } else if (this === window) {
        return new SettingsClass(currentCurrencyPair);
    }
    SettingsClass.__instance = this;
    /**/
    var that = this;
    /**/
    var showLog = false;
    /**/
    this.tabIdx = 0;
   /* const $telegramModal = $('#telegram_connect_modal');
    const $telegramMessagePrice = $('#telegram_mssg_price');
    const $telegramSubscrPrice = $('#telegram_subscr_price');
    const $telegramCode = $('#telegram_code');
    const $smsModal = $('#sms_connect_modal');*/
    const $googleModal = $('#google_authenticator_modal');
    const $smsNumberError = $('#phone_error');
   /* const $smsMessagePrice = $('#sms_mssg_price');
    const $smsSubscrPrice = $('#sms_subscr_price');
    const $smsNumberInput = $('#sms_number_input');*/

    const $pinDialogModal = $('#pin_modal');
    const $pinDialogText = $pinDialogModal.find('#pin_text');
    const $pinWrong = $pinDialogModal.find('#pin_wrong');
    const $pinSendButton = $("#check-pin-button");
    const $pinInput = $('#pin_code');

    /*===========================================================*/
    (function init() {
        that.tabIdx = $('#tabIdx').text();
        if (!that.tabIdx) {
            that.tabIdx = 0;
        }
        var $activeTabIdSpan = $('#activeTabId');
        if ($($activeTabIdSpan).length > 0) {
            var $settingsMenu = $('#user-settings-menu');
            $($settingsMenu).find('li:active').removeClass('active');
            var $activeLink =  $($settingsMenu).find('a[href=#' + $($activeTabIdSpan).text() +  ']')
            $($activeLink).click();
        }

        getG2fa();


        /* setActiveSwitcher();
         switchPassTab();*/
        /**/
        $('.orderForm-toggler').on('click', function(e){
            that.tabIdx = $(this).index();
            setActiveSwitcher();
            switchPassTab();
        });
       /* checkSmsNumber();*/
    })();

     function setActiveSwitcher(){
         $('.orderForm-toggler').removeClass('active');
         $('.orderForm-toggler:eq('+that.tabIdx+')').addClass('active');
     }

    function switchPassTab(){
        var tabId = $('.orderForm-toggler.active').data('tabid');
        $('#'+tabId).siblings().removeClass('active');
        $('#'+tabId).addClass('active');
        blink($('#passwords-changing').find('[for="user-password"]'));
        blink($('#passwords-changing').find('[for="userFin-password"]'));
    }

    $('#sessionTime').on('change keyup', function() {
        console.log('change');
        var value = $(this).val(); // get the current value of the input field.
        var sendButton = $('#submitSessionOptionsButton');
        if (!value || isNaN(value)) {
            sendButton.prop('disabled', true);
        } else {
            sendButton.prop('disabled', false);
        }
    });

    if (window.location.href.indexOf('?2fa') > 0) {
        $('html, body').animate({
            scrollTop: $("#2fa-options").offset().top-200
        }, 2000);
        $('#2fa_cell').css('color', 'red').css('text-decoration', 'underline');
    }

    /*$smsNumberInput.on('input', function () {
        checkSmsNumber();
    });

    function checkSmsNumber() {
        var val = $smsNumberInput.val().replace('+','').replace(" ", "").replace("-", "");
        if (isNumber(val)) {
            $('#sms_check_number').prop('disabled', false);
        } else {
            $('#sms_check_number').prop('disabled', true);
        }
    }*/

    $('#google2fa_send_code_button').on('click', function () {
        $smsNumberError.text('');
        var code = $('#google2fa_code_input').val();
        $.ajax({
            url: '/settings/2FaOptions/verify_google2fa?code=' + code,
            type: 'GET',
            success: function (data) {
                var form = document.createElement("form");
                form.setAttribute("method", "POST");
                form.setAttribute("action", "/settings/2FaOptions/google2fa_connect");
                var hiddenField1 = document.createElement("input");
                     hiddenField1.setAttribute("type", "hidden");
                     hiddenField1.setAttribute("name", "_csrf");
                     hiddenField1.setAttribute("value", $("input[name='_csrf']").val());
                var hiddenField2 = document.createElement("input");
                     hiddenField2.setAttribute("type", "hidden");
                     hiddenField2.setAttribute("name", "connect");
                     hiddenField2.setAttribute("value", "true");
                form.appendChild(hiddenField1);
                form.appendChild(hiddenField2);
                document.body.appendChild(form);
                form.submit();
            },
            error: function (data) {
                $smsNumberError.text(data.responseJSON.detail);
            }
        });
    });

    $('#disconnect_google2fa_send_code_button').on('click', function () {
        $smsNumberError.text('');
        var code = $('#disconnect_google2fa_code_input').val();
        $.ajax({
            url: '/settings/2FaOptions/verify_google2fa?code=' + code,
            type: 'GET',
            success: function (data) {
                var form = document.createElement("form");
                form.setAttribute("method", "POST");
                form.setAttribute("action", "/settings/2FaOptions/google2fa_disconnect");
                var hiddenField1 = document.createElement("input");
                     hiddenField1.setAttribute("type", "hidden");
                     hiddenField1.setAttribute("name", "_csrf");
                     hiddenField1.setAttribute("value", $("input[name='_csrf']").val());
                var hiddenField2 = document.createElement("input");
                     hiddenField2.setAttribute("type", "hidden");
                     hiddenField2.setAttribute("name", "connect");
                     hiddenField2.setAttribute("value", "false");
                form.appendChild(hiddenField1);
                form.appendChild(hiddenField2);
                document.body.appendChild(form);
                form.submit();
            },
            error: function (data) {
                $smsNumberError.text(data.responseJSON.detail);
            }
        });
    });

    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    $('#backed_up_16').click(function () {

        if ($(this).is(':checked') && $('#google2fa_code_input').val().length > 0 ) {
            $('#google2fa_send_code_button').removeAttr('disabled');

        } else {
            $('#google2fa_send_code_button').attr('disabled', true);
        }
    });

    $('#google2fa_code_input').keyup(function () {

        if ($('#backed_up_16').is(':checked') && $('#google2fa_code_input').val().length > 0 ) {
            $('#google2fa_send_code_button').removeAttr('disabled');

        } else {
            $('#google2fa_send_code_button').attr('disabled', true);
        }
    });

    $('#disconnect_google2fa_code_input').keyup(function () {

        if ($('#disconnect_google2fa_code_input').val().length > 0 ) {
            $('#disconnect_google2fa_send_code_button').removeAttr('disabled');

        } else {
            $('#disconnect_google2fa_send_code_button').attr('disabled', true);
        }
    });

    $('.g2fa_connect_buttonr').on('change', function(e){
        if (this.checked) {
            $('.g2fa_connect_button').prop('disabled', false);
        } else {
            $('.g2fa_connect_button').prop('disabled', true);
        }
    });

   /* $('#subscribe_GOOGLE_AUTHENTICATOR').on('click', function() {
        $('#google2fa_connect_block').show();
        $('#google2fa_disconnect_block').hide();
        $googleModal.modal();
       $.ajax(
           "/settings/2FaOptions/google2fa",
           {
            headers:
                {
                'X-CSRF-Token': $("input[name='_csrf']").val()
                },
            type: 'POST',
        }).success(function (data) {
           if($('#qr').find('img').length==1) {
               $("#qr").append('<img tyle="width: 100%; height: 100%;" src="'+data.message+'" />').show();
           }
        });
    });*/

    function getG2fa() {
        $.ajax(
            "/settings/2FaOptions/google2fa",
            {
                headers:
                    {
                        'X-CSRF-Token': $("input[name='_csrf']").val()
                    },
                type: 'POST'
            }).success(function (data) {
                if (data) {
                    showg2faConnect();
                    $('#g2fa_code').text(data.code);
                    $("#g2fa_qr_code").append('<img tyle="width: 100%; height: 100%;" src="' + data.message + '" />').show();
                } else {
                    showg2faConnected();
                }
        });
    }

    function showg2faConnect(){
        $('.g2fa_connect').show();
        $('.g2fa_connected').hide();
    }

    function showg2faConnected() {
        $('.g2fa_connect').hide();
        $('.g2fa_connected').show();
    }


   /* $('#reconnect_GOOGLE_AUTHENTICATOR').on('click', function() {
        $('#google2fa_disconnect_block').show();
        $('#google2fa_connect_block').hide();
        $googleModal.modal();
        $.ajax(
            "/settings/2FaOptions/google2fa",
            {
                headers:
                    {
                        'X-CSRF-Token': $("input[name='_csrf']").val()
                    },
                type: 'POST',
            }).success(function (data) {
            if($('#disconnect_qr').find('img').length==1) {
                $("#disconnect_qr").append('<img tyle="width: 100%; height: 100%;" src="'+data.message+'" />').show();
            }
        });
    });*/

    $('.update_set_button').on('click', function() {
        console.log('clivk');
        $.ajax({
            url: '/settings/2FaOptions/submit',
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            data: $('#2faSettings_form').serialize()
        }).success(function (result, textStatus, xhr) {
                $pinDialogModal.modal();
                $pinDialogText.text(result.detail);
        });
    });

    $pinInput.on('input', function (e) {
        checkPinInput()
    });

    function checkPinInput() {
        var value = $pinInput.val();
        if (value.length > 2 && value.length < 15 ) {
            $pinSendButton.prop('disabled', false);
        } else {
            $pinSendButton.prop('disabled', true);
        }
    }

    $pinSendButton.on('click', function () {
        sendPin($pinInput.val());
    });

    function sendPin(pin) {
        $pinWrong.hide();
        $.ajax({
            url: '/settings/2FaOptions/change?pin=' + pin,
            async: true,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            contentType: 'application/json'
        }).success(function (result, textStatus, xhr) {
            if (xhr.status === 200) {
                $pinDialogModal.modal("hide");
                window.location.replace("/settings?success2fa");
            } else {
                $pinWrong.show();
                $pinDialogText.text(result.message);
                if (result.needToSendPin) {
                    successNoty(result.message)
                }
            }
        }).error(function (result) {

        }).complete(function () {
            $pinInput.val("");
            $pinSendButton.prop('disabled', true);
        });
    }

}

$(function () {
    const passwordPatternLettersAndNumbers = new RegExp("^(?=.*\\d)(?=.*[a-zA-Z])[\\w]{8,20}$");
    const passwordPatternLettersAndCharacters = new RegExp("^(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$");
    const passwordPatternLettersAndNumbersAndCharacters = new RegExp("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$");

    const fieldContainsSpace = new RegExp("\\s");

    $("#password-change-button").click(function(e) {
        e.preventDefault();
        $.ajax({
            url: '/settings/changePassword/submit',
            type: 'POST',
            data: $('#settings-user-password-form').serialize(),
            success: function (data) {
                successNoty(data.message)
            }, error: function (data) {
                errorNoty(data.responseJSON.message);
            }, complete : function () {
                $('#user-confirmpassword').val('');
                $('#user-password').val('');
                $('#confirmNewPassword').val('');
                $('#user-confirmpassword').attr('readonly', true);
                $('#confirmNewPassword').attr('readonly', true);
                $('.repass').css("display", "none");
                $('.repass-error').css("display", "none");
                $('#password-change-button').attr('disabled', true);
            }
        });
    });

    if (document.getElementById("password-change-button")) {
        checkPasswordFieldsOnFillInUserSettings();
    }

    if (document.getElementById("user-password")) {
        checkOldPasswordField();
    }

    if (document.getElementById("user-password") && document.getElementById("user-confirmpassword")) {
        checkOldPasswordAndNewPasswordField();
    }

    $('#user-confirmpassword').keyup(debounce(function(){
        /**
         * Start validation for password confirm
         */
        var pass = $('#user-confirmpassword').val();
        var repass = $('#confirmNewPassword').val();

        if (pass && (pass === repass)) {
            $('.repass').css("display", "block");
            $('.repass-error').css("display", "none");
        }
        else {
            $('.repass-error').css("display", "block");
            $('.repass').css("display", "none");
        }
        checkPasswordFieldsOnFillInUserSettings();
        /**
         * End validation for password confirm
         */

        $('#new_password_wrong').css('display', 'none');
        $('#new_password_required').css('display', 'none');

        if(!pass) {
            $('#new_password_wrong').addClass('field__input--error').siblings('.field__label').addClass('field__label--error');
            $('#new_password_required').css('display', 'block');
            $("#password-change-button").attr('disabled', true);
            checkPasswordFieldsOnFillInUserSettings();
            return;
        }
        if ((passwordPatternLettersAndNumbers.test(pass) || passwordPatternLettersAndCharacters.test(pass)
            || passwordPatternLettersAndNumbersAndCharacters.test(pass)) && !fieldContainsSpace.test(pass)) {
            $('#user-confirmpassword').removeClass('field__input--error').siblings('.field__label').removeClass('field__label--error');
            $("#password-change-button").attr('disabled', false);
            checkPasswordFieldsOnFillInUserSettings();
        } else {
            $('#user-confirmpassword').addClass('field__input--error').siblings('.field__label').addClass('field__label--error');
            $('#new_password_wrong').css('display', 'block');
            $("#password-change-button").attr('disabled', true);
            checkPasswordFieldsOnFillInUserSettings();
        }
    },100));

    $("#confirmNewPassword").keyup(function () {
        var pass = $('#user-confirmpassword').val();
        var repass = $('#confirmNewPassword').val();
        if (repass && (pass === repass)) {
            $('.repass').css("display", "block");
            $('.repass-error').css("display", "none");
        } else {
            $('.repass-error').css("display", "block");
            $('.repass').css("display", "none");
        }
        checkPasswordFieldsOnFillInUserSettings();
    });

    $('#user-password').keyup(checkOldPasswordField);
    $('#user-password, #user-confirmpassword').keyup(checkOldPasswordAndNewPasswordField);
    $('#user-password, #user-confirmpassword, #confirmNewPassword').keyup(checkPasswordFieldsOnFillInUserSettings);

});

/**
 * Check password fields on fill for change password by user in user settings.
 */
function checkPasswordFieldsOnFillInUserSettings() {
    var password = $('#user-password').val();
    var newPassword = $('#user-confirmpassword').val();
    var confirmNewPassword = $('#confirmNewPassword').val();
    if (password && newPassword && confirmNewPassword && (newPassword === confirmNewPassword)) {
        $("#password-change-button").attr('disabled', false);
    } else {
        $("#password-change-button").attr('disabled', true);
    }
}

/**
 * Remove disabled from buttons newPassword, when old password field fill.
 */
function checkOldPasswordField(){
    var password = $('#user-password').val();

    if (password) {
        $("#user-confirmpassword").attr('readonly', false);
    } else {
        $("#user-confirmpassword").attr('readonly', true);
    }
}

/**
 * Remove disabled from button confirmNewPassword, when old password and new password fields fill.
 */
function checkOldPasswordAndNewPasswordField(){
    var password = $('#user-password').val();
    var newPassword = $('#user-confirmpassword').val();

    if (password && newPassword) {
        $("#confirmNewPassword").attr('readonly', false);
    } else {
        $("#confirmNewPassword").attr('readonly', true);
    }
}

/**
 * Do some function after wait interval
 * @param func
 * @param wait
 * @param immediate
 * @returns {Function}
 */
function debounce(func, wait, immediate) {
    var timeout;
    return function() {
        var context = this, args = arguments;
        var later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
}
