<%--&lt;%&ndash;--%>
  <%--Created by IntelliJ IDEA.--%>
  <%--User: Valk--%>
  <%--Date: 11.05.2016--%>
  <%--Time: 19:30--%>
  <%--To change this template use File | Settings | File Templates.--%>
<%--&ndash;%&gt;--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>--%>
<%--<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>--%>
<%--<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>--%>
<%--<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
    <%--<meta charset="utf-8" />--%>
    <%--<!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->--%>
    <%--<title><loc:message code="manageOrder.title"/></title>--%>
    <%--<meta name="keywords" content="" />--%>
    <%--<meta name="description" content="" />--%>
    <%--<meta name="viewport" content="width=device-width, initial-scale=1.0" />--%>
    <%--<%@include file='links_scripts.jsp'%>--%>
    <%--<script type="text/javascript" src="<c:url value='/client/js/order/adminDeleteOrder.js'/>"></script>--%>
<%--</head>--%>

<%--<body>--%>
<%--<%@include file='../fragments/header-simple.jsp'%>--%>
<%--<main class="container">--%>
    <%--<div class="row">--%>
        <%--<%@include file='left_side_menu.jsp' %>--%>
        <%--<div class="row">--%>
        <%--<div class="col-md-6 col-md-offset-2 content admin-container">--%>
            <%--<div class="text-center">--%>
                <%--<h4 class="modal-title"><loc:message code="ordersearch.title"/></h4>--%>
            <%--</div>--%>
            <%--<button data-toggle="collapse" class="blue-box" style="margin: 10px 0;" data-target="#order-search">--%>
                <%--<loc:message code="admin.user.transactions.extendedFilter"/> </button>--%>
            <%--<a href="/2a8fy7b07dxe44/removeStopOrder" class="blue-box" style="margin: 10px 0;" data-target="#order-search">--%>
                <%--<loc:message code="myorders.stoporders"/> </a>--%>

            <%--<div id="order-search" class="collapse">--%>


                <%--<form id="delete-order-info__form" class="form_full_height_width" action="/2a8fy7b07dxe44/searchorders" method="get">--%>

                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label">Id</label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<input type="number" id="orderId" name="orderId" placeholder="0"/>--%>
                        <%--</div>--%>
                        <%--<div for="orderId" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="orderId" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errornumber"/></label>--%>
                        <%--</div>--%>
                    <%--</div>--%>


                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message--%>
                                    <%--code="ordersearch.currencypair"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<select id="currencyPair" class="input-block-wrapper__input admin-form-input" name="currencyPairId">--%>
                                    <%--<option value="">ANY</option>--%>
                                <%--<c:forEach items="${currencyPairList}" var="currencyPair">--%>
                                    <%--<option value="${currencyPair.id}">${currencyPair.name}</option>--%>
                                <%--</c:forEach>--%>
                            <%--</select>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message code="ordersearch.type"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<select id="orderType" class="input-block-wrapper__input admin-form-input" name="orderType">--%>
                                <%--<option value="">ANY</option>--%>
                                <%--<c:forEach items="${operationTypes}" var="type">--%>
                                    <%--<option value="${type.type}">${type.name()}</option>--%>
                                <%--</c:forEach>--%>
                            <%--</select>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message code="orderinfo.status"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<select id="orderStatus" class="input-block-wrapper__input admin-form-input" name="statusId">--%>
                                <%--<option value="">ANY</option>--%>
                                <%--<c:forEach items="${statusList}" var="status">--%>
                                    <%--<option value="${status.status}">${status.name()}</option>--%>
                                <%--</c:forEach>--%>
                            <%--</select>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label">--%>
                                <%--<loc:message code="ordersearch.date" />--%>
                            <%--</label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<input id="datetimepicker_start" type="text" name="dateFrom">--%>
                            <%--<input id="datetimepicker_end" type="text" name="dateTo">--%>
                        <%--</div>--%>
                        <%--<div for="datetimepicker_start" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="datetimepicker_start" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errordatetime"/></label>--%>
                        <%--</div>--%>
                        <%--<div for="datetimepicker_end" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="datetimepicker_end" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errordatetime"/></label>--%>
                        <%--</div>--%>

                    <%--</div>--%>
                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message code="ordersearch.rate"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<input type="number" id="orderRateFrom" name="exrateFrom" placeholder="0.0"/>--%>
                            <%--<input type="number" id="orderRateTo" name="exrateTo" placeholder="0.0"/>--%>
                        <%--</div>--%>
                        <%--<div for="orderRateFrom" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="orderRateFrom" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errornumber"/></label>--%>
                        <%--</div>--%>
                        <%--<div for="orderRateTo" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="orderRateTo" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errornumber"/></label>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message code="ordersearch.volume"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<input type="number" id="orderVolumeFrom" name="volumeFrom"--%>
                                   <%--placeholder="0.0"/>--%>
                            <%--<input type="number" id="orderVolumeTo" name="volumeTo"--%>
                                   <%--placeholder="0.0"/>--%>
                        <%--</div>--%>
                        <%--<div for="orderVolumeFrom" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="orderVolumeFrom" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errornumber"/></label>--%>
                        <%--</div>--%>
                        <%--<div for="orderVolumeTo" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="orderVolumeTo" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.errornumber"/></label>--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message--%>
                                    <%--code="orderinfo.creator"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<input id="creatorEmail" name="creator" class="input-block-wrapper__input admin-form-input"--%>
                                   <%--placeholder="user@user.com"/>--%>
                        <%--</div>--%>
                        <%--<div for="creatorEmail" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="creatorEmail" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.erroremail"/></label>--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message code="orderinfo.creator.role"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<select id="creatorRole" class="input-block-wrapper__input admin-form-input" name="creatorRole">--%>
                                <%--<option value="">ANY</option>--%>
                                <%--<c:forEach items="${roleList}" var="role">--%>
                                    <%--<option value="${role.role}">${role.name()}</option>--%>
                                <%--</c:forEach>--%>
                            <%--</select>--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="input-block-wrapper">--%>
                        <%--<div class="col-md-3 input-block-wrapper__label-wrapper">--%>
                            <%--<label class="input-block-wrapper__label"><loc:message--%>
                                    <%--code="orderinfo.acceptor"/></label>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-9 input-block-wrapper__input-wrapper">--%>
                            <%--<input id="acceptorEmail" name="acceptor" class="input-block-wrapper__input admin-form-input"--%>
                                   <%--placeholder="user@user.com"/>--%>
                        <%--</div>--%>
                        <%--<div for="acceptorEmail" hidden class="col-md-7 input-block-wrapper__error-wrapper">--%>
                            <%--<label for="acceptorEmail" class="input-block-wrapper__input"><loc:message--%>
                                    <%--code="ordersearch.erroremail"/></label>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="delete-order-info__button-wrapper">--%>
                        <%--<button id="delete-order-info__search" class="delete-order-info__button blue-box"--%>
                                <%--type="button"><loc:message code="ordersearch.submit"/></button>--%>
                        <%--<button id="delete-order-info__reset" class="delete-order-info__button blue-box"--%>
                                <%--type="button"><loc:message code="admin.user.transactions.resetFilter"/></button>--%>

                    <%--</div>--%>

                <%--</form>--%>

            <%--</div>--%>
            <%--<div class="row">--%>
                <%--<table id="order-info-table">--%>
                    <%--<thead>--%>
                    <%--<tr>--%>
                        <%--<th></th>--%>
                        <%--<th><loc:message code="orderinfo.id"/></th>--%>
                        <%--<th><loc:message code="orderinfo.createdate"/></th>--%>
                        <%--<th><loc:message code="orderinfo.currencypair"/></th>--%>
                        <%--<th><loc:message code="orders.type"/></th>--%>
                        <%--<th><loc:message code="orderinfo.rate"/></th>--%>
                        <%--<th><loc:message code="orderinfo.baseamount"/></th>--%>
                        <%--<th><loc:message code="orderinfo.creator"/></th>--%>
                        <%--<th><loc:message code="orderinfo.creator.role"/></th>--%>
                        <%--<th><loc:message code="orderinfo.status"/></th>--%>
                    <%--</tr>--%>
                    <%--</thead>--%>
                <%--</table>--%>
            <%--</div>--%>

        <%--</div>--%>
        <%--</div>--%>

    <%--</div>--%>
<%--</main>--%>

<%--<span hidden id="selectAllButtonLoc"><loc:message code="admin.orders.selectAllButton" /></span>--%>
<%--<span hidden id="selectNoneButtonLoc"><loc:message code="admin.orders.selectNoneButton" /></span>--%>
<%--<span hidden id="acceptSelectedButtonLoc"><loc:message code="admin.orders.acceptSelectedButton" /></span>--%>
<%--<span hidden id="deleteSelectedButtonLoc"><loc:message code="admin.orders.deleteSelectedButton" /></span>--%>
<%--<span hidden id="promptAcceptLoc"><loc:message code="admin.orders.promptAccept" /></span>--%>
<%--<span hidden id="promptDeleteLoc"><loc:message code="admin.orders.promptDelete" /></span>--%>

<%--<%@include file='order-modals.jsp' %>--%>
<%--<%@include file='../fragments/footer.jsp' %>--%>
<%--<span hidden id="errorNoty">${errorNoty}</span>--%>
<%--<span hidden id="successNoty">${successNoty}</span>--%>
<%--</body>--%>
<%--</html>--%>

