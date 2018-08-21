<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 17.10.2016
  Time: 15:41
  To change this template use File | Settings | File Templates.
--%>
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
    <%----------%>
    <script type="text/javascript" src="<c:url value="/client/js/ekko-lightbox.min.js"/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/dataTable/adminStatementsDataTable.js"/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>

<main class="container orders_new admin side_menu">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-md-offset-1 content admin-container">
            <table id="user-statements-table">
                <thead>
                <tr>
                    <th class="col-3 center blue-white"><loc:message code="mystatement.datetime"/></th>
                    <th class="col-13 center blue-white"><loc:message code="mystatement.activebefore"/></th>
                    <th class="col-13 center blue-white"><loc:message code="mystatement.reservedbefore"/></th>
                    <th class="col-06 center blue-white"><loc:message code="mystatement.operationtype"/></th>
                    <th class="col-2 center blue-white"><loc:message code="mystatement.amount"/></th>
                    <th class="col-2 center blue-white"><loc:message code="mystatement.commissionamount"/></th>
                    <th class="col-06 center blue-white"><loc:message code="mystatement.sourcetype"/></th>
                    <th class="col-13 center blue-white"><loc:message code="mystatement.activeafter"/></th>
                    <th class="col-13 center blue-white"><loc:message code="mystatement.reservedafter"/></th>
                </tr>
                </thead>
            </table>

        </div>
        <hr>
    </div>
</main>
<span id="walletId" hidden>${walletId}</span>

</body>
</html>
