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
    const $telegramModal = $('#telegram_connect_modal');
    const $telegramMessagePrice = $('#telegram_mssg_price');
    const $telegramSubscrPrice = $('#telegram_subscr_price');
    const $telegramCode = $('#telegram_code');
    const $smsModal = $('#sms_connect_modal');
    const $smsNumberError = $('#phone_error');
    const $smsMessagePrice = $('#sms_mssg_price');
    const $smsSubscrPrice = $('#sms_subscr_price');
    const $smsNumberInput = $('#sms_number_input');

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
            var $activeLink =  $($settingsMenu).find('a[href=#' + $($activeTabIdSpan).text() +  ']');
            $($activeLink).click();
        }

        /* setActiveSwitcher();
         switchPassTab();*/

        /**/
        $('.orderForm-toggler').on('click', function(e){
            that.tabIdx = $(this).index();
            setActiveSwitcher();
            switchPassTab();
        });
        checkSmsNumber();
    })();

    /* function setActiveSwitcher(){
         $('.orderForm-toggler').removeClass('active');
         $('.orderForm-toggler:eq('+that.tabIdx+')').addClass('active');
     }*/

    /*function switchPassTab(){
        var tabId = $('.orderForm-toggler.active').data('tabid');
        $('#'+tabId).siblings().removeClass('active');
        $('#'+tabId).addClass('active');
        blink($('#passwords-changing').find('[for="user-password"]'));
        blink($('#passwords-changing').find('[for="userFin-password"]'));
    }*/

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

    function resetSmsConnectModal() {
        $('#sms_info_block').hide();
        $('#sms_code_block').hide();
        $('#sms_connect_block').hide();
        $smsNumberError.text('');
    }

    $('#subscribe_SMS').on('click', function() {
        connectOrReconnect();
    });

    $('#reconnect_SMS').on('click', function() {
        connectOrReconnect();
    });

    function connectOrReconnect() {
        resetSmsConnectModal();
        $('#sms_connect_block').show();
        $smsModal.modal();
    }

    $('#sms_check_number').on('click', function() {
        $smsNumberError.text('');
        var val = $smsNumberInput.val().replace('+','').replace(" ", "").replace("-", "");
        $.ajax({
            url: '/settings/2FaOptions/preconnect_sms?number=' + val,
            type: 'GET',
            success: function (data) {
                $smsMessagePrice.text(data);
                $('#sms_instruction').show();
                $('#sms_connect_button').prop('disabled', false);
            }, error: function (data) {
                $smsNumberError.text(data.responseJSON.detail);
            }
        });
    });

    $smsNumberInput.on('input', function () {
        checkSmsNumber();
    });

    function checkSmsNumber() {
        var val = $smsNumberInput.val().replace('+','').replace(" ", "").replace("-", "");
        if (isNumber(val)) {
            $('#sms_check_number').prop('disabled', false);
        } else {
            $('#sms_check_number').prop('disabled', true);
        }
    }

    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    $('#sms_connect__reload_button').on('click', function() {
        location.reload();
    });

    $('#sms_connect_button').on('click', function() {
        $smsNumberError.text('');
        $.ajax({
            url: '/settings/2FaOptions/confirm_connect_sms',
            type: 'GET',
            success: function (data) {
                resetSmsConnectModal();
                $('#sms_code_input').val("");
                $('#sms_code_block').show();
            },
            error: function (data) {
                $smsNumberError.text(data.responseJSON.detail);
            }
        });
    });


    $('#sms_enter_code_button').on('click', function () {
        resetSmsConnectModal();
        $('#sms_code_input').val("");
        $('#sms_code_block').show();
    });

    $('#sms_send_code_button').on('click', function () {
        $smsNumberError.text('');
        var code = $('#sms_code_input').val();
        $.ajax({
            url: '/settings/2FaOptions/verify_connect_sms?code=' + code,
            type: 'GET',
            success: function (data) {
                location.reload();
            },
            error: function (data) {
                $smsNumberError.text(data.responseJSON.detail);
            }
        });
    });

    $('#sms_code_input').on('input', function () {
        var code = $('#sms_code_input').val();
        if (isNumber(code)) {
            $('#sms_send_code_button').prop('disabled', false);
        } else {
            $('#sms_send_code_button').prop('disabled', true);
        }
    });



    $('.contact_info').on('click', function() {
        resetSmsConnectModal();
        var id = $(this).data("id");
        var contact = $(this).data("contact");
        console.log("id " + id);
        console.log("contact " + contact);
        $.ajax({
            url: '/settings/2FaOptions/contact_info?id=' + id,
            type: 'GET',
            success: function (data) {
                console.log(data);
                data = JSON.parse(data);
                $smsModal.modal();
                $('#sms_info_block').show();
                $('#sms_number').text(data.contact);
                $('#sms_price').text(data.price);
            }
        });
    });



    $('#subscribe_TELEGRAM').on('click', function() {
        $.ajax({
            url: '/settings/2FaOptions/getNotyPrice?id=3',
            type: 'GET',
            success: function (data) {
                $telegramMessagePrice.text(data.messagePrice == null ? 0 : data.messagePrice);
                $telegramSubscrPrice.text(data.subscriptionPrice);
                if (data.code != undefined) {
                    $('.code').show();
                    $telegramCode.text(data.code);
                    $('#telegram_pay_button').hide();
                    $('#telegram_cancel_button').hide();
                    $('#telegram_back_button').show();
                }
                $telegramModal.modal();
            }
        });
    });

    $('#telegram_pay_button').on('click', function() {
        $.ajax({
            url: '/settings/2FaOptions/connect_telegram',
            type: 'GET',
            success: function (data) {
                $telegramModal.modal();
                $('.code').show();
                $telegramCode.text(data);
                $('#telegram_pay_button').hide();
                $('#telegram_cancel_button').hide();
                $('#telegram_back_button').show();
            }, error: function (data) {
                console.log(data);
            }
        });
    });

    $('#reconnect_TELEGRAM').on('click', function() {
        $.ajax({
            url: '/settings/2FaOptions/reconnect_telegram',
            type: 'GET',
            success: function (data) {
                $('#telegram_reconnect_block').show();
                $('#telegram_connect_block').hide();
                $('#telegram_pay_button').hide();
                $('#telegram_cancel_button').hide();
                $('#telegram_back_button').show();
                $('.code').show();
                $telegramModal.modal();
                $telegramCode.text(data);

            }
        });
    });

}

/**
 * Password element
 */
var password = $('#user-password');
/**
 * New password element
 */
var newPassword = $('#user-confirmpassword');
/**
 * Confirm new password element
 */
var confirmNewPassword = $('#confirmNewPassword');
/**
 * Password submit button
 */
var passwordChangeButton = $("#password-change-button");

/**
 * Password wrong error
 */
var errorPasswordWrong = $('#new_password_wrong');
/**
 * Password required error
 */
var errorPasswordRequired = $('#new_password_required');

/**
 * Symbol (okay) when newPassword and confirmNewPassword identity (equal one to one)
 */
var symbolOkayConfirmPassword = $('.repass');
/**
 * Symbol (not okay) when newPassword and confirmNewPassword identity (not equal one to one)
 */
var symbolNotOkayConfirmPassword = $('.repass-error');

/**
 * Password patterm | START
 */
const passwordPatternLettersAndNumbers = new RegExp("^(?=.*\\d)(?=.*[a-zA-Z])[\\w]{8,20}$");
const passwordPatternLettersAndCharacters = new RegExp("^(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$");
const passwordPatternLettersAndNumbersAndCharacters = new RegExp("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$");

const fieldContainsSpace = new RegExp("\\s");
/**
 * Password pattern | END
 */

$(function () {
    passwordChangeButton.click(function(e) {
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
                password.val('');
                newPassword.val('');
                confirmNewPassword.val('');

                newPassword.attr('readonly', true);
                confirmNewPassword.attr('readonly', true);

                errorPasswordWrong.css('display', 'none');
                errorPasswordRequired.css('display', 'none');

                symbolOkayConfirmPassword.css("display", "none");
                symbolNotOkayConfirmPassword.css("display", "none");

                passwordChangeButton.attr('disabled', true);
            }
        });
    });

    /**
     * Start validation for password confirm
     */
    newPassword.keyup(function(){
        if (newPassword.val() && (newPassword.val() === confirmNewPassword.val())) {
            symbolOkayConfirmPassword.css("display", "block");
            symbolNotOkayConfirmPassword.css("display", "none");
        }
        else {
            symbolOkayConfirmPassword.css("display", "none");
            symbolNotOkayConfirmPassword.css("display", "block");
        }
        checkPasswordFieldsOnFillInUserSettings();
    });

    confirmNewPassword.keyup(function () {
        if (confirmNewPassword.val() && (newPassword.val() === confirmNewPassword.val())) {
            symbolOkayConfirmPassword.css("display", "block");
            symbolNotOkayConfirmPassword.css("display", "none");
        } else {
            symbolOkayConfirmPassword.css("display", "none");
            symbolNotOkayConfirmPassword.css("display", "block");
        }
        checkPasswordFieldsOnFillInUserSettings();
    });
    /**
     * End validation for password confirm
     */

    password.keyup(checkOldPasswordField);

    password.keyup(checkOldPasswordAndNewPasswordField);
    newPassword.keyup(checkOldPasswordAndNewPasswordField);

    confirmNewPassword.keyup(checkPasswordFieldsOnFillInUserSettings);
});

/**
 * Check password fields on fill and correct input (validation pass) for change password by user in user settings.
 */
function checkPasswordFieldsOnFillInUserSettings() {
    errorPasswordWrong.css('display', 'none');
    errorPasswordRequired.css('display', 'none');

    if(!newPassword) {
        newPassword.addClass('field__input--error').siblings('.field__label').addClass('field__label--error');
        errorPasswordRequired.css('display', 'block');
        passwordChangeButton.attr('disabled', true);
        return;
    }
    if (((passwordPatternLettersAndNumbers.test(newPassword) || passwordPatternLettersAndCharacters.test(newPassword)
        || passwordPatternLettersAndNumbersAndCharacters.test(newPassword)) && !fieldContainsSpace.test(newPassword))
        && (password && newPassword && confirmNewPassword && (newPassword === confirmNewPassword))) {

        newPassword.removeClass('field__input--error').siblings('.field__label').removeClass('field__label--error');
        passwordChangeButton.attr('disabled', false);

    } else {
        newPassword.addClass('field__input--error').siblings('.field__label').addClass('field__label--error');
        errorPasswordWrong.css('display', 'block');
        passwordChangeButton.attr('disabled', true);
    }

}

/**
 * Remove disabled from buttons newPassword, when old password field fill.
 */
function checkOldPasswordField(){
    if (password) {
        newPassword.attr('readonly', false);
    } else {
        newPassword.attr('readonly', true);
    }
}

/**
 * Remove disabled from button confirmNewPassword, when old password and new password fields fill.
 */
function checkOldPasswordAndNewPasswordField(){
    if (password && newPassword) {
        confirmNewPassword.attr('readonly', false);
    } else {
        confirmNewPassword.attr('readonly', true);
    }
}
