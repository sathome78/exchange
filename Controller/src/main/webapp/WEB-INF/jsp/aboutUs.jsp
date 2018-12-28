<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title><loc:message code="dashboard.aboutUs"/></title>
    <%@include file="../jsp/tools/google_head.jsp"%>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <script src="<c:url value="/client/js/jquery_1.11.3.min.js"/>" type="text/javascript"></script>
    <link href="<c:url value='/client/css/bootstrap.min.css'/>" rel="stylesheet">
    <link href="<c:url value='/client/css/style-new.css'/>" rel="stylesheet">

</head>


<body>

<%@include file='header_new.jsp' %>
<%@include file="../jsp/tools/google_body.jsp"%>

<main class="container register">
    <hr>
    <div class="row">
        <div class="col-sm-4">
            <%--About us--%>
            <h4><loc:message code="dashboard.aboutUs"/></h4>
            <br/>
                <div class="content">
                    <h4><loc:message code="about_us.contacts"/></h4>
                    <h4><loc:message code="about_us.telephone" arguments="${telephone}"/></h4>
                    <h4><loc:message code="about_us.email" arguments="${email}"/></h4>
                </div>
        </div>
    </div>
</main>
<%@include file='fragments/footer.jsp' %>

<script type="text/javascript" src="<c:url value='/client/js/bootstrap.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/locale.js'/>"></script>
<script type="text/javascript" src="<c:url value='/client/js/menuSwitcher.js'/>"></script>

</body>
</html>

