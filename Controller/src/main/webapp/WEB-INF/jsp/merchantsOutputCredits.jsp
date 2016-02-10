<%@page language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="paymentForm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
    <title></title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />

    <link href="<c:url value="/client/css/bootstrap.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/chosen.css"/>" rel="stylesheet" type="text/css" />
    <link href="<c:url value="/client/css/style.css"/>" rel="stylesheet" type="text/css" />

    <script type="text/javascript" src="<c:url value="/client/js/jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/dropdown.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/modal.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/chosen.jquery.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/function.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/client/js/main.js"/>"></script>

</head>

<body>

<div class="wrapper lk">


    <div class="container container_center full__height">

        <!--#include file="sidebar__lk.shtml" -->
        <%@include file='usermenu.jsp'%>

        <div class="main__content">
            <%@include file='header.jsp'%>
            <!--#include file="header__lk.shtml" -->

            <div class="content__page">

                <div class="title__page">Вывод средств</div>
            <c:choose>
                <c:when test="${empty wallets}">
                    <loc:message code="merchants.noWallet"/>
                </c:when>
                <c:otherwise>
                    <!-- Start  withdraw__money -->
                    <c:url value="/merchants/yandexmoney/payment/output" var="url"/>
                    <paymentForm:form class="form-horizontal withdraw__money" name="payment" method="post" modelAttribute="payment" action="${url}">
                        <div class="form-group">
                            <label class="col-sm-3 control-label" for="#">Валюта к выводу</label>
                            <div class="col-sm-8">
                                <paymentForm:select id="currencySelect" path="currency" onchange="loadMeansOfPayment()" class="select form-control">
                                    <paymentForm:options items="${wallets}" itemLabel="fullName" itemValue="currId" />
                                </paymentForm:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label" for="#"><loc:message code="merchants.meansOfPayment"/></label>
                            <div class="col-sm-8">
                                <paymentForm:select id="meansOfPaymentSelect" path="meansOfPayment">
                                </paymentForm:select>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-8">
                                <paymentForm:input  class="form-control" placeholder="Сумма" id="#" path="sum"/>
                            </div>
                        </div>
                        <paymentForm:input  type="hidden" id="meanOfPaymentId" path="meansOfPaymentId"/>
                        <div class="form-group">
                            <div class="col-sm-offset-3 col-sm-6">
                                <button type="button" data-toggle="modal" name="assertOutputPay" data-target="#myModal2" class="btn btn-primary">Вывести</button>
                            </div>
                        </div>
                    </paymentForm:form>
                </c:otherwise>
            </c:choose>
                <!-- End  withdraw__money -->
                <%@include file='footer.jsp'%>
                <!--#include file="footer__lk.shtml" -->
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="myModal2">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Вывод средств</h4>
                <p>Вы хотите вывести 800 USD.</p>
                <p>Комиссия составляет — 0.1BTC</p>
            </div>
            <div class="modal-body">
                <label class="control-label" for="#">Введите email либо номер кошелька :</label>
                <div class="">
                    <input class="form-control" name="walletUid" type="text" id="#" placeholder="">
                </div>
            </div>
            <div class="modal-footer">
                <div class="add__money__btns">
                    <button type="button" name="paymentOutput" class="btn btn-primary">Продолжить</button>
                    <button type="button" class="btn btn-warning">Отменить</button>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal

</body>
</html>