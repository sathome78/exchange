<%@page language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <!--[if lt IE 9]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title><loc:message code="admin.refillAdresses"/></title>
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <%@include file='links_scripts.jsp' %>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery-ui.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery-ui.js'/>"></script>
    <link rel="stylesheet" href="<c:url value="/client/css/jquery.datetimepicker.css"/>">
    <script type="text/javascript" src="<c:url value='/client/js/jquery.datetimepicker.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/moment-with-locales.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/client/js/inputOutput/refillAdresses.js'/>"></script>

</head>

<body id="addresses-admin">
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>
        <div class="col-md-8 col-sm-offset-1 content admin-container">


            <div class="row text-center"><h4><loc:message code="admin.refillAdresses"/></h4></div>
            <div class="col-md-11">
                <button data-toggle="collapse" class="blue-box" style="margin: 10px 0;"
                        data-target="#withdrawal-request-filter">
                    <loc:message code="admin.user.transactions.extendedFilter"/></button>
                <div id="withdrawal-request-filter" class="collapse">
                    <form id="addresses-search-form" class="form_full_height_width" method="get">
                        <%--CURRENCY--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="transaction.currency"/>
                                </label>
                            </div>
                            <div class="col-md-9 ">
                                <ul class="checkbox-grid">
                                    <c:forEach items="${currencies}" var="currency">
                                        <li><input type="checkbox" name="currencyIds"
                                                   value="${currency.id}"><span>${currency.name}</span></li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>

                        <%--USER EMAIL--%>
                        <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label class="input-block-wrapper__label">
                                    <loc:message code="login.email"/>
                                </label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <input id="filter-email" class="input-block-wrapper__input admin-form-input" name="email">
                            </div>
                        </div>

                            <%--ADDRESS--%>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label">
                                        <loc:message code="refill.address"/>
                                    </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="filter-address" class="input-block-wrapper__input admin-form-input" name="address">
                                </div>
                            </div>



                        <button id="filter-apply" class="blue-box"><loc:message
                                code="admin.user.transactions.applyFilter"/></button>
                        <button id="filter-reset" class="blue-box"><loc:message
                                code="admin.user.transactions.resetFilter"/></button>
                    </form>

                </div>
            </div>

            <table id="addressesTable">
                <thead>
                <tr>
                    <th><loc:message code="admin.email"/></th>
                    <th><loc:message code="refill.currency"/></th>
                    <th><loc:message code="refill.address"/></th>
                    <th><loc:message code="admin.address.additionalFieldName"/></th>
                    <th><loc:message code="refill.requestDatetime"/></th>
                    <th><loc:message code="refill.needTransfer"/></th>
                </tr>
                </thead>

            </table>
        </div>
    </div>

</main>

<%@include file='../fragments/footer.jsp' %>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
