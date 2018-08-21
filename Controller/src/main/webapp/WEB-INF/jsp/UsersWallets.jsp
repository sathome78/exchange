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

    <div class="col-md-8 col-md-offset-1 content admin-container">

      <ul class="nav nav-tabs">

        <c:forEach var="mapRole" items="${mapUsersWalletsSummaryList}">
          <c:choose>
            <c:when test="${mapRole.key eq 'ALL'}">
              <li class="active">
            </c:when>
            <c:otherwise>
              <li>
            </c:otherwise>
          </c:choose>
          <a data-toggle="tab" href="#panel${mapRole.key}">${mapRole.key}</a>
          </li>
        </c:forEach>
      </ul>

      <div class="tab-content">

        <c:forEach var="mapRole" items="${mapUsersWalletsSummaryList}">
          <c:choose>
            <c:when test="${mapRole.key eq 'ALL'}">
              <c:set value="tab-pane fade in active" var="panelClass"/>
            </c:when>
            <c:otherwise>
              <c:set value="tab-pane fade" var="panelClass"/>
            </c:otherwise>
          </c:choose>
          <div id="panel${mapRole.key}" class='<c:out value="${panelClass}"/>'>
            <div class="text-center">
              <h4>
                <b><loc:message code="admin.usersWallet"/> ${mapRole.key}</b>
              </h4>
            </div>
          <div class="row">
              <button id="upload-users-ip-info" class="blue-box pull-right"
                      onclick="uploadUserIps('${mapRole.key}')" type="submit"><loc:message
                      code="wallets.downloadUserIps"/></button>
          </div>
          <div class="row">
              <button id="upload-users-wallets" class="blue-box pull-right"
                      onclick="uploadUserWallets('${mapRole.key}')" type="submit"><loc:message
                      code="wallets.download"/></button>
          </div>

          <div class="row">
              <button id="upload-users-wallets-inout" class="blue-box pull-right"
                      onclick="uploadUserWalletsInOut('${mapRole.key}')" type="submit">
                  <loc:message
                          code="wallets.downloadInputOutput"/></button>
          </div>

          <div class="row">
              <button id="upload-users-wallets-orders" class="blue-box pull-right"
                      onclick="uploadUserWalletsOrders('${mapRole.key}')" type="submit">
                  <loc:message
                          code="wallets.downloadOrders"/></button>
          </div>
          <div class="row">
              <button id="upload-users-wallets-orders-by-currency-pairs" class="blue-box pull-right"
                      onclick="uploadUserWalletsOrdersByCurrencyPairs('${mapRole.key}')" type="submit">
                  <loc:message
                          code="wallets.downloadOrdersByCurrencyPairs"/></button>
          </div>

            <div class="row">
              <button id="download-input-output-summery-report" class="blue-box pull-right"
                      onclick="uploadInputOutputSummaryReport('${mapRole.key}')" type="submit">
                <loc:message
                        code="wallets.downloadInputOutputReport"/></button>
            </div>

            <c:forEach var="wallet" items="${mapRole.value}">
              <div class="block">
                <div class="currency">${wallet.currencyName}</div>
                <p class="info-item info-item-title col-sm-12">
                  <loc:message code="wallets.amount"/>:
                    ${wallet.walletsAmount}
                </p>

                <p class="info-item col-sm-4">
                  <loc:message code="wallets.balance"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9" value="${wallet.balance}"/>
                </p>

                <p class="info-item next_item">
                  <loc:message code="wallets.average"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9"
                                    value="${wallet.balancePerWallet}"/>
                </p>
                <br/>

                <p class="info-item col-sm-4">
                  <loc:message code="wallets.abalance"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9"
                                    value="${wallet.activeBalance}"/>
                </p>

                <p class="info-item next_item">
                  <loc:message code="wallets.average"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9"
                                    value="${wallet.activeBalancePerWallet}"/>
                </p>
                <br/>

                <p class="info-item  col-sm-4">
                  <loc:message code="wallets.rbalance"/>:
                    ${wallet.reservedBalance}
                </p>

                <p class="info-item next_item">
                  <loc:message code="wallets.average"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9"
                                    value="${wallet.reservedBalancePerWallet}"/>
                </p>
                <br/>

                <p class="info-item  col-sm-4">
                  <loc:message code="wallets.totalInputAmount"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9"
                                    value="${wallet.merchantAmountInput}"/>
                </p>

                <p class="info-item next_item">
                  <loc:message code="wallets.totalOutputAmount"/>:
                  <fmt:formatNumber type="number" maxFractionDigits="9"
                                    value="${wallet.merchantAmountOutput}"/>
                </p>
              </div>
            </c:forEach>

          </div>
        </c:forEach>
      </div>

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

