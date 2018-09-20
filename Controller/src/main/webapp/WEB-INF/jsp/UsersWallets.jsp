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
  <title><loc:message code="admin.usersWallet"/></title>
  <link href="<c:url value='/client/img/favicon.ico'/>" rel="shortcut icon" type="image/x-icon"/>

  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <%@include file='admin/links_scripts.jsp' %>

  <link href='<c:url value="/client/css/roboto-font-400_700_300.css"/>' rel='stylesheet' type='text/css'>
  <link href="/client/css/jquery-ui.css" rel='stylesheet' type='text/css'>

  <script type="text/javascript" src="<c:url value='/client/js/reportAdmin.js'/>"></script>
  <%----------%>
</head>

<body>

<%@include file='fragments/header-simple.jsp' %>

<main class="container my_wallets">
  <div class="row">
    <%@include file='admin/left_side_menu.jsp' %>

    <div class="col-md-8 col-sm-offset-1  content admin-container">

      <ul class="nav nav-tabs">
        <li class="active"><a data-toggle="tab" id="panelADMIN" class=" blue-box margin-box">ADMIN</a></li>
        <li><a data-toggle="tab"  id="panelUSER"  class=" blue-box margin-box">USER</a></li>
        <li><a data-toggle="tab"  id="panelEXCHANGE" class=" blue-box margin-box">EXCHANGE</a></li>
        <li><a data-toggle="tab"  id="panelVIP_USER"  class=" blue-box margin-box">VIP_USER</a></li>
        <li><a data-toggle="tab"  id="panelTRADER"  class=" blue-box margin-box">TRADER</a></li>
        <li><a data-toggle="tab" id="panelALL" class=" blue-box margin-box">ALL</a></li>
      </ul>

      <div class="tab-content">
          <div class="tab-pane fade in active">
            <div class="text-center">
              <h4>
                <b><loc:message code="admin.usersWallet"/> <span class='value'></span></b>
              </h4>
            </div>
          <div class="row">
              <button id="upload-users-ip-info" class="blue-box pull-right"
                      onclick="uploadUserIps()" type="submit"><loc:message
                      code="wallets.downloadUserIps"/></button>
          </div>
          <div class="row">
              <button id="upload-users-wallets" class="blue-box pull-right"
                      onclick="uploadUserWallets()" type="submit"><loc:message
                      code="wallets.download"/></button>
          </div>

          <div class="row">
              <button id="upload-users-wallets-inout" class="blue-box pull-right"
                      onclick="uploadUserWalletsInOut()" type="submit">
                  <loc:message
                          code="wallets.downloadInputOutput"/></button>
          </div>

          <div class="row">
              <button id="upload-users-wallets-orders" class="blue-box pull-right"
                      onclick="uploadUserWalletsOrders()" type="submit">
                  <loc:message
                          code="wallets.downloadOrders"/></button>
          </div>
          <div class="row">
              <button id="upload-users-wallets-orders-by-currency-pairs" class="blue-box pull-right"
                      onclick="uploadUserWalletsOrdersByCurrencyPairs()" type="submit">
                  <loc:message
                          code="wallets.downloadOrdersByCurrencyPairs"/></button>
          </div>

            <div class="row">
              <button id="download-input-output-summery-report" class="blue-box pull-right"
                      onclick="uploadInputOutputSummaryReport()" type="submit">
                <loc:message
                        code="wallets.downloadInputOutputReport"/></button>
            </div>

      </div>

      </div>
       <br>
            <table id="walletsSummaryTable">
                <thead>
                <tr>
                    <th>currency name</th>
                    <th><loc:message code="wallets.amount"/></th>
                    <th><loc:message code="wallets.balance"/></th>
                    <th><loc:message code="wallets.average"/></th>
                    <th><loc:message code="wallets.abalance"/></th>
                    <th><loc:message code="wallets.average"/></th>
                    <th><loc:message code="wallets.rbalance"/></th>
                    <th><loc:message code="wallets.average"/></th>
                    <th><loc:message code="wallets.totalInputAmount"/></th>
                    <th><loc:message code="wallets.totalOutputAmount"/></th>
                </tr>
                </thead>
            </table>

    </div>

  </div>
  <hr>
</main>
<%@include file='fragments/footer.jsp' %>
<%@include file='admin/datePicker.jsp' %>
<%@include file="fragments/modal/loading_modal.jsp" %>
<%@include file='fragments/modal/dialogWithCurrencyAndDateAndDirection_modal.jsp' %>
</body>
</html>

