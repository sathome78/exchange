/**
 * Created by Valk on 18.06.2016.
 */

var leftSider;
var rightSider;
var trading;
var myWallets;
var myStatements;
var myHistory;
var orders;
var $currentPageMenuItem;
var $currentSubMenuItem;
var notifications;

var ordersSubscription;
var tradesSubscription;
var eventsSubscrition;
var alertsSubscription;
var currencyPairStatisticSubscription;
var personalSubscription;
var connectedPS = false;
var currentCurrencyPairId;
var currentPairName;
var subscribedCurrencyPairId;
var chartPeriod;
var newChartPeriod = null;

var socket_url = '/public_socket';
var socket;
var client;
var f = false;
var enableF = false;
var sessionId;
var email;
var csrf;
var reconnectsCounter = 0;

var timer;



var onConnectFail = function () {
    connectedPS = false;
    setTimeout(connectAndReconnect, 5000);
};

var onConnect = function() {
    connectedPS = true;
    subscribeAll()
};

function subscribeAll() {
    if (connectedPS && (subscribedCurrencyPairId != currentCurrencyPairId || f != enableF)) {
        subscribeTradeOrders();
    }
    if (connectedPS) {
        subscribeStatistics();
        subscribeForAlerts();
        subscribeEvents();
    }
    if (connectedPS && (subscribedCurrencyPairId != currentCurrencyPairId || newChartPeriod != chartPeriod)) {
    }
    if (connectedPS && subscribedCurrencyPairId != currentCurrencyPairId) {
        subscribeTrades();
        subscribeForMyTrades();
    }
}

function connectAndReconnect() {
    reconnectsCounter ++;
    console.log("try to reconnect " + reconnectsCounter);
    if (reconnectsCounter > 5) {
        location.reload()
    }
    socket = new SockJS(socket_url);
    client = Stomp.over(socket);
    client.debug = null;
    var headers = {'X-CSRF-TOKEN' : csrf};
    client.connect(headers, onConnect, onConnectFail);
}

function subscribeForAlerts() {
    if (alertsSubscription == undefined) {
        var lang = $("#language").text().toUpperCase().trim();
        console.log('lang ' + lang);
        var headers = {'X-CSRF-TOKEN': csrf};
        alertsSubscription = client.subscribe("/app/users_alerts/" + lang, function (message) {
            var messageBody = JSON.parse(message.body);
            messageBody.forEach(function (object) {
                handleAlerts(object);
            });
        }, headers);
    }
}


function subscribeForMyTrades() {
    if (personalSubscription != undefined) {
        personalSubscription.unsubscribe();
    }
    var headers = {'X-CSRF-TOKEN' : csrf};
    personalSubscription = client.subscribe("/user/queue/personal/" + currentCurrencyPairId, function(message) {
        var messageBody = JSON.parse(message.body);
        messageBody.forEach(function(object){
            initTrades(JSON.parse(object), currentCurrencyPairId);
        });
    }, headers);
}


function subscribeTradeOrders() {
    if (ordersSubscription != undefined) {
        ordersSubscription.unsubscribe();
    }
    var headers = {'X-CSRF-TOKEN' : csrf};
    var fn = enableF ? '/user/queue/trade_orders/f/' : '/app/trade_orders/';
    var tradeOrdersSubscr = fn + currentCurrencyPairId;
    ordersSubscription = client.subscribe(tradeOrdersSubscr, function(message) {
        subscribedCurrencyPairId = currentCurrencyPairId;
        var messageBody = JSON.parse(message.body);
        if (messageBody instanceof Array) {
            messageBody.forEach(function(object){
                initTradeOrders(object);
            });
        } else {
            initTradeOrders(message.body);
        }
    }, headers);
    f = enableF;
}


function subscribeTrades() {
    if (tradesSubscription != undefined) {
        tradesSubscription.unsubscribe();
    }
    var headers = {'X-CSRF-TOKEN' : csrf};
    var path = '/app/trades/' + currentCurrencyPairId;
    tradesSubscription = client.subscribe(path, function(message) {
        var messageBody = JSON.parse(message.body);
        messageBody.forEach(function(object){
            initTrades(JSON.parse(object), currentCurrencyPairId);
        });

    }, headers);
}

function subscribeStatistics() {
    if (currencyPairStatisticSubscription == undefined) {
        var headers = {'X-CSRF-TOKEN': csrf};
        var path = '/app/statistics/MAIN_CURRENCIES_STATISTIC';
        currencyPairStatisticSubscription = client.subscribe(path, function (message) {
            var messageBody = JSON.parse(message.body);
            messageBody.forEach(function(object){
                handleStatisticMessages(JSON.parse(object));
            });
        }, headers);
    }
}

function subscribeEvents() {
    if (eventsSubscrition == undefined) {
        var headers = {'X-CSRF-TOKEN': csrf};
        var path = '/app/ev/' + sessionId;
        eventsSubscrition = client.subscribe(path, function (message) {
            handleEventsMessage(message.body);
        }, headers);
    }
}

function handleAlerts(object) {
    switch (object.alertType){
        case "TECHNICAL_WORKS" : {
            drawTechAlert(object);
            break;
        }
        case "UPDATE" : {
            showHideUpdAlert(object);
            break;
        }
    }
}

function showHideUpdAlert(object) {
    var $container = $('#upd_alert');
    var $textContainer = $('#upd_alert_text');
    $textContainer.text('');
    if (object.enabled) {
        $container.show();
        $textContainer.text(object.text);
        drawUpdateALert(object);
    } else {
        $('.countdown').final_countdown(null, null, true);
        $container.hide();
    }
}

function drawUpdateALert(object) {
    var remain = object.timeRemainSeconds;
        var timeNow = Date.now()/1000;
        var endTime = timeNow + (remain);
        $('.countdown').final_countdown({
            start: timeNow,
            end: endTime,
            now: timeNow,
        selectors: {
                value_seconds: '.clock-seconds .val',
                canvas_seconds: 'canvas_seconds',
                value_minutes: '.clock-minutes .val',
                canvas_minutes: 'canvas_minutes',
                value_hours: '.clock-hours .val',
                canvas_hours: 'canvas_hours',
                value_days: '.clock-days .val',
                canvas_days: 'canvas_days'
            },
            seconds: {
                borderColor: '#7995D5',
                borderWidth: '6'
            },
            minutes: {
                borderColor: '#ACC742',
                borderWidth: '6'
            },
            hours: {
                borderColor: '#ECEFCB',
                borderWidth: '6'
            }},
        function() {
            // Finish callback
        });
}

function drawTechAlert(object) {
    var $tech_block = $('#tech_alert');
    var $tech_text = $('#tech_alert_text');
    if(object.enabled) {
        $tech_block.show();
        $tech_text.text(object.text)
    } else {
        $tech_block.hide();
        $tech_text.text('')
    }
}

function handleStatisticMessages(object) {
    switch (object.type){
        case "MAIN_CURRENCIES_STATISTIC" : {
            leftSider.updateStatisticsForAllCurrencies(object.data);
            break;
        }
        case "MAIN_CURRENCY_STATISTIC" : {
            object.data.forEach(function(object){
                leftSider.updateStatisticsForCurrency(object);
            });

            break;
        }
    }
}

function handleEventsMessage(data) {
    if (!data) return;
    if (data.indexOf("redirect") > 0) {
        data = JSON.parse(data);
        var registered = $('#hello-my-friend')[0];
        var noty = '';
        if (data.redirect.url && registered) {
            window.location = data.redirect.url;
            /*noty = "?errorNoty=" + data.redirect.successQR;*/
        }
    }

}

function initTrades(object, currentCurrencyPair) {
    if (object.currencyPairId != currentCurrencyPair) {
        return;
    }
    switch (object.type){
        case "ALL_TRADES" : {
            trading.updateAndShowAllTrades(object.data);
            break;
        }
        case "MY_TRADES" : {
            trading.updateAndShowMyTrades(object.data);
            break;
        }
    }
}


function initTradeOrders(object) {
    object = JSON.parse(object);
    if (object.currencyPairId != subscribedCurrencyPairId) {
        return
    }
    switch (object.type){
        case "BUY" : {
            trading.updateAndShowBuyOrders(object.data, true);
            break;
        }
        case "SELL" : {
            trading.updateAndShowSellOrders(object.data, true);
            break;
        }
    }
}




$(function dashdoardInit() {
    sessionId = $('#session').text();
    csrf = $('.s_csrf').val();
    var $2faModal = $('#noty2fa_modal');
    var $2faConfirmModal = $('#noty2fa_confirm_modal');
    try {
        /*FOR EVERYWHERE ... */
        $(".input-block-wrapper__input").prop("autocomplete", "off");
        $(".numericInputField").prop("autocomplete", "off");
        $(".numericInputField")
            .keypress(
                function (e) {
                    var decimal = $(this).val().split('.')[1];
                    if (decimal && decimal.length >= trading.ROUND_SCALE) {
                        return false;
                    }
                    if (e.charCode >= 48 && e.charCode <= 57 || e.charCode == 46 || e.charCode == 44 || e.charCode == 0) {
                        var keyPressed = e.key == ',' ? '.' : e.key;

                        if (keyPressed == '.' && $(this).val().indexOf('.') >= 0) {
                            return false;
                        }
                        var str = $(this).val() + keyPressed;
                        if (str.length > 1 && str.indexOf('0') == 0 && str.indexOf('.') != 1) {
                            return false
                        }
                    } else {
                        return false;
                    }
                    return true;
                }
            )
            .on('input', function (e) {
                var val = $(this).val();
                if (val[val.length - 1] == ',') {
                    val = val.replace(',', '.');
                    $(this).val(val)
                }
                var regx = /^(^[1-9]+\d*((\.{1}\d*)|(\d*)))|(^0{1}\.{1}\d*)|(^0{1})$/;
                var result = val.match(regx);
                if (!result || result[0] != val) {
                    $(this).val('');
                }
                var decimal = $(this).val().split('.')[1];
                if (decimal && decimal.length >= trading.ROUND_SCALE) {
                    $(this).val(+(+$(this).val()).toFixed(trading.ROUND_SCALE));
                }
            });
        /*... FOR EVERYWHERE*/

        /*FOR HEADER...*/
        notifications = new NotificationsClass();

        $('#menu-traiding').on('click', onMenuTraidingItemClick);
        function onMenuTraidingItemClick(e) {
            if (e) e.preventDefault();
            trading.syncCurrencyPairSelector(currentPairName);
            showPage('trading');
            trading.updateAndShowAll();
            trading.fillOrderCreationFormFields();
        }

        $('#menu-mywallets').on('click', function (e) {
            e.preventDefault();
            if (!e.ctrlKey) {
                showPage('balance-page');
                myWallets.getAndShowMyWalletsData();
            } else {
                window.open('/dashboard?startupPage=balance-page', '_blank');
                return false;
            }
        });
        $('#menu-myhistory').on('click', function (e) {
            e.preventDefault();
            if (!e.ctrlKey) {
                showPage('myhistory');
                myHistory.updateAndShowAll();
                myHistory.updateActiveTab();
            } else {
                window.open('/dashboard?startupPage=myhistory', '_blank');
                return false;
            }
        });
        $('#menu-orders').on('click', function (e) {
            e.preventDefault();
            if (!e.ctrlKey) {
                orders.syncCurrencyPairSelector();
                showPage('orders');
                orders.updateAndShowAll();
            } else {
                window.open('/dashboard?startupPage=orders', '_blank');
                return false;
            }
        });
    //TODO temporary disabled
    //    $('#login-qr').html("<img src='https://chart.googleapis.com/chart?chs=150x150&chld=L|2&cht=qr&chl=" + sessionId + "'>");
        /*...FOR HEADER*/

        /*FOR LEFT-SIDER ...*/


        $('#currency_table').on('click', 'td:first-child', function (e) {
            var newCurrentCurrencyPairName = $(this).text().trim();
            syncCurrentParams(newCurrentCurrencyPairName, null, null, null, null, 'MAIN', function (data) {
                if ($currentPageMenuItem.length) {
                    $currentPageMenuItem.click();
                    if ($currentSubMenuItem && $currentSubMenuItem.length) {
                        $currentSubMenuItem.click();
                    }
                } else {
                    onMenuTraidingItemClick();
                }
            });
            trading.fillOrderCreationFormFields();
            });
        $('#currency_table_wrapper, #mywallets_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true

        });
        /*...FOR LEFT-SIDER*/

        /*FOR CENTER ON START UP ...*/

        $('#orders-sell-table-wrapper, #orders-buy-table-wrapper, #orders-history-table-wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "y",
            live: true
        });


        syncCurrentParams(null, null, null, null, null, 'MAIN', function (data) {
            showPage($('#startup-page-id').text().trim());

            var url = '/dashboard/createPairSelectorMenu';
            $.ajax({
                url: url,
                type: 'GET',
                success: function (cpData) {
                    if (!cpData) return;
                    trading = new TradingClass(data.currencyPair.name, data.orderRoleFilterEnabled, subscribeChart, cpData);
                    newChartResolution = data.period;

                    leftSider = new LeftSiderClass();
                    leftSider.setOnWalletsRefresh(function () {
                        trading.fillOrderBalance($('.currency-pair-selector__button').first().text().trim())
                    });

                    myWallets = new MyWalletsClass();
                    myStatements = new MyStatementsClass();
                    myHistory = new MyHistoryClass(data.currencyPair.name, cpData);
                    orders = new OrdersClass(data.currencyPair.name, cpData);
                    /**/
                    connectAndReconnect();
                }
            });
            showSubPage($('#startup-subPage-id').text().trim());
        });
        /*...FOR CENTER ON START UP*/

        /*FOR RIGHT-SIDER ...*/
        $('#news_table_wrapper').mCustomScrollbar({
            theme: "dark",
            axis: "yx",
            live: true
        });

        rightSider = new RightSiderClass();
        /*...FOR RIGHT-SIDER*/

        /*FOR POLL ...*/
       /*var startPoll = $("#start-poll").val() == 'true';
        if (startPoll) {
            var $pollDialog = $("#poll-modal");
            $pollDialog.modal();
            doPoll($pollDialog);
        }*/
        /*...FOR POLL*/
        /*2fa notify*/

        var notify2fa = $("#noty2fa").val() == 'true';
        if (notify2fa) {
          $2faModal.modal({
              backdrop: 'static',
              keyboard: false
          });
        }
        /*end 2fa notify*/
    } catch (e) {
        /*it's need for ignoring error from old interface*/
    }

    $('#decline_2fa').on('click', function () {
        $2faModal.modal('hide');
        $2faConfirmModal.modal({
            backdrop: 'static',
            keyboard: false
        });
    });

    $('#decline_2fa_finally').on('click', function () {
        $2faConfirmModal.modal('hide');
    });

    $('.accept_2fa').on('click', function () {
        window.location.href = '/settings?2fa';
    });
});



function showPage(pageId) {
    if (!pageId) {
        return;
    }
    $('.center-frame-container').addClass('hidden');
    $('#' + pageId).removeClass('hidden');
    $currentPageMenuItem = $('#' + $('#' + pageId).data('menuitemid'));
}

function showSubPage(subPageId) {
    if (subPageId) {
        $currentSubMenuItem = $('#' + $('#' + subPageId).data('submenuitemid'));
        $($currentSubMenuItem).click();
    }
}


function syncCurrentParams(currencyPairName, period, chart, showAllPairs, enableFilter, cpType, callback) {
    var url = '/dashboard/currentParams?';
    /*if parameter is empty, in response will be retrieved current value is set or default if non*/

    if($("#preferedCurrencyPairName").val()!=""){
        currencyPairName = $("#preferedCurrencyPairName").val();
        $("#preferedCurrencyPairName").val("");
    }
    url = url + (currencyPairName ? '&currencyPairName=' + currencyPairName : '');
    url = url + (period ? '&period=' + period : '');
    url = url + (chart ? '&chart=' + chart : '');
    url = url + (showAllPairs != null ? '&showAllPairs=' + showAllPairs : '');
    url = url + (enableFilter != null ? '&orderRoleFilterEnabled=' + enableFilter : '');
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            /*sets currencyBaseName for all pages*/
            $('.currencyBaseName').text(data.currencyPair.currency1.name);
            $('.currencyConvertName').text(data.currencyPair.currency2.name);
            /**/
            currentCurrencyPairId = data.currencyPair.id;
            currentPairName = data.currencyPair.name;
            enableF = enableFilter;
            if (period != null) {
                newChartResolution = period;
            }
            subscribeAll();
            if (callback) {
                callback(data);
            }
        }
    });
}

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


function doPoll($pollDialog) {
    Survey.Survey.cssType = "bootstrap";
    var surveyToken;
    var surveyData = getSurveyData(function (response) {
        var surveyData = response;
        surveyToken = surveyData.token;
        var surveyJSON = JSON.parse(surveyData.json);
        var surveyItems = surveyData.items;
        /**/
        surveyJSON.locale = $.cookie("myAppLocaleCookie");
        surveyJSON.pages.forEach(function (page, pi) {
            page.elements.forEach(function (element, ei) {
                var name = element.name;
                surveyItems.forEach(function (item) {
                    if (item.name == name) {
                        surveyJSON.pages[pi].elements[ei].title = item.title;
                        return;
                    }
                })
            });
        });
        $pollDialog.find("#description").html(surveyData.description);
        /**/
        var survey = new Survey.Model(surveyJSON);
        $("#surveyContainer").Survey({
            model: survey,
            onComplete: sendDataToServer
        });
    });

    function sendDataToServer(survey) {
        survey.sendResult(surveyToken);
        var result = JSON.stringify(survey.data);
        savePollAsDone(surveyToken, result);
    }

    function getSurveyData(callback) {
        $.ajax({
            type: 'GET',
            url: '/survey/getSurvey',
            success: function (data) {
                callback(data);
            }
        });
    }

    function savePollAsDone(surveyToken, result) {
        $.ajax({
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val(),
            },
            contentType: "text/plain; charset=utf-8",
            type: 'POST',
            url: '/survey/saveAsDone?surveyToken=' + surveyToken,
            data: result,
        });
    }

    function successRegister (event) {
        if ($('#successRegister').text() != undefined ) {
            yaCounter47624182.reachGoal('sendregister');
            console.log('it works!');
            return true;
        }
    }
}
