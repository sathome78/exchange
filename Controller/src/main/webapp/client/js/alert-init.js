
var alertsSubscription;
var connectedPS = false;
var socket_url = '/public_socket';
var socket;
var client;
var f = false;
var sessionId;
var csrf;
var reconnectsCounter = 0;
var timer;

var onConnectFail = function () {
    connectedPS = false;
    setTimeout(connectAndReconnect, 5000);
};

var onConnect = function() {
    connectedPS = true;
    subscribeForAlerts()
};

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

function handleAlerts(object) {
    switch (object.alertType){
        case "TECHNICAL_WORKS" : {
            drawTechAlert(object);
            break;
        }
        case "SYSTEM_MESSAGE_TO_USERS" : {
            drawSysMessageForUsersAlert(object);
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

function drawSysMessageForUsersAlert(object) {
    var $sys_mess_block = $('#sys_message_alert');
    var $sys_mess_title = $('#sys_message_alert_title');
    var $sys_mess_text = $('#sys_message_alert_text');
    if(object.enabled) {
        $sys_mess_block.show();
        $sys_mess_title.html(object.title);
        $sys_mess_text.html(object.text);
    } else {
        $sys_mess_block.hide();
        $sys_mess_title.html('');
        $sys_mess_text.html('');
    }
}

/**
 * Show text via click on title of system message
 */
$(function () {
    var acc = document.getElementsByClassName("accordion");
    var i;

    for (i = 0; i < acc.length; i++) {
        acc[i].addEventListener("click", function() {
            this.classList.toggle("active");
            var panel = this.nextElementSibling;
            if (panel.style.maxHeight){
                panel.style.maxHeight = null;
            } else {
                panel.style.maxHeight = panel.scrollHeight + "px";
            }
        });
    }
});

$(function alertInit() {
    sessionId = $('#session').text();
    csrf = $('.s_csrf').val();
    connectAndReconnect();
});

