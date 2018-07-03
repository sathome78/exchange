$(document).delegate('*[data-toggle="lightbox"]', 'click', function (event) {
    event.preventDefault();
    $(this).ekkoLightbox();
    if ($(".delete_img").length !== 0) {
        $(".delete_img").submit(function (e) {
            e.preventDefault();
            var array = $(this).serializeArray();
            var id = array[0]['value'];
            var path = array[1]['value'];
            var userId = array[2]['value'];
            promptDeleteDoc(id, path, userId);
        });
    }

    if ($('#usersTable').length !== 0) {
        $('#usersTable').DataTable();
    }
});

$(function () {
    $('.adminForm-toggler').click(function () {
        if ($(this).hasClass('active')) {
            return;
        } else {
            $('.tab-pane').removeClass('active');
            $('.adminForm-toggler').removeClass('active');
            $(this).addClass('active');
            var idx = $(this).index();
            $('.tab-pane:eq(' + idx + ')').addClass('active');
        }
    });

    //Enable REGISTER button if pass == repass when entering repass
    /*Activates submit button if all field filled correct and capcha is passed
     * */
    if (document.getElementById("register_button")) {
        checkFill();
        /*document.getElementById("register_button").disabled = true;*/
    }

    $("#repass").keyup(function () {
        var pass = $('#pass').val();
        var repass = $('#repass').val();


        if (pass && (pass.length != 0) && (pass === repass)) {
            $('.repass').css("display", "block");
            $('.repass-error').css("display", "none");
        }
        else {
            $('.repass-error').css("display", "block");
            $('.repass').css("display", "none");
        }
        checkFill();

    });
    $("#pass").keyup(function () {
        var pass = $('#pass').val();
        var repass = $('#repass').val();
        if (repass && (repass.length != 0)) {
            if (pass === repass) {
                $('.repass').css("display", "block");
                $('.repass-error').css("display", "none");
            } else {
                $('.repass-error').css("display", "block");
                $('.repass').css("display", "none");
            }
        }

        checkFill();
    });

    $('#login, #email').keyup(checkFill)

});

function checkFill() {
    var email = $('#email').val();
    var login = $('#login').val();
    var pass = $('#pass').val();
    var repass = $('#repass').val();
    var capchaPassed = $('#cpch-field').hasClass('passed');
    if ((!$('#email')[0] || (email.length != 0) && (login.length != 0)) &&
        (capchaPassed) && (pass && (pass.length != 0) && (pass === repass))) {
        $("#register_button").prop('disabled', false);
    } else {
        $("#register_button").prop('disabled', true);
    }
}

$(function () {
    /*Activates submit button if all field filled correct (email, password, captcha) on login page (/login)
    and on /dashboard page for login model panel
     * */
    if (document.getElementById("login_button")) {
        checkFillOnLoginPage();
    }
    $('#login__name, #login__password, #captchaCode').keyup(checkFillOnLoginPage)
});

function checkFillOnLoginPage() {
    var email = $('#login__name').val();
    var password = $('#login__password').val();
    var captchaCode = $('#captchaCode').val();

    if (email && password && captchaCode) {
        $("#login_button").prop('disabled', false);
    } else {
        $("#login_button").prop('disabled', true);
    }
}


function promptDeleteDoc(id, path, userId) {
    if (confirm($('#prompt_delete_rqst').html())) {
        var data = "fileId=" + id + "&path=" + path + "&userId=" + userId;
        $.ajax('/2a8fy7b07dxe44/users/deleteUserFile', {
            headers: {
                'X-CSRF-Token': $("input[name='_csrf']").val()
            },
            type: 'POST',
            dataType: 'json',
            data: data
        }).done(function (result) {
            alert(result['success']);
            $('.modal').modal('hide');
            $('#_' + id).remove();
        }).fail(function (error) {
            console.log(JSON.stringify(error));
            alert(error['responseJSON']['error'])
        });
    }
}





