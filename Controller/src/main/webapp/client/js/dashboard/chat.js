
var ws; //web-socket connection
var chatLanguage;

function connect(chatLang) {
    if (ws!=null) {
        ws.close();
    }
    ws = new SockJS('/chat-' + chatLang);
    ws.onmessage = function (message) {
        const messageObj = JSON.parse(message['data']);
        if ($.inArray("removed", Object.keys(messageObj)) >= 0) {
            removeMessageFromChatHistory(messageObj.id);
        } else {
            appendNewMessage(messageObj);
            if (chatLang === 'ar') {
                $('#chat').find('.chat_message:last').addClass('right-to-left')
            }
            if (chatLang === 'ko') {
                $('#chat').find('.chat_message:last').addClass('right-to-left')
            }
        }
    };
}


function toJson(a) {
    var o = {};
    $.each(a, function() {
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
}

function formatNewMessage(o) {
    var dateTime = o['time'].split(/[\s.]+/);
    var previousDate = $('.chat_message .message_date').last().text();
    var hidden = previousDate === dateTime[0] ? ' invisible' : '';
    return '<div class="chat_message">' +
        '<p class="message_date text-center ' + hidden + '">' + dateTime[0] + '</p><span class="message_id invisible">' + o['id'] +
        '</span> <span class="user_id invisible">' + o['userId'] + '</span> <p class="nickname">' + o['nickname']  +
         '<span class="message_time text-muted">' + dateTime[1] + '</span>' +
        '</p> <p class="message"><span class="message_body">'  + o['body']  + '</span></p></div>' ;

}

function appendNewMessage(messageObj) {
    const newMessage = formatNewMessage(messageObj);
    $(newMessage).appendTo($('#chat').find('.mCSB_container')).trigger('newMessage');
    scrollChat();
}

function loadChatHistory(lang) {
    $.ajax('/chat/history', {
        method: 'GET',
        data: 'lang=' + lang
    }).done(function (data) {
        for (var i = data.length - 1; i >=0; i--) {
            $('#chat').find('.mCSB_container').append(formatNewMessage(data[i]));
        }
        scrollChat();
        if (lang === 'ar') {
            $('#chat').find('.chat_message p').addClass('right-to-left')
        }
        $('#chatLangButtons').trigger('historyLoaded')
    }).fail(function(e){
        console.log(e)
    })
}

function changeChatLocale(lang) {
    chatLanguage = lang;
    if (lang === 'ar') {
        $('#new_mess').find('input[name="body"]').addClass('right-to-left');
    } else {
        $('#new_mess').find('input[name="body"]').removeClass('right-to-left');
    }
    $('#chat').find('.mCSB_container').empty();
    $('#new_mess').find('input[name="lang"]').val(lang);

    connect(lang);
    loadChatHistory(lang);
}


$(function () {
    var listLang = $("#language").text().toLowerCase().trim();
    changeChatLocale(listLang == 'id' ? 'in' : listLang);
    var bchat = document.getElementById('bchat'+(listLang == 'id' ? 'in' : listLang));
    if(bchat == null){
        bchat = document.getElementById('bchaten');
    }
    bchat.className += " active";

    var btnContainer = document.getElementById("chatLangButtons");
    var btns = btnContainer.getElementsByClassName("btna");
// Loop through the buttons and add the active class to the current/clicked button
    for (var i = 0; i < btns.length; i++) {
        btns[i].addEventListener("click", function() {
            var current = btnContainer.getElementsByClassName("active");
            current[0].className = current[0].className.replace(" active", "");
            this.className += " active";
        });
    }
    initScrollbar();

    $('.chat-locales>a:first-child').click(); // Connect to websocket and load chat history

    $('#new_mess').submit(function (e) {
        e.preventDefault();
        const payload = toJson($(this).serializeArray());
        $('#new_mess').find('input[name="body"]').val('');
        $.ajax('/chat/new-message', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            method: 'POST',
            data: payload
        }).done(function (e) {
            /*NOP*/
        }).fail(function (e) {
            console.log(e);
            const error = JSON.parse(e['responseText']);

            if(error['errorInfo']!=null){
                alert(error['errorInfo']);
            } else if(error['errorInfoSendChatMessageWithoutNickname']!=null){
                $('#errorInfoSendChatMessageWithoutNickname').modal('show');
            }
        });
    })
});

function initScrollbar() {
    $("#chat").mCustomScrollbar({
        theme:"dark",
        axis:"y",
        live: true
    });
}

function scrollChat() {
    $('#chat').mCustomScrollbar("scrollTo", "bottom", {
        scrollInertia:0
    });
}

function removeMessageFromChatHistory(id) {
    var $messageDiv =$('.chat_message').filter(function (index) {
        return parseInt($(this).find('.message_id').text()) === id;
    });
    var $prevMessageDiv = $messageDiv.prev();
    var $nextMessageDiv = $messageDiv.next();

    if ($prevMessageDiv.find('.message_date').text() != $nextMessageDiv.find('.message_date').text()) {
        $nextMessageDiv.find('.message_date').removeClass('invisible');
    }
    $messageDiv.remove();
}