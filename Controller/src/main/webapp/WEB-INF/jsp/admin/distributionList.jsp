<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title><loc:message code="admin.title"/></title>
    <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>

    <%@include file='links_scripts.jsp' %>

    <link rel="stylesheet" href="<c:url value="/client/css/font-awesome.min.css"/>">
    <link href="<c:url value="/client/css/ekko-lightbox.min.css"/>" rel="stylesheet">
    <script type="text/javascript" src="<c:url value='/client/js/app.js'/>"></script>

    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/freecoinsDistributionList.js'/>"></script>

<body>
<%@include file='../fragments/header-simple.jsp' %>

<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 content admin-container">

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="text-center">
                <h4 class="modal-title">Free coins</h4>
            </div>
            <br>
            <table id="freecoinsTable" style="width:100%; cursor: pointer">
                <thead>
                <tr>
                    <th class="col-2 center blue-white"><loc:message code="freecoins.createdAt"/></th>
                    <th class="col-2 center blue-white"><loc:message code="freecoins.currency"/></th>
                    <th class="col-2 center blue-white"><loc:message code="freecoins.totalAmount"/></th>
                    <th class="col-2 center blue-white"><loc:message code="freecoins.period"/></th>
                    <th class="col-3 center blue-white"><loc:message code="freecoins.prizeAmount"/></th>
                    <th class="col-2 center blue-white"><loc:message code="freecoins.quantityOfPrizes"/></th>
                    <th class="col-3 center blue-white"><loc:message code="freecoins.quantityOfPrizesLeft"/></th>
                    <th class="col-3 center blue-white"><loc:message code="freecoins.uniqueAcceptors"/></th>
                    <th class="col-3 center blue-white"><loc:message code="freecoins.creator"/></th>
                    <th class="col-3 center blue-white"><loc:message code="freecoins.status"/></th>
                    <th class="col-3 center blue-white"><loc:message code="freecoins.revoke"/></th>
                </tr>
                </thead>
            </table>
            <br>
        </div>
    </div>
</main>

</body>
</html>