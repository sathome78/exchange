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
            var $activeLink =  $($settingsMenu).find('a[href=#' + $($activeTabIdSpan).text() +  ']');
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

    $('#g2fa_connect_button').on('click', function () {
        var data = $('#connect_g2fa').serialize();
        $('#2fa_user_pass').val('');
        $('#2fa_user_code').val('');
        $.ajax({
            url: '/settings/2FaOptions/google2fa_connect_check_creds',
            type: "POST",
            data: data,
            success: function (data) {
                $pinWrong.hide();
                $pinDialogModal.modal();
                $pinDialogText.text(data.detail);
            }, error : function (data) {
            }
        });
    });

    $pinSendButton.on('click', function () {
        var pinCode = $('#pin_code');
        $.ajax({
            url: '/settings/2FaOptions/google2fa_connect',
            type: "POST",
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            data: pinCode
        }).success(function (result, textStatus, xhr) {
            console.log(xhr.status);
            console.log(JSON.stringify(result));
            if (xhr.status === 200) {
                $pinDialogModal.modal("hide");
                getG2fa();
                successNoty(result.message);
            } else {
                $pinWrong.show();
                $pinDialogText.text(result.message);
                if (result.needToSendPin) {
                    successNoty(result.message)
                }
            }
        }).error(function (result, textStatus, xhr) {
            console.log(xhr.status);
            console.log(JSON.stringify(result));
            console.log('error');
            $pinDialogModal.modal("hide");
        }).complete(function () {
            $pinInput.val("");
            $pinSendButton.prop('disabled', true);
        });
    });

    $('#disconnect_google2fa').on('click', function () {
        var data = $('#disconnect_g2fa').serialize();
        $('#disconnect_pass').val('');
        $('#disconnect_code').val('');
        $.ajax({
            url: '/settings/2FaOptions/google2fa_disconnect',
            type: "POST",
            data: data,
            success: function (data) {
                getG2fa();
                successNoty(data.message);
            }, error : function (data) {
            }
        });
    });

    $('#backed_up_16').click(function () {
        checkConnectButton();
    });

    $('#2fa_user_code').keyup(function () {
        checkConnectButton();
    });

    $('#2fa_user_pass').keyup(function () {
        checkConnectButton();
    });

    $('#disconnect_pass').keyup(function () {
        checkDisconnectButton()
    });

    $('#disconnect_code').keyup(function () {
        checkDisconnectButton()
    });

    function checkConnectButton() {
        var code = $('#2fa_user_code').val();
        var pass = $('#2fa_user_pass').val();
        if ($('#backed_up_16').is(':checked') && code.length > 0 && pass.length > 0) {
            $('#g2fa_connect_button').removeAttr('disabled');
        } else {
            $('#g2fa_connect_button').attr('disabled', true);
        }
    }

    function checkDisconnectButton() {
        var code = $('#disconnect_code').val();
        var pass = $('#disconnect_pass').val();
        if (pass.length > 0 && code.length > 0) {
            $('#disconnect_google2fa').removeAttr('disabled');
        } else {
            $('#disconnect_google2fa').attr('disabled', true);
        }
    }

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
                    $("#g2fa_qr_code").replaceWith('<img id="g2fa_qr_code" tyle="width: 100%; height: 100%;" src="' + data.message + '" />').show();
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
