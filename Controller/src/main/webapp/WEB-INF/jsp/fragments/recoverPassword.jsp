<%--
  Created by Sasha
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Password recovery</title>

    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style.css'/>" rel="stylesheet">

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>

    <!-- New design -->
    <link href="<c:url value='/client/assets/css/main.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/assets/css/libs.min.css'/>" rel="stylesheet">

    <script src="<c:url value="/client/assets/js/libs.min.js"/>"></script>
    <script src="<c:url value="/client/assets/js/main.min.js"/>"></script>
    <!-- New design -->

</head>
<body>

<a id="forgot_pwd_hide" data-fancybox href="#recovery_confirmed" class="popup__bottom-link">Forgot password?</a>

<input id="recoveryConfirm" hidden value='${recoveryConfirm}'/>
<input type="hidden" class="s_csrf" name="${_csrf.parameterName}" value="${_csrf.token}"/>

<a id="recovery_confirmed_link" data-fancybox href="#recovery_confirmed" class="demo-bar-item / js-coverbox" style="display: none">finish</a>
<div id="recovery_confirmed" class="popup">
    <div class="popup__inner">
        <div class="popup__caption">Password recovery</div>

        <div class="popup__sub-caption">
            Now, we need to create strong password.
        </div>

        <form action="/dashboard/updatePasswordbytoken" class="form" method="post">
            <input type="hidden"  class="csrfC" name="_csrf" value="${_csrf.token}"/>
            <input type="hidden" name="token" value="${token.value}">
            <div class="field">
                <div class="field__label">Password</div>
                <div class="field__pwd-show / js-show-pwd"></div>
                <input id="password" class="field__input / js-pwd" type="password" name="password" placeholder="Password" required>
                <div id="password_wrong" class='field__error' style="display:none">
                    Password cannot be less than 8 and more than 20 characters long and should contain of letters (a-z), numbers (0-9) and/or any combination of @*%!#^!&$<> characters
                </div>
                <div id="password_required" class='field__error' style="display:none">
                    Password is required
                </div>
            </div>

            <div class="field field--btn__new">
                <input id="pass_submit" class="btn__new btn__new--form" type="submit" value="Update password" disabled>
            </div>
        </form>
    </div>
</div>
</body>
</html>

