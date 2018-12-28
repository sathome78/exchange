/* --------- Serializes form data to json object -------------- */
/*todo: refactor this! this js uses only for transfer funds between users. */


$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;

};

const NICKNAME_REGEX = /^\D+[\w\d\-_]+/;


/* --------- make merchants module start -------------- */

$(function () {
    $('.merchantError').hide();
    $('.response_money_operation_btn').hide();

});

Number.prototype.noExponents = function () {
    var data = String(this).split(/[eE]/);
    if (data.length == 1) return data[0];

    var z = '', sign = this < 0 ? '-' : '',
        str = data[0].replace('.', ''),
        mag = Number(data[1]) + 1;

    if (mag < 0) {
        z = sign + '0.';
        while (mag++) z += '0';
        return z + str.replace(/^\-/, '');
    }
    mag -= str.length;
    while (mag--) z += '0';
    return str + z;
};

var notifications;

$(function () {
    notifications = new NotificationsClass();

    /*const YANDEX = 'Yandex.Money';
    const PERFECT = 'Perfect Money';
    const BLOCKCHAIN = 'Blockchain';
    const ADVCASH = 'Advcash Money';
    const EDR_COIN = 'EDR Coin';
    const LIQPAY = 'LiqPay';
    const NIX = 'Nix Money';
    const YANDEX_KASSA = 'Yandex kassa';
    const PRIVAT24 = 'Privat24';
    const INTERKASSA = 'Interkassa';
    const INVOICE = 'Invoice';
    const EDC = 'EDC';
    const OKPAY = 'OkPay';
    const PAYEER = 'Payeer';
    const ETHEREUM = 'Ethereum';
    const LITECOIIN = 'Litecoin';
    const DASH = 'Dash';
    const ETHEREUM_CLASSIC = 'Ethereum Classic';*/

    // const NO_ACTION = 'javascript:void(0);';

    var currency = $('#currency');
    var merchant = $('#merchant');
    var merchantName;
    var merchantMinSum;
    var fractionalAmount;
    var merchantImageId;
    var sum = $('#sum');
    var operationType = $('#operationType');
    var modalTemplate = $('.paymentInfo p');
    var button = $('#payment').find('button');
    button.prop('disabled', true);
    var merchantsData;
    var usernameToTransfer = $('#nickname');
    var $timeoutWarning = $('.timeoutWarning');
    var $minSumNotification = $('#min-sum-notification');




    /*function resetMerchantsList(currency) {
        var optionsHTML = '';
        $.each(merchantsData, function (index) {
            if (merchantsData[index].currencyId == currency) {
                optionsHTML += '<option value="' + merchantsData[index].merchantId + '">' + merchantsData[index].description + '</option>';
                fractionalAmount = merchantsData[index].minSum.noExponents().split('.')[1].length;
            }
        });
        if (optionsHTML === '') {
            document.getElementById('sum').disabled = true;
            merchant.fadeOut();
            button.prop('disabled', true);
        } else {
            merchant.fadeIn();
            //button.prop('disabled', false);
        }
        merchant.empty();
        merchant.html(optionsHTML);
        if (isCorrectSum()) {
            //button.prop('disabled',false);
        } else {
            button.prop('disabled', true);
        }
    }*/

    function resetFormAction(operationType,merchant,form) {
        /*var formAction = {
            yandex:'/merchants/yandexmoney/payment/prepare',
            blockchainDeposit:'/merchants/bitcoin/payment/provide',
            perfectDeposit:'https://perfectmoney.is/api/step1.asp',
            advcash:'/merchants/advcash/payment/prepare',
            liqpay:'/merchants/liqpay/payment/prepare',
            nixmoney:'/merchants/nixmoney/payment/prepare',
            yandex_kassa:'http://din24.net/index.php?route=acc/success/order',
            privat24:'https://api.privatbank.ua/p24api/ishop',
            interkassa:'https://sci.interkassa.com/',
            okpay:'/merchants/okpay/payment/prepare/',
            payeer:'/merchants/payeer/payment/prepare/',
            ethereum:'/merchants/ethereum/payment/prepare/',
            invoice: '/merchants/invoice/preSubmit',
            litecoin: '/merchants/litecoin/payment/prepare/',
            dash: '/merchants/dash/payment/prepare/',
            ethereum:'/merchants/ethereum/ethereum_classic/payment/prepare'

        };*/
        if (operationType === 'INPUT') {
            form.attr('action', "/refill/request/create");
        } /*else if (operationType === 'OUTPUT' && merchant === INVOICE) {
            var finpass = $('#finpassword').val();
            form.attr('action', '/merchants/invoice/withdraw/prepare?finpassword=' + finpass);
        }*/
    }

    function responseControls() {
        $('.request_money_operation_btn').hide();
        $('.response_money_operation_btn').show();
    }

    function requestControls() {
        $('.request_money_operation_btn').show();
        $('.response_money_operation_btn').hide();
    }

    function resetPaymentFormData(targetMerchant, form, callback) {
        if (operationType.val() === 'OUTPUT') {
            if (targetMerchant === INVOICE) {
                callback();
            } else {
                var finpass = $('#finpassword').val();
                $.ajax('/withdraw/request/merchant/create?finpassword=' + finpass, {
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val()
                    },
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify($(form).serializeObject())
                }).success(function (response) {
                    $('#finPassModal .close').click();
                    $('#myModal').modal();
                    responseControls();
                    $('.wallet_input').hide();
                    $(sum).val('0.0');
                    button.prop('disabled', true);
                    $('#outputPaymentProcess')
                        .prop('disabled', false);
                    successNoty();
                    notifications.getNotifications();
                }).fail(function (error, jqXHR, textStatus) {
                    $('#finPassModal .close').click();
                    responseControls();
                    $('#outputPaymentProcess')
                        .prop('disabled', false);
                    $('.wallet_input').hide();
                    errorFromCookie();
                });
            }
        } else {
            switch (targetMerchant) {

                case BLOCKCHAIN :
                    $('#inputPaymentProcess')
                        .html($('#mrcht-waiting').val())
                        .prop('disabled', true);
                    $.ajax('/merchants/bitcoin/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json;charset=utf-8',
                        dataType: 'json',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        $('#inputPaymentProcess')
                            .prop('disabled', false)
                            .html($('#mrcht-ready').val());

                        $.each(response, function (key) {
                            if (key == 'notification') {
                                $('.paymentInfo').html(response[key]);
                            }
                            if (key == 'qr') {
                                $('.paymentQR').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + response[key] + "'>");
                            }
                        });
                        responseControls();
                    }).fail(function (error, jqXHR, textStatus) {
                        responseControls();
                        $('.paymentInfo').html(error.responseJSON.error);
                        console.log(textStatus);
                    });
                    break;
                case LITECOIIN:
                    $('#inputPaymentProcess')
                        .html($('#mrcht-waiting').val())
                        .prop('disabled', true);
                    $.ajax('/merchants/litecoin/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json;charset=utf-8',
                        dataType: 'json',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        $('#inputPaymentProcess')
                            .prop('disabled', false)
                            .html($('#mrcht-ready').val());

                        $.each(response, function (key) {
                            if(key=='notification'){
                                $('.paymentInfo').html(response[key]);
                            }
                            if(key=='qr'){
                                $('.paymentQR').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + response[key] + "'>");
                            }
                        });
                        responseControls();
                    }).fail(function (error, jqXHR, textStatus) {
                        responseControls();
                        $('.paymentInfo').html(error.responseJSON.error);
                        console.log(textStatus);
                    });
                    break;
                case DASH:
                    $('#inputPaymentProcess')
                        .html($('#mrcht-waiting').val())
                        .prop('disabled', true);
                    $.ajax('/merchants/dash/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json;charset=utf-8',
                        dataType: 'json',
                        data: JSON.stringify($(form).serializeObject())
                    }).done(function (response) {
                        $('#inputPaymentProcess')
                            .prop('disabled', false)
                            .html($('#mrcht-ready').val());

                        $.each(response, function (key) {
                            if(key=='notification'){
                                $('.paymentInfo').html(response[key]);
                            }
                            if(key=='qr'){
                                $('.paymentQR').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + response[key] + "'>");
                            }
                        });
                        responseControls();
                    }).fail(function (error, jqXHR, textStatus) {
                        responseControls();
                        $('.paymentInfo').html(error.responseJSON.error);
                        console.log(textStatus);
                    });
                    break;
                case EDC :
                    $('#inputPaymentProcess')
                        .prop('disabled', true)
                        .html($('#mrcht-waiting').val());
                    if ($($timeoutWarning).size() > 0) {
                        $($timeoutWarning).show();
                    }
                    $.ajax('/merchants/edc/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify($(form).serializeObject()),
                        success: function (response) {
                            $('#inputPaymentProcess')
                                .prop('disabled', false)
                                .html($('#mrcht-ready').val());
                            console.log(response);
                            if ($($timeoutWarning).size() > 0) {
                                $($timeoutWarning).hide();
                            }
                            $.each(response, function (key) {
                                if (key == 'notification') {
                                    $('.paymentInfo').html(response[key] + "<p>");
                                }
                                if (key == 'qr') {
                                    $('.paymentQR').html("<img src='https://info.edinarcoin.com/qr-code/" + response[key] + "' width='80' height='80'>");
                                }
                            });

                            responseControls();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.log(jqXHR);
                            console.log(textStatus);
                            console.log(errorThrown);
                            $('.paymentInfo').html(jqXHR.responseJSON.error);

                            responseControls();
                        }
                    });
                    break;
                case EDR_COIN :
                    $('#inputPaymentProcess')
                        .prop('disabled', true)
                        .html($('#mrcht-waiting').val());

                    $.ajax('/merchants/edrcoin/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify($(form).serializeObject()),
                        success: function (response) {
                            $('#inputPaymentProcess')
                                .prop('disabled', false)
                                .html($('#mrcht-ready').val());
                            console.log(response);
                            $('.paymentInfo').html(response);
                            responseControls();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.log(jqXHR);
                            console.log(textStatus);
                            console.log(errorThrown);
                            $('.paymentInfo').html(jqXHR.responseText);
                            responseControls();
                        }
                    });
                    break;
                case ETHEREUM :
                    $('#inputPaymentProcess')
                        .prop('disabled', true)
                        .html($('#mrcht-waiting').val());
                    if ($($timeoutWarning).size() > 0) {
                        $($timeoutWarning).show();
                    }
                    $.ajax('/merchants/ethereum/ethereum/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify($(form).serializeObject()),
                        success:function (response) {
                            $('#inputPaymentProcess')
                                .prop('disabled', false)
                                .html($('#mrcht-ready').val());
                            console.log(response);
                            if ($($timeoutWarning).size() > 0) {
                                $($timeoutWarning).hide();
                            }
                            $.each(response, function (key) {
                                if(key=='notification'){
                                    $('.paymentInfo').html(response[key] + "<p>");
                                }
                                if(key=='qr'){
                                    $('.paymentQR').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + response[key] + "'>");
                                }
                            });

                            responseControls();
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            console.log(jqXHR);
                            console.log(textStatus);
                            console.log(errorThrown);
                            $('.paymentInfo').html(jqXHR.responseJSON.error);

                            responseControls();
                        }
                    });
                    break;
                case ETHEREUM_CLASSIC :
                    $('#inputPaymentProcess')
                        .prop('disabled', true)
                        .html($('#mrcht-waiting').val());
                    if ($($timeoutWarning).size() > 0) {
                        $($timeoutWarning).show();
                    }
                    $.ajax('/merchants/ethereum/ethereum_classic/payment/prepare', {
                        headers: {
                            'X-CSRF-Token': $("input[name='_csrf']").val()
                        },
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify($(form).serializeObject()),
                        success:function (response) {
                            $('#inputPaymentProcess')
                                .prop('disabled', false)
                                .html($('#mrcht-ready').val());
                            console.log(response);
                            if ($($timeoutWarning).size() > 0) {
                                $($timeoutWarning).hide();
                            }
                            $.each(response, function (key) {
                                if(key=='notification'){
                                    $('.paymentInfo').html(response[key] + "<p>");
                                }
                                if(key=='qr'){
                                    $('.paymentQR').html("<img src='https://chart.googleapis.com/chart?chs=100x100&chld=L|2&cht=qr&chl=" + response[key] + "'>");
                                }
                            });

                            responseControls();
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            console.log(jqXHR);
                            console.log(textStatus);
                            console.log(errorThrown);
                            $('.paymentInfo').html(jqXHR.responseJSON.error);

                            responseControls();
                        }
                    });
                    break;
                default:
                    callback();
            }
        }
    }

    function fillModalWindow(type,amount,currency) {
        $.ajax({
            url: '/merchants/commission',
            type: "get",
            contentType: "application/json",
            data : {"type":type, "amount":amount, "currency":currency, "merchant":merchantName}
        }).done(function (response) {
            var templateVariables = {
                amount: '__amount',
                currency: '__currency',
                merchant: '__merchant',
                percent: '__percent'
            };
            var newHTMLElements = [];
            modalTemplate.slice().each(function(index,val){
                newHTMLElements[index] = '<p>'+$(val).html()+'</p>';
            });
            newHTMLElements[0] = newHTMLElements[0]
                .replace(templateVariables.amount, "<span class='modal-amount'>"+amount+"</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>"+getCurrentCurrency()+"</span>")
                .replace(templateVariables.merchant, "<span class='modal-merchant'>"+merchantName+"</span>");
            newHTMLElements[1] = newHTMLElements[1]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + response['commissionAmount'] + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + getCurrentCurrency() + "</span>")
                .replace(templateVariables.percent, "<span class='modal-amount'>"+response['commission'] + "</span>");
            newHTMLElements[2] = newHTMLElements[2]
                .replace(templateVariables.amount, "<span class='modal-amount'>" + response['amount'] + "</span>")
                .replace(templateVariables.currency, "<span class='modal-amount'>" + getCurrentCurrency() + "</span>");
            var newHTML = '';
            $.each(newHTMLElements, function (index) {
                newHTML += newHTMLElements[index];
            });
            $('.paymentInfo').html(newHTML);
            $('.merchantError').hide();

        }).fail(function () {
            $('.paymentInfo').hide();
            $('.wallet_input').hide();
            $('.merchantError').show();
        });
    }


    sum.on('input', function () {
        if(isCorrectSum()) {
            button.prop('disabled', false);
            $minSumNotification.hide();
        } else {
            button.prop('disabled', true);
            $minSumNotification.show();
        }
    });

    function isCorrectSum() {
        var merchantMinSum = $('#minAmount').text();
        var merchantMaxSum = $('#maxForTransfer').text();
        console.log( "ms" + merchantMinSum);
        var targetSum = parseFloat(sum.val());
        console.log()
        return targetSum >= merchantMinSum && targetSum <= merchantMaxSum;

    }

    /*currency.on('change', function () {
        resetMerchantsList(this.value);
    });*/

    function submitProcess() {
        var targetMerchant = merchantName;
        var paymentForm = $('#payment');
        if (paymentForm.get(0).merchant == null) {
            paymentForm.append('<input type="hidden" name="merchant" value="' + merchant + '">');
        } else {
            $(paymentForm).find('input[name="merchant"]').val(merchant);
        }
        if (paymentForm.get(0).merchantImage == null) {
            paymentForm.append('<input type="hidden" name="merchantImage" value="' + merchantImageId + '">');
        } else {
            $(paymentForm).find('input[name="merchantImage"]').val(merchantImageId);
        }
        resetFormAction(operationType.val(), targetMerchant, paymentForm);
        resetPaymentFormData(targetMerchant, paymentForm, function () {
            paymentForm.submit();
        });
    }

    function getCurrentCurrency() {
        return $("#currencyName").val();
    }

    /*$('button[name=assertInputPay]').click(function (e) {
        e.preventDefault();
        var arr = this.value.split(':');
        merchant = arr[0];
        merchantName = arr[1];
        merchantMinSum = parseFloat(arr[2]);
        merchantImageId = parseFloat(arr[3]);

        $('.paymentInfo').html("");
        $('.paymentQR').html("");
        requestControls();
        $('#inputPaymentProcess')
            .html($('#mrcht-waiting').val())
            .prop('disabled', false);

        $('#inputPaymentProcess')
            .prop('disabled', false)
            .html($('#mrcht-ready').val());

        if (isCorrectSum()) {
            if (merchantName === INVOICE) {
                submitProcess();
            } else {
                fillModalWindow('INPUT', sum.val(), getCurrentCurrency());
                if ($($timeoutWarning).size() > 0) {
                    $($timeoutWarning).hide();
                }
                $('#myModal').modal();
            }
        }
    });*/

    /*$('button[name=assertOutputPay]').click(function () {
        var arr = this.value.split(':');
        merchant = arr[0];
        merchantName = arr[1];
        merchantMinSum = parseFloat(arr[2]);
        merchantImageId = parseFloat(arr[3]);

        if (merchantName != INVOICE) {
            $('.wallet_input').show();
            setTimeout("$('.wallet_input>input').focus().val('')", 200);
        } else {
            $('.wallet_input').hide();

        }
        requestControls();
        fillModalWindow('OUTPUT', sum.val(), getCurrentCurrency());
    });*/



    /*$("#outputPaymentProcess").on('click', function () {
        if (merchantName === INVOICE) {
            getFinPassModal();
            $('#myModal .close').click();
            $('#outputPaymentProcess')
                .prop('disabled', true);
        } else {
            var uid = $("input[name='walletUid']").val();
            if (uid.length > 3) {
                $('#destination').val(uid);
                $('#myModal .close').click();
                getFinPassModal();
            }
        }
    });*/

    /*function performWithdraw() {
        submitProcess();
        $('#outputPaymentProcess')
            .prop('disabled', true);
    }*/




    /*$('#submitTransferModalButton').click(function (e) {
        console.log('merchant ' + merchant);
        e.preventDefault();
        $('#submitTransferModalButton').prop('disabled', true);
        if (operationType.val() === 'OUTPUT') {

            performWithdraw()
        }
        else {
            submitTransfer()
        }

    });*/

    $('#inputPaymentProcess').on('click', function () {
        submitProcess();
    });

    $('#transferButton').click(function () {
        prepareTransfer()
    });

    function prepareTransfer() {
        $('#transferProcess').prop('disabled', true);
        merchantName = 'transfer';
        fillModalWindow('USER_TRANSFER', sum.val(), getCurrentCurrency());
        $('#nicknameInput').val('');
        $('#nickname').val('');
        validateNickname();
        $('.nickname_input').show();
        requestControls();
        $('#transferModal').modal({
            backdrop: 'static'
        });
    }

    $('#nicknameInput').on('keyup', validateNickname);

    $('#transferProcess').click(function (e) {
        e.preventDefault();
        var nickname = $('#nicknameInput').val();
        $('#nickname').val(nickname);
        $('#transferProcess').prop('disabled', true);
        checkTransfer();
    });

    function getFinPassModal() {
        $('#check-fin-password-button').prop('disabled', false);
        $('#finPassModal').modal({
            backdrop: 'static'
        });
    }

    $('#check-fin-password-button').click(function (e) {
        console.log('merchant ' + merchant);
        e.preventDefault();
        $('#check-fin-password-button').prop('disabled', true);
        submitTransfer();
    });


    function submitTransfer() {
        var finpass = $('#finpassword').val();
        var transferForm = $('#payment').serialize() + '&finpassword=' + finpass;
        $.ajax('/transfer/submit', {
            type: 'POST',
            data: transferForm,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            success: function (response) {
                $('#finPassModal .close').click();
                $('#transferModal').modal();
                $('.paymentInfo').html(response.result);
                $('.nickname_input').hide();
                responseControls();
                setTimeout(function () {
                    location.reload();
                }, 5000);
            },
            error: function (err) {
                $('#finPassModal .close').click();
                var errorType = $.parseJSON(err.responseText).cause;
                var errorMsg = $.parseJSON(err.responseText).detail;
                switch (errorType) {
                    case 'AbsentFinPasswordException': {
                        window.location.href = '/settings?tabIdx=2&msg=' + errorMsg;
                        break;
                    }
                    default: {
                        responseControls()
                    }
                }
            }
        });
    }


    function checkTransfer() {
        var transferForm = $('#payment').serialize() + '&checkOnly=true';
        $.ajax('/transfer/submit', {
            type: 'POST',
            data: transferForm,
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            success: function (response) {
                $('#transferModal .close').click();
                getFinPassModal();
                responseControls();
            },
            error: function (err) {
                console.log(err);
                var errorText = JSON.parse(err.responseText);
                $('.paymentInfo').html(errorText.detail);
                $('.nickname_input').hide();
                responseControls()
            }
        })

    }

    function validateNickname() {
        var value = $('#nicknameInput').val();
        if (NICKNAME_REGEX.test(value)) {
            $('#transferProcess').prop('disabled', false);
        } else {
            $('#transferProcess').prop('disabled', true);
        }
    }

});



function parseNumber(numberStr) {
    /*ATTENTION: this func wil by work correctly if number always has decimal separator
     * for this reason we use BigDecimalProcessing.formatLocale(rs.getBigDecimal("some value"), locale, 2)
     * which makes 1000.00 from 1000 or 1000.00000
     * or we can use igDecimalProcessing.formatLocale(rs.getBigDecimal("some value"), locale, true)*/
    if (numberStr.search(/\,.*\..*/) != -1) {
        /*100,000.12 -> 100000.12*/
        numberStr = numberStr.replace(/\,/g, '');
    } else if (numberStr.search(/\..*\,.*/) != -1) {
        /*100.000,12 -> 100000.12*/
        numberStr = numberStr.replace(/\./g, '').replace(/\,/g, '.');
    } else if (numberStr.search(/\s.*\..*/) != -1) {
        /*100 000.12 -> 100000.12*/
        numberStr = numberStr.replace(/\s/g, '');
    } else if (numberStr.search(/\s.*\,.*/) != -1) {
        /*100 000,12 -> 100000.12*/
        numberStr = numberStr.replace(/\s/g, '').replace(/\,/g, '.');
    }
    numberStr = numberStr.replace(/\s/g, '').replace(/\,/g, '.');
    return parseFloat(numberStr);
}

function sendGtag() {
    gtag('event', 'password-correct', {'event_category': 'password-confirm', 'event_label': 'finish-registration'});
}