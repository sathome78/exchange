$(function () {

    var withdrawButtons = $('#merchantList').find(".start-button");
    var minAmount = 0;
    var checkAmount = true;
    determineMinAmount();
    var $input = $('#sum');

    $('#allSum').on('click', function () {
        const maxAmount = parseFloat($input.data("max-amount"));
        $input.val(maxAmount);
        checkButtons(maxAmount)
    });


    function determineMinAmount() {
        withdrawButtons.each(function(index,item){
            var $item = $(item);
            var isAmounInputNeed = $item.data("is-amount-input-needed") == undefined ? true : $item.data("is-amount-input-needed");
            checkAmount = isAmounInputNeed;
            if (isAmounInputNeed) {
                $item.prop('disabled', true);
            }
            if(minAmount < $item.data("min-sum")) {
                minAmount = $item.data("min-sum");
            }
        });
        var systemMinAmount = $('.numericInputField').data("system-min-sum");
        minAmount = Math.max(systemMinAmount, minAmount);
        $('#minSum').text(minAmount);
        $('.numericInputField').attr("data-min-amount", minAmount);
    }

    function checkButtons(amount) {
        withdrawButtons.each(function(index,item){
            var $item = $(item);
            if(amount >= $item.data("min-sum")) {
                $item.prop('disabled', false);
            } else {
                $item.prop('disabled', true);
            }
        });
    }


    $(".input-block-wrapper__input").prop("autocomplete", "off");
    $(".numericInputField").prop("autocomplete", "off");
    $(".numericInputField")
        .keypress(
            function (e) {
                const fractionalAmount = $(this).data("scale-of-amount");
                var decimal = $(this).val().split('.')[1];
                if (decimal && decimal.length >= fractionalAmount + 1) {
                    return false;
                }
                if (e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 0) {
                    if (e.key == '.' && $(this).val().indexOf('.') >= 0) {
                        return false;
                    }
                    var str = $(this).val() + e.key;
                    if (str.length > 1 && str.indexOf('0') == 0 && str.indexOf('.') != 1) {
                        $(this).val("");
                        return false
                    }
                } else {
                    return false;
                }
                return true;
            }
        )
        .on('input', function (e) {
            const minSumNotyId = $(this).data("min-sum-noty-id");
            const buttonId = $(this).data("submit-button-id");
            /*const currency = $(this).data("currency-name").trim();*/
            const maxAmount = parseFloat($(this).data("max-amount"));
            const minAmount = parseFloat($(this).data("min-amount"));
            const balance = parseFloat($(this).data("balance"));
            const fractionalAmount = $(this).data("scale-of-amount");
            var val = $(this).val();
            var regx = /^(^[1-9]+\d*((\.{1}\d*)|(\d*)))|(^0{1}\.{1}\d*)|(^0{1})$/;
            var result = val.match(regx);
            /*var maxSum = (currency === 'IDR') ? 999999999999.99 : 999999.99;*/
            var maxSum = 999999.99;
            if (!result || result[0] != val) {
                $(this).val('');
            }
            if (val >= maxSum) {
                $(this).val(maxSum);
            }
            var minLimit = 0;

            if (maxAmount && val >= maxAmount) {
                $(this).val(maxAmount);
                val = maxAmount;
            }

            minLimit = minAmount;

            if (val >= minLimit) {
                $(minSumNotyId).hide();
            } else {
                $(minSumNotyId).show();
            }

            var decimal = $(this).val().split('.')[1];
            if (decimal && decimal.length > fractionalAmount) {
                $(this).val($(this).val().slice(0, -1));

            }
            if (checkAmount) {
                checkButtons(val);
            }
           /* if (val > 0 && val >= minLimit) {
                $(buttonId).prop('disabled', false);
            } else {
                $(buttonId).prop('disabled', true);
            }
*/
        });
});

const errorClass = "fail-enter";

function validateString($elem, regex, errorDiv, allowAbsent, addErrorClass) {
    $elem.removeClass(errorClass);
    if (errorDiv) {
        $(errorDiv).hide();
    }
    var str = $elem.val();
    if (!str && allowAbsent) {
        return true;
    }
    if (regex.test(str)) {
        return true;
    } else {
        if (errorDiv) {
            $(errorDiv).show();
        }
        if (addErrorClass) {
            $elem.addClass(errorClass);
        }
        return false;
    }
}