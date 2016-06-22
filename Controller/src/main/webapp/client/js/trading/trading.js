/**
 * Created by Valk on 02.06.2016.
 */

function TradingClass(period, chartType, currentCurrencyPair) {
    if (TradingClass.__instance) {
        return TradingClass.__instance;
    } else if (this === window) {
        return new TradingClass();
    }
    TradingClass.__instance = this;
    /**/
    var that = this;
    var chart = null;

    var $dashboardContainer = $('#dashboard');
    var dashboardCurrencyPairSelector;
    var refreshInterval = 5000*REFRESH_INTERVAL_MULTIPLIER;
    var timeOutId;
    /**/
    var showLog = false;
    /**/
    this.ordersListForAccept = [];
    /**/
    this.commissionSell;
    this.commissionBuy;

    function onCurrencyPairChange() {
        that.updateAndShowAll();
    }

    this.syncCurrencyPairSelector = function () {
        dashboardCurrencyPairSelector.syncState();
    };

    this.updateAndShowAll = function (refreshIfNeeded) {
        that.getAndShowAcceptedOrdersHistory(refreshIfNeeded, function () {
            that.getAndShowStatisticsForCurrency();
            that.getAndShowChart();
        });
        that.getAndShowSellOrders(refreshIfNeeded);
        that.getAndShowBuyOrders(refreshIfNeeded);
    };

    this.getAndShowStatisticsForCurrency = function () {
        if ($dashboardContainer.hasClass('hidden')) {
            return;
        }
        var url = '/dashboard/ordersForPairStatistics';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                $('#lastOrderAmountBase>span').text(data.lastOrderAmountBase + ' ' + data.currencyPair.currency1.name);
                $('#firstOrderRate>span').text(data.firstOrderRate + ' ' + data.currencyPair.currency2.name);
                $('#lastOrderRate>span').text(data.lastOrderRate + ' ' + data.currencyPair.currency2.name);
                $('#sumBase>span').text(data.sumBase + ' ' + data.currencyPair.currency1.name);
                $('#sumConvert>span').text(data.sumConvert + ' ' + data.currencyPair.currency2.name);
                $('#minRate').text(data.minRate + ' ' + data.currencyPair.currency2.name);
                $('#maxRate').text(data.maxRate + ' ' + data.currencyPair.currency2.name);
            }
        });
    };

    this.getAndShowChart = function () {
        if ($dashboardContainer.hasClass('hidden')) {
            return;
        }
        if (chart) {
            chart.drawChart();
        }
    };

    this.getAndShowAcceptedOrdersHistory = function (refreshIfNeeded, callback) {
        if ($dashboardContainer.hasClass('hidden')) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowAcceptedOrdersHistory');
        }
        var $ordersHistoryTable = $('#orders-history-table').find('tbody');
        var url = '/dashboard/acceptedOrderHistory?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#orders-history-table_row').html().replace(/@/g, '%');
                    $ordersHistoryTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $ordersHistoryTable.append(tmpl($tmpl, e));
                    });
                    blink($ordersHistoryTable);
                    callback();
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
    };

    this.getAndShowSellOrders = function (refreshIfNeeded) {
        if ($dashboardContainer.hasClass('hidden')) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowSellOrders');
        }
        var $ordersSellTable = $('#dashboard-orders-sell-table').find('tbody');
        var url = '/dashboard/sellOrders?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#dashboard-orders-sell-table_row').html().replace(/@/g, '%');
                    $ordersSellTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $ordersSellTable.append(tmpl($tmpl, e));
                    });
                    blink($('#dashboard-orders-sell-table'));
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
    };

    this.getAndShowBuyOrders = function (refreshIfNeeded) {
        if ($dashboardContainer.hasClass('hidden')) {
            return;
        }
        if (showLog) {
            console.log(new Date() + '  ' + refreshIfNeeded + ' ' + 'getAndShowBuyOrders');
        }
        var $ordersBuyTable = $('#dashboard-orders-buy-table').find('tbody');
        var url = '/dashboard/BuyOrders?refreshIfNeeded=' + (refreshIfNeeded ? 'true' : 'false');
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (!data) return;
                if (data.length == 0 || data[0].needRefresh) {
                    var $tmpl = $('#dashboard-orders-buy-table_row').html().replace(/@/g, '%');
                    $ordersBuyTable.find('tr').has('td').remove();
                    data.forEach(function (e) {
                        $ordersBuyTable.append(tmpl($tmpl, e));
                    });
                    blink($('#dashboard-orders-buy-table'));
                }
                clearTimeout(timeOutId);
                timeOutId = setTimeout(function () {
                    that.updateAndShowAll(true);
                }, refreshInterval);
            }
        });
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

    function calculateFieldsForBuy() {
        var amount = +$('#amountBuy').val();
        var exchangeRate = +$('#exchangeRateBuy').val();
        var totalForBuy = +$('#totalForBuy').val(amount * exchangeRate).val();
        var commission = that.commissionBuy;
        var calculatedCommissionForBuy = totalForBuy * commission / 100;
        var totalWithCommissionForBuy = totalForBuy + calculatedCommissionForBuy;
        $('#totalForBuy>span:first').text(totalForBuy.toFixed(9));
        $('#calculatedCommissionForBuy>span:first').text(calculatedCommissionForBuy.toFixed(9));
        $('#totalWithCommissionForBuy>span:first').text(totalWithCommissionForBuy.toFixed(9));
    }

    function calculateFieldsForSell() {
        var amount = +$('#amountSell').val();
        var exchangeRate = +$('#exchangeRateSell').val();
        var totalForSell = +$('#totalForSell').val(amount * exchangeRate).val();
        var commission = that.commissionSell;
        var calculatedCommissionForSell = totalForSell * commission / 100;
        var totalWithCommissionForSell = totalForSell - calculatedCommissionForSell;
        $('#totalForSell>span:first').text(totalForSell.toFixed(9));
        $('#calculatedCommissionForSell>span:first').text(calculatedCommissionForSell.toFixed(9));
        $('#totalWithCommissionForSell>span:first').text(totalWithCommissionForSell.toFixed(9));
    }

    /*=========================================================*/
    (function init(period, chartType, currentCurrencyPair) {
        getOrderCommissions();
        dashboardCurrencyPairSelector = new CurrencyPairSelectorClass('dashboard-currency-pair-selector', currentCurrencyPair);
        dashboardCurrencyPairSelector.init(onCurrencyPairChange);
        try {
            chart = new ChartGoogleClass();
        } catch (e){}
        try {
            chart = new ChartAmchartsClass("STOCK", period);
        } catch (e){}
        if (chart) {
            try {
                chart.init(chartType);
            } catch(e) {}
        }
        that.updateAndShowAll(false);
        /**/
        $('#amountBuy').on('keyup', calculateFieldsForBuy).on('keydown', resetOrdersListForAccept);
        $('#exchangeRateBuy').on('keyup', calculateFieldsForBuy).on('keydown', resetOrdersListForAccept);
        $('#amountSell').on('keyup', calculateFieldsForSell).on('keydown', resetOrdersListForAccept);
        $('#exchangeRateSell').on('keyup', calculateFieldsForSell).on('keydown', resetOrdersListForAccept);
        /**/
        $('.dashboard-order__table').on('click', '.dashboard-order__tr', fillOrdersFormFromCurrentOrder);
        /**/
        $('#dashboard-buy').on('click', orderBuy);
        $('#dashboard-sell').on('click', orderSell);
        /**/
        $('#dashboard-buy-accept').on('click', orderBuyAccept);
        $('#dashboard-sell-accept').on('click', orderSellAccept);
        /**/
        $('#order-create-confirm__submit').on('click', orderCreate);
        /**/
        switchCreateOrAcceptButtons();
    })(period, chartType, currentCurrencyPair);

    function fillOrdersFormFromCurrentOrder() {
        that.ordersListForAccept = [];
        /**/
        var orderAmountSumm = 0;
        $(this).prevAll('.dashboard-order__tr').each(function (i, e) {
            var orderId = $(e).find('.order_id').text();
            var orderType = $(e).find('.order_type').text();
            var orderAmount = $(e).find('.order_amount').text();
            var orderExRate = $(e).find('.order_exrate').text();
            var data = {
                orderId: orderId,
                orderType: orderType,
                orderAmount: orderAmount,
                orderExRate: orderExRate
            };
            that.ordersListForAccept.unshift(data);
            orderAmountSumm += parseNumber(orderAmount);
        });
        var orderId = $(this).find('.order_id').text();
        var orderType = $(this).find('.order_type').text();
        var orderAmount = $(this).find('.order_amount').text();
        var orderExRate = parseNumber($(this).find('.order_exrate').text());
        var data = {
            orderId: orderId,
            orderType: orderType,
            orderAmount: orderAmount,
            orderExRate: orderExRate
        };
        that.ordersListForAccept.push(data);
        orderAmountSumm += parseNumber(orderAmount);
        /**/
        $('#amountBuy').val(orderAmountSumm);
        $('#exchangeRateBuy').val(orderExRate);
        $('#amountSell').val(orderAmountSumm);
        $('#exchangeRateSell').val(orderExRate);
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
            $('#dashboard-buy-accept').addClass('hidden');
            $('#dashboard-sell').removeClass('hidden');
            $('#dashboard-buy').removeClass('hidden');
        }
        if (acceptedOrderType == 'BUY') {
            $('#dashboard-sell-accept').removeClass('hidden');
            $('#dashboard-buy-accept').addClass('hidden');
            $('#dashboard-sell').addClass('hidden');
            $('#dashboard-buy').removeClass('hidden');
        }
        if (acceptedOrderType == 'SELL') {
            $('#dashboard-sell-accept').addClass('hidden');
            $('#dashboard-buy-accept').removeClass('hidden');
            $('#dashboard-sell').removeClass('hidden');
            $('#dashboard-buy').addClass('hidden');
        }
    }

    function resetOrdersListForAccept() {
        if (that.ordersListForAccept.length != 0) {
            that.ordersListForAccept = [];
            switchCreateOrAcceptButtons();
        }
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

    /*...PREPARE DATA FOR MODAL DIALOG FOR CREATION ORDER */

    /*MODAL DIALOG FOR CREATION ORDER ... */
    function showOrderCreateDialog(data) {
        /**/
        var $balanceErrorContainer = $('#order-create-confirm__modal').find('[for=balance]');
        $balanceErrorContainer.empty();
        var $amountErrorContainer = $('#order-create-confirm__modal').find('[for=amount]');
        $amountErrorContainer.empty();
        var $exrateErrorContainer = $('#order-create-confirm__modal').find('[for=exrate]');
        $exrateErrorContainer.empty();
        $('#order-create-confirm__submit').removeClass('hidden');
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            url: '/order/submitnew/' + data.operationType,
            data: data,
            type: 'POST',
            success: function (data) {
                $('#order-create-confirm__modal').find('#operationTypeName').val(data.operationTypeName);
                $('#order-create-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                $('#order-create-confirm__modal').find('#balance').val(data.balance);
                $('#order-create-confirm__modal').find('#amount').val(data.amount);
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
                }
                var data = responseData.order;
                if (data) {
                    $('#order-create-confirm__modal').find('#operationTypeName').val(data.operationTypeName);
                    $('#order-create-confirm__modal').find('#currencyPairName').val(data.currencyPairName);
                    $('#order-create-confirm__modal').find('#balance').val(data.balance);
                    $('#order-create-confirm__modal').find('#amount').val(data.amount);
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
        leftSider.getStatisticsForMyWallets();
        successNoty(data.result);
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
            return parseInt(e.orderId);
        });
        orders.acceptOrder(ordersList, onAcceptOrderSuccess, onAcceptOrderError);
    }

    function onAcceptOrderSuccess(data) {
        that.ordersListForAccept = [];
        switchCreateOrAcceptButtons();
        that.updateAndShowAll();
        leftSider.getStatisticsForMyWallets();
        leftSider.getStatisticsForAllCurrencies();
        successNoty(data.result);
    }

    function onAcceptOrderError(jqXHR, textStatus, errorThrown) {
        that.ordersListForAccept = [];
        switchCreateOrAcceptButtons();
    }

    /*... CALL ACCEPTANCE THE ORDERS LIST AND CONTROL RESULT*/


}