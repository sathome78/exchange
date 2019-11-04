/**
 * Created by Valk on 02.06.2016.
 */

function TradingClass(currentCurrencyPair, orderRoleFilterEnabled, chartSubscribeCallback, cpData) {
    if (TradingClass.__instance) {
        return TradingClass.__instance;
    } else if (this === window) {
        return new TradingClass();
    }
    TradingClass.__instance = this;
    /**/
    var that = this;
    var orderRoleFilter = null;
    var currentPair = currentCurrencyPair;

    var dashboardCurrencyPairSelector;
    var $graphicsLoadingImg = $('#graphics-container').find('.loading');
    var $totalForBuyInput = $('#totalForBuy');
    var $exchangeRateBuyInput = $('#exchangeRateBuy');
    var $amountBuyInput = $('#amountBuy');
    var $amountStopInput = $('#amount-stop');
    var $limitRateInput = $('#limit-stop');
    var $totalStopInput = $('#totalForStop');

    var $totalForSellInput = $('#totalForSell');
    var $exchangeRateSellInput = $('#exchangeRateSell');
    var $amountSellInput = $('#amountSell');
    /**/
    var showLog = false;
    /**/
    this.ordersListForAccept = [];
    /**/
    this.commissionSell;
    this.commissionBuy;
    /**/
    this.ROUND_SCALE = 9;
    this.numeralFormat = '0.[' + '0'.repeat(this.ROUND_SCALE) + ']';

    function onCurrencyPairChange() {
        $graphicsLoadingImg.removeClass('hidden');
        that.fillOrderCreationFormFields();
        currentPair = $('.currency-pair-selector__menu-item.active').prop('id');
    }

    this.syncCurrencyPairSelector = function (cpName) {
        dashboardCurrencyPairSelector.syncState('MAIN', onCurrencyPairChange);
        currentPair = cpName;
    };

    this.updateAndShowBuyOrders = function (orders, refreshIfNeeded) {
        if (refreshIfNeeded) {
            var $ordersBuyTable = $('#dashboard-orders-buy-table').find('tbody');
            var $tmpl = $('#dashboard-orders-buy-table_row').html().replace(/@/g, '%');
            clearTable($ordersBuyTable);
            orders.forEach(function (e) {
                $ordersBuyTable.append(tmpl($tmpl, e));
            });
            blink($('#dashboard-orders-buy-table'));
        }
    };

    this.updateAndShowSellOrders = function (orders, refreshIfNeeded) {
        if (refreshIfNeeded) {
            var $ordersSellTable = $('#dashboard-orders-sell-table').find('tbody');
            var $tmpl = $('#dashboard-orders-sell-table_row').html().replace(/@/g, '%');
            clearTable($ordersSellTable );
            orders.forEach(function (e) {
                $ordersSellTable.append(tmpl($tmpl, e));
            });
            blink($('#dashboard-orders-sell-table'));
        }
    };

    this.updateAndShowAllTrades = function (data) {
        var $ordersHistoryTable = $('#orders-history-table').find('tbody');
        var $tmpl = $('#orders-history-table_row').html().replace(/@/g, '%');
        clearTable($ordersHistoryTable);
        data.forEach(function (e) {
            $ordersHistoryTable.append(tmpl($tmpl, e));
        });
        blink($ordersHistoryTable);
    };

    this.updateAndShowMyTrades = function (data) {
        var $ordersHistoryTable = $('#orders-history-table__my-deals').find('tbody');
        var $tmpl = $('#orders-history-table_row').html().replace(/@/g, '%');
        clearTable($ordersHistoryTable);
        data.forEach(function (e) {
            $ordersHistoryTable.append(tmpl($tmpl, e));
        });
        blink($ordersHistoryTable);
    };

    this.fillOrderCreationFormFields = function () {
        $(document).one("ajaxStop", function () {
            var currencyPairName = $('.currency-pair-selector__menu-item.active').prop('id');
            var initialAmount = 1;
            var initialAmountString = numbro(initialAmount).format(that.numeralFormat);
            $('#amountBuy').val(initialAmountString);
            var lastBuyExrate = getLastExrate('#dashboard-orders-buy-table .dashboard-order__tr:first', currencyPairName);
            $('#exchangeRateBuy').val(lastBuyExrate);
            calculateFieldsForBuy();
            $('#amountSell').val(initialAmountString);
            var lastSellExrate = getLastExrate('#dashboard-orders-sell-table .dashboard-order__tr:first', currencyPairName);
            $('#exchangeRateSell').val(lastSellExrate);
            calculateFieldsForSell();
            $('#limit-stop').val(lastSellExrate);
            $('#amount-stop').val(initialAmountString);
            calculateFieldsForStop();
            that.fillOrderBalance(currencyPairName);
        });
    };

    this.fillOrderBalance = function (currencyPairName) {
        if ($('#currentBaseBalance').length > 0 && $('#currentConvertBalance').length > 0
            && currencyPairName != undefined) {
            var currencies = currencyPairName.split('\/');
            var currentBaseBalance = getCurrentBalanceByCurrency(currencies[0]);
            var currentConvertBalance = getCurrentBalanceByCurrency(currencies[1]);
            $('#currentBaseBalance').text(currentBaseBalance);
            $('#currentConvertBalance').text(currentConvertBalance);
            $('.currentConvertBalance').text(currentConvertBalance);
        }
    };

    function getCurrentBalanceByCurrency(currencyName) {
        return $('.balance_'+currencyName).text();
    }

    function getLastExrate($selector, currencyPairName) {
        var lastRate;
        if ($($selector).size() === 0) {
            var $cell = $('#currency_table').find("tr td:contains('" + currencyPairName + "')");
            if ($cell.size() === 0) {
                return '';
            } else {
                lastRate = $cell.next().text()
            }
            return numbro(parseNumber(lastRate)).format(that.numeralFormat);

        } else {
            lastRate = $($selector).find('.order_exrate').text();
            return numbro(parseNumber(lastRate)).format(that.numeralFormat);
        }
    }

    this.resetOrdersListForAccept = function () {
        if (that.ordersListForAccept.length != 0) {
            that.ordersListForAccept = [];
            switchCreateOrAcceptButtons();
        }
    };

    this.clearOrdersCreationForm = function () {
        $('.item__input').val('');
        $('.buyBTC__input').val('');
        calculateFieldsForBuy();
        calculateFieldsForSell();
        switchCreateOrAcceptButtons();
    };

    function getOrderCommissions() {
        var url = '/dashboard/orderCommissions';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                that.commissionSell = data.sellCommission;
                that.commissionBuy = data.buyCommission;
                calculateFieldsForBuy();
                calculateFieldsForSell();
            }
        });
    }

    function calculateFieldsForStop() {
        var amount = +$($amountStopInput).val();
        var exchangeRate = +$($limitRateInput).val();
        $($totalStopInput).val(numbro(amount * exchangeRate).format(that.numeralFormat));
    }

    function calculateFieldsForStopBackward() {
        var totalForBuy = +$($totalStopInput).val();
        var exchangeRate = +$($limitRateInput).val();
        if (!totalForBuy) {
            $($amountStopInput).val(0);
        } else if (!exchangeRate) {
            $($amountStopInput).val(0);
            $($limitRateInput).val(0);
        } else {
            $($amountBuyInput).val(numbro(totalForBuy / exchangeRate).format(that.numeralFormat));
        }
    }

    function calculateFieldsForBuy() {
        var amount = +$($amountBuyInput).val();
        var exchangeRate = +$($exchangeRateBuyInput).val();
        $($totalForBuyInput).val(numbro(amount * exchangeRate).format(that.numeralFormat));
        var totalForBuy = +$($totalForBuyInput).val();
        fillCommissionFieldsBuy(totalForBuy);
    }
    function calculateFieldsForBuyBackward() {
        var totalForBuy = +$($totalForBuyInput).val();
        var exchangeRate = +$($exchangeRateBuyInput).val();
        if (!totalForBuy) {
            $($amountBuyInput).val(0);
        } else if (!exchangeRate) {
            $($amountBuyInput).val(0);
            $($exchangeRateBuyInput).val(0);
        } else {
            $($amountBuyInput).val(numbro(totalForBuy / exchangeRate).format(that.numeralFormat));
        }
        fillCommissionFieldsBuy(totalForBuy);
    }

    function calculateFieldsForSell() {
        var amount = +$($amountSellInput).val();
        var exchangeRate = +$($exchangeRateSellInput).val();
        $($totalForSellInput).val(numbro(amount * exchangeRate).format(that.numeralFormat));
        var totalForSell = +$($totalForSellInput).val();
        fillCommissionFieldsSell(totalForSell);
    }

    function calculateFieldsForSellBackward() {
        var totalForSell = +$($totalForSellInput).val();
        var exchangeRate = +$($exchangeRateSellInput).val();
        if (!totalForSell) {
            $($amountSellInput).val(0);
        } else if (!exchangeRate) {
            $($amountSellInput).val(0);
            $($exchangeRateSellInput).val(0);
        } else {
            $($amountSellInput).val(numbro(totalForSell / exchangeRate).format(that.numeralFormat));
        }
        fillCommissionFieldsSell(totalForSell);
    }


    function fillCommissionFieldsBuy(totalForBuy) {
        var commission = that.commissionBuy;
        var calculatedCommissionForBuy = +(totalForBuy * commission / 100).toFixed(that.ROUND_SCALE);
        var totalWithCommissionForBuy = +(totalForBuy + calculatedCommissionForBuy).toFixed(that.ROUND_SCALE);
        $('#calculatedCommissionForBuy').find('span:first').text(calculatedCommissionForBuy.toFixed(that.ROUND_SCALE));
        $('#totalWithCommissionForBuy').find('span:first').text(totalWithCommissionForBuy.toFixed(that.ROUND_SCALE));
    }
    function fillCommissionFieldsSell(totalForSell) {
        var commission = that.commissionSell;
        var calculatedCommissionForSell = +(totalForSell * commission / 100).toFixed(that.ROUND_SCALE);
        var totalWithCommissionForSell = +(totalForSell - calculatedCommissionForSell).toFixed(that.ROUND_SCALE);
        $('#calculatedCommissionForSell').find('span:first').text(calculatedCommissionForSell.toFixed(that.ROUND_SCALE));
        $('#totalWithCommissionForSell').find('span:first').text(totalWithCommissionForSell.toFixed(that.ROUND_SCALE));
    }



    /*=========================================================*/
    (function init(currentCurrencyPair, orderRoleFilterEnabled, chartSubscribeCallback, cpData) {
        getOrderCommissions();
        dashboardCurrencyPairSelector = new CurrencyPairSelectorClass('dashboard-currency-pair-selector', currentCurrencyPair, cpData);
        chart = new ChartAmchartsClass2(currentCurrencyPair, chartSubscribeCallback);
        dashboardCurrencyPairSelector.init(onCurrencyPairChange, 'MAIN');
        try {
            orderRoleFilter = new OrderRoleFilterClass(orderRoleFilterEnabled, onCurrencyPairChange);
        } catch (e) {
        }

        that.fillOrderCreationFormFields();

        /**/
        $('#amountBuy').on('keyup', calculateFieldsForBuy).on('keydown', that.resetOrdersListForAccept);
        $('#exchangeRateBuy').on('keyup', calculateFieldsForBuy).on('keydown', that.resetOrdersListForAccept);
        $('#amountSell').on('keyup', calculateFieldsForSell).on('keydown', that.resetOrdersListForAccept);
        $('#exchangeRateSell').on('keyup', calculateFieldsForSell).on('keydown', that.resetOrdersListForAccept);
        $('#totalForBuy').on('keyup', calculateFieldsForBuyBackward).on('keydown', that.resetOrdersListForAccept);
        $('#totalForSell').on('keyup', calculateFieldsForSellBackward).on('keydown', that.resetOrdersListForAccept);
        $('#limit-stop').on('keyup', calculateFieldsForStop);
        $('#amount-stop').on('keyup', calculateFieldsForStop);
        $('#totalForStop').on('keyup', calculateFieldsForStopBackward);
        /**/
        $('.dashboard-order__table').on('click', '.dashboard-order__tr', fillOrdersFormFromCurrentOrder);
        /**/
        $('#dashboard-buy').on('click', orderBuy);
        $('#dashboard-sell').on('click', orderSell);
        $('#dashboard-stop-buy').on('click', stopOrder);
        $('#dashboard-stop-sell').on('click', stopOrder);
        /**/
        $('#dashboard-buy-accept').on('click', orderBuyAccept);
        $('#dashboard-sell-accept').on('click', orderSellAccept);
        /**/
        $('#order-create-confirm__submit').on('click', orderCreate);
        /**/
        $('.dashboard-accept-reset__button').on('click', resetOrdersListForAcceptOnClick);
        /**/
        $('.deals-scope-switcher__wrapper').on('click', '.deals-scope-switcher__button', function (e) {
            $(this).siblings().removeClass('ht-active');
            $(this).addClass('ht-active');
            var $tableId = $('#' + $(this).data('tableid'));
            $('.orders-history-table').addClass('hidden');
            $tableId.removeClass('hidden');
            /*that.getAndShowAcceptedOrdersHistory(); move on sockets*/
            that.getAndShowAcceptedOrdersHistory_myDeals();
        });
        /**/
        switchCreateOrAcceptButtons();
    })(currentCurrencyPair, orderRoleFilterEnabled, chartSubscribeCallback, cpData);

    function fillOrdersFormFromCurrentOrder() {
        that.ordersListForAccept = [];
        /**/
        var orderAmountSumm = 0;
        $(this).prevAll('.dashboard-order__tr').each(function (i, e) {
            var orderId = $(e).find('.order_id').text();
            var orderType = $(e).find('.order_type').text();
            var orderAmount = $(e).find('.order_amount').attr('title');
            var orderExRate = $(e).find('.order_exrate').attr('title');
            var data = {
                orderId: orderId,
                orderType: orderType,
                orderAmount: orderAmount,
                orderExRate: orderExRate
            };
            that.ordersListForAccept.unshift(data);
            orderAmountSumm += parseNumber(orderAmount);
            orderAmountSumm = (+orderAmountSumm.toFixed(that.ROUND_SCALE));
        });
        var orderId = $(this).find('.order_id').text();
        var orderType = $(this).find('.order_type').text();
        var orderAmount = $(this).find('.order_amount').attr('title');
        var orderExRate = parseNumber($(this).find('.order_exrate').attr('title'));
        var data = {
            orderId: orderId,
            orderType: orderType,
            orderAmount: orderAmount,
            orderExRate: orderExRate
        };
        that.ordersListForAccept.push(data);
        orderAmountSumm += parseNumber(orderAmount);
        /**/
        $('#amountBuy').val(numbro(orderAmountSumm).format(that.numeralFormat));
        $('#exchangeRateBuy').val(numbro(orderExRate).format(that.numeralFormat));
        $('#amountSell').val(numbro(orderAmountSumm).format(that.numeralFormat));
        $('#exchangeRateSell').val(numbro(orderExRate).format(that.numeralFormat));
        /**/
        calculateFieldsForSell();
        calculateFieldsForBuy();
        switchCreateOrAcceptButtons(orderType, that.ordersListForAccept.length);
    }

    function switchCreateOrAcceptButtons(acceptedOrderType, ordersForAcceptionCount) {
        var s;
        s = $('#dashboard-sell-accept').text();
        s = s.split('(')[0].trim() + ' (' + ordersForAcceptionCount + ')';
        $('#dashboard-sell-accept').text(s);
        s = $('#dashboard-buy-accept').text();
        s = s.split('(')[0].trim() + ' (' + ordersForAcceptionCount + ')';
        $('#dashboard-buy-accept').text(s);
        if (!acceptedOrderType) {
            $('#dashboard-sell-accept').addClass('hidden');
            $('#dashboard-sell-accept-reset').addClass('hidden');
            $('#dashboard-buy-accept').addClass('hidden');
            $('#dashboard-buy-accept-reset').addClass('hidden');
            $('#dashboard-sell').removeClass('hidden');
            $('#dashboard-buy').removeClass('hidden');
        }
        if (acceptedOrderType == 'BUY') {
            $('#dashboard-sell-accept').removeClass('hidden');
            $('#dashboard-sell-accept-reset').removeClass('hidden');
            $('#dashboard-buy-accept').addClass('hidden');
            $('#dashboard-buy-accept-reset').addClass('hidden');
            $('#dashboard-sell').addClass('hidden');
            $('#dashboard-buy').removeClass('hidden');
        }
        if (acceptedOrderType == 'SELL') {
            $('#dashboard-sell-accept').addClass('hidden');
            $('#dashboard-sell-accept-reset').addClass('hidden');
            $('#dashboard-buy-accept').removeClass('hidden');
            $('#dashboard-buy-accept-reset').removeClass('hidden');
            $('#dashboard-sell').removeClass('hidden');
            $('#dashboard-buy').addClass('hidden');
        }
    }

    function resetOrdersListForAcceptOnClick(e) {
        e.preventDefault();
        that.resetOrdersListForAccept();
    }

    /*PREPARE DATA FOR MODAL DIALOG FOR CREATION ORDER ... */
    function orderBuy(event) {
        event.preventDefault();
        var data = {operationType: 'BUY'};
        $.map($('#dashboard-buy-form').serializeArray(), function (e) {
            if (e.name == 'amount') {
                data.amount = e.value;
            }
            if (e.name == 'exchangeRate') {
                data.rate = e.value;
            }
        });
        showOrderCreateDialog(data);
    }

    function orderSell(event) {
        event.preventDefault();
        var data = {operationType: 'SELL'};
        $.map($('#dashboard-sell-form').serializeArray(), function (e) {
            if (e.name == 'amount') {
                data.amount = e.value;
            }
            if (e.name == 'exchangeRate') {
                data.rate = e.value;
            }
        });
        showOrderCreateDialog(data);
    }

    function stopOrder(event) {
        event.preventDefault();
        var data = {operationType: $(this).data('action')};
        $.map($('#dashboard-stop-order-form').serializeArray(), function (e) {
            if (e.name == 'amount') {
                data.amount = e.value;
            }
            if (e.name == 'exchangeRate') {
                data.rate = e.value;
            }
            if (e.name == 'stop') {
                data.stop = e.value;
            }
        });
        data.baseType = 'STOP_LIMIT';
        showOrderCreateDialog(data);
    }

    /*...PREPARE DATA FOR MODAL DIALOG FOR CREATION ORDER */

    /*MODAL DIALOG FOR CREATION ORDER ... */
    function showOrderCreateDialog(data) {
        data.currencyPair = currentPair;
        /**/
        $('.stop-rate').hide();
        var $balanceErrorContainer = $('#order-create-confirm__modal').find('[for=balance]');
        $balanceErrorContainer.empty();
        var $amountErrorContainer = $('#order-create-confirm__modal').find('[for=amount]');
        $amountErrorContainer.empty();
        var $exrateErrorContainer = $('#order-create-confirm__modal').find('[for=exrate]');
        $exrateErrorContainer.empty();
        var $stopErrorContainer = $('#order-create-confirm__modal').find('[for=stop]');
        $stopErrorContainer.empty();
        $('#order-create-confirm__submit').removeClass('hidden');
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/submitnew/' + data.operationType,
            data: data,
            type: 'POST',
            success: function (data) {
                $('#order-create-confirm__modal').find('#operationTypeName').val(data.operationTypeName + ' ' + data.baseType);
                $('#order-create-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                $('#order-create-confirm__modal').find('#balance').val(data.balance);
                $('#order-create-confirm__modal').find('#amount').val(data.amount);
                if (data.baseType == 'STOP_LIMIT') {
                    $('#order-create-confirm__modal').find('#stop').val(data.stop);
                    $('.stop-rate').show();
                }
                $('#order-create-confirm__modal').find('#exrate').val(data.exrate);
                $('#order-create-confirm__modal').find('#total').val(data.total);
                $('#order-create-confirm__modal').find('#commission').val(data.commission);
                $('#order-create-confirm__modal').find('#totalWithComission').val(data.totalWithComission);
                /**/
                $('#order-create-confirm__modal').modal();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                var responseData = jqXHR.responseJSON;
                for (var f in responseData) {
                    if (f.split('_')[0] == 'balance') {
                        $balanceErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                    if (f.split('_')[0] == 'amount') {
                        $amountErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                    if (f.split('_')[0] == 'exrate') {
                        $exrateErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                    if (f.split('_')[0] == 'stop') {
                        $stopErrorContainer.append('<div class="input-block-wrapper__error">' + responseData[f] + '</div>');
                    }
                }
                var data = responseData.order;
                if (data) {
                    $('#order-create-confirm__modal').find('#operationTypeName').val(data.operationTypeName + ' ' + data.baseType);
                    $('#order-create-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                    $('#order-create-confirm__modal').find('#balance').val(data.balance);
                    $('#order-create-confirm__modal').find('#amount').val(data.amount);
                    if (data.baseType == 'STOP_LIMIT') {
                        $('#order-create-confirm__modal').find('#stop').val(data.stop);
                        $('.stop-rate').show();
                    }
                    $('#order-create-confirm__modal').find('#exrate').val(data.exrate);
                    $('#order-create-confirm__modal').find('#total').val(data.total);
                    $('#order-create-confirm__modal').find('#commission').val(data.commission);
                    $('#order-create-confirm__modal').find('#totalWithComission').val(data.totalWithComission);
                    /**/
                    $('#order-create-confirm__submit').addClass('hidden');
                    $('#order-create-confirm__modal').modal();
                }
            }
        });
    }

    /*... MODAL DIALOG FOR CREATION ORDER*/

    /*CALL CREATION THE SUBMITTED ORDER AND CONTROL RESULT ... */
    function orderCreate(event) {
        event.preventDefault();
        $('#order-create-confirm__modal').one('hidden.bs.modal', function (e) {
            orders.createOrder(onCreateOrderSuccess, onCreateOrderError);
        });
        $('#order-create-confirm__modal').modal('hide');
    }

    function onCreateOrderSuccess(data) {
        that.getAndShowSellOrders();
        that.getAndShowBuyOrders();
        that.fillOrderCreationFormFields();
        /*that.clearOrdersCreationForm();*/
        successNoty(data.result, 'successOrder');
    }

    function onCreateOrderError(jqXHR, textStatus, errorThrown) {
    }

    /*... CALL CREATION THE SUBMITTED ORDER AND CONTROL RESULT*/

    /*PREPARE DATA FOR ACCEPTION ORDER ... */
    function orderBuyAccept(event) {
        event.preventDefault();
        orderAccept(event);
    }

    function orderSellAccept(event) {
        event.preventDefault();
        orderAccept(event);
    }

    /*... PREPARE DATA FOR ACCEPTION ORDER */

    /*CALL ACCEPTANCE THE ORDERS LIST AND CONTROL RESULT ... */
    function orderAccept(event) {
        event.preventDefault();
        var ordersList = that.ordersListForAccept.map(function (e) {
            return e.orderId;
        });
        console.log(ordersList);
        that.clearOrdersCreationForm();
        switchCreateOrAcceptButtons();
        orders.acceptOrder(ordersList, onAcceptOrderSuccess, onAcceptOrderError);
    }

    function onAcceptOrderSuccess(data) {
        that.ordersListForAccept = [];
        successNoty(data.result, 'successOrder');
    }

    function onAcceptOrderError(jqXHR, textStatus, errorThrown) {
        that.ordersListForAccept = [];
    }

    /*... CALL ACCEPTANCE THE ORDERS LIST AND CONTROL RESULT*/


}