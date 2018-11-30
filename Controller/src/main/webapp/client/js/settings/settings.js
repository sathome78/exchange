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

    /**
     * Password patterm | START
     */
    var passwordPatternLettersAndNumbers = new RegExp("^(?=.*\\d)(?=.*[a-zA-Z])[\\w]{8,20}$");
    var passwordPatternLettersAndCharacters = new RegExp("^(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$");
    var passwordPatternLettersAndNumbersAndCharacters = new RegExp("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[@*%!#^!&$<>])[\\w\\W]{8,20}$");

    var fieldContainsSpace = new RegExp("\\s");
    /**
     * Password pattern | END
     */

    $(function () {

        $('#user-password').attr('readonly', false);
        $('#user-confirmpassword').attr('readonly', true);
        $('#confirmNewPassword').attr('readonly', true);

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
                    $('#user-password').val('');
                    $('#user-confirmpassword').val('');
                    $('#confirmNewPassword').val('');

                    $('#user-confirmpassword').attr('readonly', true);
                    $('#confirmNewPassword').attr('readonly', true);

                    $('#new_password_wrong').css('display', 'none');
                    $('#new_password_required').css('display', 'none');

                    $('.repass').css("display", "none");
                    $('.repass-error').css("display", "none");

                    $("#password-change-button").attr('disabled', true);
                }
            });
        });

        /**
         * Start validation for password confirm
         */
        $('#user-confirmpassword').keyup(function(){
            var newPassword = $('#user-confirmpassword').val();
            var confirmNewPassword = $('#confirmNewPassword').val();

            checkNewPasswordForDefence();
            checkOldPasswordAndNewPasswordField();
            if (newPassword && (newPassword === confirmNewPassword)) {
                $('.repass').css("display", "block");
                $('.repass-error').css("display", "none");
                $("#password-change-button").attr('disabled', false);
            }
            else {
                $('.repass').css("display", "none");
                $('.repass-error').css("display", "block");
                $("#password-change-button").attr('disabled', true);
            }
        });

        $('#confirmNewPassword').keyup(function () {
            var newPassword = $('#user-confirmpassword').val();
            var confirmNewPassword = $('#confirmNewPassword').val();

            if (confirmNewPassword && (newPassword === confirmNewPassword)) {
                $('.repass').css("display", "block");
                $('.repass-error').css("display", "none");
                $("#password-change-button").attr('disabled', false);
            } else {
                $('.repass').css("display", "none");
                $('.repass-error').css("display", "block");
                $("#password-change-button").attr('disabled', true);
            }
        });
        /**
         * End validation for password confirm
         */

        $('#user-password').keyup(function (){
            var newPassword = $('#user-confirmpassword').val();
            var confirmNewPassword = $('#confirmNewPassword').val();

            checkOldPasswordField();
            if (confirmNewPassword && (newPassword === confirmNewPassword)) {
                $('.repass').css("display", "block");
                $('.repass-error').css("display", "none");
                $("#password-change-button").attr('disabled', false);
            } else {
                $('.repass').css("display", "none");
                $('.repass-error').css("display", "block");
                $("#password-change-button").attr('disabled', true);
            }
        });
    });

    /**
     * Check new password on match pattern and not empty
     */
    function checkNewPasswordForDefence() {
        var newPassword = $('#user-confirmpassword').val();

        $('#new_password_wrong').css('display', 'none');
        $('#new_password_required').css('display', 'none');

        if(!newPassword) {
            $('#user-confirmpassword').addClass('field__input--error').siblings('.field__label').addClass('field__label--error');
            $('#new_password_required').css('display', 'block');
            $('#confirmNewPassword').attr('readonly', true);
            $("#password-change-button").attr('disabled', true);
            return;
        }

        if (((passwordPatternLettersAndNumbers.test(newPassword) || passwordPatternLettersAndCharacters.test(newPassword)
            || passwordPatternLettersAndNumbersAndCharacters.test(newPassword)) && !fieldContainsSpace.test(newPassword))) {
            $('#user-confirmpassword').removeClass('field__input--error').siblings('.field__label').removeClass('field__label--error');
            $('#confirmNewPassword').attr('readonly', false);

        } else {
            $('#user-confirmpassword').addClass('field__input--error').siblings('.field__label').addClass('field__label--error');
            $('#confirmNewPassword').attr('readonly', true);
            $('#new_password_wrong').css('display', 'block');
            $("#password-change-button").attr('disabled', true);
        }

    }

    /**
     * Remove disabled from buttons newPassword, when old password field fill.
     */
    function checkOldPasswordField(){
        var password = $('#user-password').val();
        if (password) {
            $('#user-confirmpassword').attr('readonly', false);
        } else {
            $('#user-confirmpassword').attr('readonly', true);
        }
    }

    /**
     * Remove disabled from button confirmNewPassword, when old password and new password fields fill.
     */
    function checkOldPasswordAndNewPasswordField(){
        var password = $('#user-password').val();
        var newPassword = $('#user-confirmpassword').val();
        if (password && newPassword) {
            $('#confirmNewPassword').attr('readonly', false);
        } else {
            $('#confirmNewPassword').attr('readonly', true);
        }
    }
}
