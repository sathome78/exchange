/**
 * Created by OLEG on 11.11.2016.
 */



$(function () {
    if ($('#notification-icon').length > 0) {
        var $notificationContainer = $("#notifications-body");

        $('#notification-icon').find('.dropdown-menu').click(function(event){
            event.stopPropagation();
        });
        $("#notifications-body-wrapper").mCustomScrollbar({
            theme:"dark",
            axis:"y",
            live: true
        });
        var $counter = $('#unread-counter');
        var unreadQuantity = 0;
        $($counter).hide();
        $.ajax({
            url: '/notifications/findAll',
            type: 'GET',
            success: function (data) {
                if (data.length == 0) {
                    handleAbsentMessages();
                    return;
                }
                var $tmpl = $('#notifications-row').html().replace(/@/g, '%');
                $notificationContainer.find('.notification-item').remove();
                data.forEach(function (item) {
                    var $newItem = $(tmpl($tmpl, item));
                    if (!item.read) {
                        unreadQuantity++;
                    } else {
                        $($newItem).removeAttr('onclick');
                    }
                    $notificationContainer.prepend($newItem);

                });

                if (unreadQuantity > 0) {
                    $($counter).text(unreadQuantity);
                    $($counter).show();
                }
            }
        })
    }
});

function markRead(element) {
    var count = $('#unread-counter').text();
    var idData = $(element).find('.notification-id').serialize();
    $.ajax({
        url: '/notifications/markRead',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: idData,
        success: function () {
            $(element).removeClass('unread');
            $(element).addClass('read');
            $(element).removeAttr('onclick');
            $('#unread-counter').text(--count);
        }
    })
}


function removeNotification(e, element) {
    var event = e || window.event;
    event.stopPropagation();
    var unreadCount = $('#unread-counter').text();
    var isRead = $(element).parent().hasClass('read');
    var idData = $(element).parent().find('.notification-id').serialize();
    $.ajax({
        url: '/notifications/remove',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        data: idData,
        success: function () {
            $(element).parent().remove();
            if (!isRead) {
                $('#unread-counter').text(--unreadCount);
            }
            if (unreadCount == 0) {
                $('#unread-counter').hide();
            }
            if ($('.notification-item').length == 0) {
                handleAbsentMessages()
            }
        }
    })
}

function markReadAll() {
    var $counter = $('#unread-counter');
    $.ajax({
        url: '/notifications/markReadAll',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        success: function () {
            $('.notification-item').removeClass('unread');
            $('.notification-item').addClass('read');
            $counter.text(0);
            $counter.hide();
        }
    })

}

function removeAllNotifications() {
    var $counter = $('#unread-counter');
    $.ajax({
        url: '/notifications/removeAll',
        type: 'POST',
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        success: function () {
            $('.notification-item').remove();
            $counter.text(0);
            $counter.hide();
            handleAbsentMessages()
        }
    })
}

function handleAbsentMessages() {
    $('#notifications-absent').removeClass('invisible');
    $('#notifications-header').hide();
}