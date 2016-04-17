<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="login.title"/></title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,700,300' rel='stylesheet' type='text/css'>

    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js" type="text/javascript"></script>
    <script src="<c:url value='/client/js/jquery.mCustomScrollbar.concat.min.js'/>" type="text/javascript"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link href="<c:url value='/client/css/jquery.mCustomScrollbar.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>
    <%----------%>
    <script type="text/javascript" src="<c:url value='/client/js/script.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
    <%----------%>
    <%--capcha--%>
    <script type="text/javascript" src="<c:url value='/client/js/capcha.js'/>"></script>
    <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit&hl=${pageContext.response.locale}"
            async defer>
    </script>

</head>


<body>

<%@include file='header_new.jsp' %>

<main class="container register">
    <hr>
    <div class="row">
        <div class="col-sm-4">
            <%--РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ--%>
            <h5><loc:message code="login.title"/></h5>

            <p style="color:red">${error}</p>

            <c:url value="/login" var="loginUrl"/>
            <form action="${loginUrl}" method="post">
                <%--Логин/email--%>
                <loc:message code="login.email" var="login"/>
                <input name="username" placeholder="${login}"
                       required autofocus/>
                <%--Пароль--%>
                <loc:message code="login.password" var="password"/>
                <input name="password" type="password" placeholder="${password}"
                       required="required"/>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <br/>
                <br/>
                <%--CAPCHA--%>
                <div id="cpch-field" class="g-recaptcha" data-sitekey=${captchaProperties.get("captcha.key")}></div>
                <p class='cpch-error-message' style="color:red">${cpch}</p>
                <br/>
                <%--ВОЙТИ--%>
                <br/>
                <button type="submit"><loc:message
                        code="login.submit"/></button>
            </form>
        </div>
    </div>
</main>
<%@include file='footer_new.jsp' %>

</body>
</html>

