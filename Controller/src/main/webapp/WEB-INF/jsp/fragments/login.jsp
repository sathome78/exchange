<%--
  Created by Sasha
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create password</title>

    <%@include file="../tools/google_head.jsp" %>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>

    <!-- New design -->
    <link rel="stylesheet" href="client/assets/css/main.min.css">
    <link rel="stylesheet" href="client/assets/css/libs.min.css">

    <script src="<c:url value="/client/assets/js/libs.min.js"/>"></script>
    <script src="<c:url value="/client/assets/js/main.min.js"/>"></script>
    <!-- New design -->

</head>
<body>
<%@include file="../tools/google_body.jsp" %>
<input id="loginError" hidden value='${error}'/>
<input type="hidden" class="s_csrf" name="${_csrf.parameterName}" value="${_csrf.token}"/>

<a id="login_page_link" data-fancybox href="#login" class="demo-bar-item / js-coverbox" style="display: none">finish</a>
<div id="login" class="popup">
    <div class="popup__inner">
        <div class="popup__caption">Log in</div>
        <c:if test="${not empty error}">
            <div class='field__error'>
                    ${error}
            </div>
        </c:if>
        <form id="login_form" action="/login" class="form" method="post">
            <input type="hidden" class="csrfC" name="_csrf" value="${_csrf.token}"/>
            <div class="field">
                <div class="field__label">Email</div>
                <input class="field__input" type="email" name="username" placeholder="Email" required>
            </div>
            <div class="field">
                <div class="field__label">Password</div>
                <div class="field__pwd-show / js-show-pwd"></div>
                <input class="field__input / js-pwd" type="password" name="password" placeholder="Password" required>
            </div>

            <div class="field field--btn">
                <input id="login_submit" class="btn btn--form" value="Authorise me">
            </div>
        </form>
    </div>
</div>

</body>
</html>
