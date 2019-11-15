<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 20.10.2016
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>To be redirected</title>
    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>

    <script type="text/javascript">
        $(function () {
            var data = $('#paymentForm').serialize();
            sendRequest(data)
        });


        function sendRequest(data) {
                $.ajax({
                    url: '/api/payments/preparePostPayment',
                    headers: {
                        'X-CSRF-Token': $("input[name='_csrf']").val(),
                        'Exrates-Rest-Token': $("#auth-token").text()
                    },
                    type: 'POST',
                    data: data
                }).success(function (result) {
                    if (result['redirectionUrl']) {
                        if (!result['method']) {
                            window.location = result['redirectionUrl'];
                        } else {
                            redirectByPost(
                                result['redirectionUrl'],
                                result);
                        }
                    }
                })
        }

        function redirectByPost(url, params) {
            var formFields = '';
            var method = params["method"];
            $.each(params["params"], function (key, value) {
                formFields += '<input type="hidden" name="' + key + '" value="' + value + '">';
            });
            var $form = $('<form id=temp-form-for-redirection action=' + url + ' method='+params["method"]+'>' + formFields + '</form>');
            $("body").append($form);
            $form.submit();
            $("#temp-form-for-redirection").remove();
        }


    </script>
</head>
<body>
<form id="paymentForm" method="post">
    <input type="hidden" id="currency" name="currency" value="${currency}" />
    <input type="hidden" id="sum" name="sum" value="${amount}" />
    <input type="hidden" id="merchant" name="merchant" value="${merchant}" />
    <input type="hidden" id="operationType" name="operationType" value="${operationType}" />
</form>
<span hidden id="auth-token">${authToken}</span>
</body>
</html>
