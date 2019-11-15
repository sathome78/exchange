<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 28.11.2016
  Time: 10:09
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
<html>
<head>
    <title><loc:message code="admin.autoTrading.title"/></title>
    <%@include file='links_scripts.jsp' %>
    <script type="text/javascript" src="<c:url value='/client/js/admin-autotrading/autotrading.js'/>"></script>
</head>
<body>
<%@include file='../fragments/header-simple.jsp' %>
<main class="container">
    <div class="row">
        <%@include file='left_side_menu.jsp' %>


            <div class="col-md-8 col-md-offset-1 content admin-container">
                <div class="text-center"><h4><loc:message code="admin.autoTrading.title"/></h4>

            <div id="autotradingMenu" class="buttons">
                <button class="active adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.bot"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.bot.trading"/>
                </button>
                <button class="adminForm-toggler blue-box">
                    <loc:message code="admin.autoTrading.settings.roles"/>
                </button>
            </div>
                </div>
            <div class="tab-content">

                <div id="panel1" class="tab-pane active">
                    <div class="col-md-8 col-md-offset-2">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.bot"/></h4></div>
                        <div>
                            <c:choose>
                                <c:when test="${not empty bot}">
                                    <div class="row">
                                        <form id="bot-settings-form" class="form_full_height_width">
                                            <input id="bot-id" hidden value="<c:out value="${bot.id}"/>" readonly>
                                            <input id="bot-user-id" hidden value="<c:out value="${bot.userId}"/>" readonly>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.email"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">

                                                    <a class="input-block-wrapper__input admin-form-input text-1_5"
                                                       href="<c:url value="/2a8fy7b07dxe44/userInfo">
                                                   <c:param name = "id" value = "${bot.userId}"/></c:url>">${botUser.email}</a>
                                                </div>
                                            </div>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label for="bot-enabled-box" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.status"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper blue-switch">
                                                    <input id="bot-enabled-box" type="checkbox" name="isEnabled" <c:out value="${bot.enabled ? 'checked' : ''}"/> class="input-block-wrapper__input">
                                                </div>
                                            </div>
                                            <div class="input-block-wrapper">
                                                <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                    <label for="timeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.timeout"/></label>
                                                </div>
                                                <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                    <input id="timeout" type="number" name="acceptDelayInSeconds" value="<c:out value="${bot.acceptDelayInMillis}"/>"
                                                           class="input-block-wrapper__input admin-form-input">
                                                </div>
                                            </div>

                                            <button id="submitBotGeneralSettings" class="blue-box"><loc:message code="admin.submit"/></button>
                                        </form>
                                    </div>

                                </c:when>
                                <c:otherwise>
                                    <div class="row text-center">
                                        <p class="text-1_5 red"><loc:message code="admin.autoTrading.bot.notCreated"/> </p>
                                    </div>
                                    <form id="bot-creation-form" class="form_full_height_width">
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="nickname" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.nickname"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="nickname" name="nickname" class="input-block-wrapper__input admin-form-input">
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="email" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.email"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="email" type="email" name="email" class="input-block-wrapper__input admin-form-input">
                                            </div>
                                        </div>
                                        <div class="input-block-wrapper">
                                            <div class="col-md-4 input-block-wrapper__label-wrapper">
                                                <label for="password" class="input-block-wrapper__label"><loc:message code="admin.password"/></label>
                                            </div>
                                            <div class="col-md-8 input-block-wrapper__input-wrapper">
                                                <input id="password" type="password" name="password" class="input-block-wrapper__input admin-form-input">
                                            </div>
                                        </div>

                                        <button id="submitNewBot" class="blue-box"><loc:message code="admin.submit"/></button>
                                    </form>

                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div id="panel2" class="tab-pane">
                    <div class="col-md-8 col-md-offset-2">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.bot.trading"/></h4></div>
                        <div>
                            <c:choose>
                                <c:when test="${not empty bot}">
                                    <div class="row">
                                        <table id="launch-settings-table">
                                            <thead>
                                            <tr>
                                                <th><loc:message code="currency.pair"/></th>
                                                <th><loc:message code="admin.autoTrading.bot.status"/> </th>
                                                <th><loc:message code="admin.autoTrading.settings.considerRange"/> </th>
                                                <th><loc:message code="admin.autoTrading.bot.launchInterval"/></th>
                                                <th><loc:message code="admin.autoTrading.bot.createTimeout"/></th>
                                                <th><loc:message code="admin.autoTrading.bot.quantityPerSeq"/></th>
                                                <th></th>
                                                <th></th>
                                            </tr>
                                            </thead>
                                        </table>

                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="row text-center">
                                        <p class="text-1_5 red"><loc:message code="admin.autoTrading.bot.notCreated"/> </p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div id="panel3" class="tab-pane">
                    <div class="col-md-6 col-md-offset-3">
                        <div class="text-center"><h4><loc:message code="admin.autoTrading.settings.roles"/></h4></div>


                        <hr/>

                        <table id="roles-table">
                            <thead>
                            <tr>
                                <th><loc:message code="admin.autoTrading.role"/> </th>
                                <th><loc:message code="admin.autoTrading.sameRoleOnly"/></th>
                                <th><loc:message code="admin.autoTrading.botAccept"/></th>
                                <th><loc:message code="admin.autoTrading.considerRange"/></th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>



        </div>
</main>

<div id="editLaunchSettingsModal" class="modal fade">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span>&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.autoTrading.settings.bot.launch"/> - <span id="launch-title-pair"></span></h4>
            </div>
            <div class="modal-body">
                <form id="launch-settings-form" class="form_full_width form_auto_height">
                    <input hidden id="launch-settings-id" name="id">
                    <input hidden id="launch-currency-pair-id" name="currencyPairId">
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="launchInterval" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.launchInterval"/></label>
                        </div>
                        <div class="col-md-5 col-md-offset-2 input-block-wrapper__input-wrapper">
                            <input id="launchInterval" name="launchIntervalInMinutes" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="createTimeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.createTimeout"/></label>
                        </div>
                        <div class="col-md-5 col-md-offset-2 input-block-wrapper__input-wrapper">
                            <input id="createTimeout" name="createTimeoutInSeconds" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-5 input-block-wrapper__label-wrapper">
                            <label for="quantityPerSeq" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.bot.quantityPerSeq"/></label>
                        </div>
                        <div class="col-md-5 col-md-offset-2 input-block-wrapper__input-wrapper">
                            <input id="quantityPerSeq" name="quantityPerSequence" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <button id="submitLaunchSettings" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
                </form>
            </div>
        </div>
    </div>
</div>

<div id="editTradeSettingsModal" class="modal fade">
    <div class="modal-dialog modal-lg" style="width: 1200px">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span>&times;</span></button>
                <h4 class="modal-title"><loc:message code="admin.autoTrading.settings.bot.trading"/> - <span id="trade-title-pair"></span></h4>
            </div>
            <div class="modal-body">
                <form id="trade-settings-form-sell" class="form_full_width form_auto_height">
                    <input hidden id="trade-settings-id-sell" name="id">

                    <div class="input-block-wrapper">
                        <div class="col-md-12 input-block-wrapper__label-wrapper text-center">
                            <label class="input-block-wrapper__label"><loc:message code="operationtype.SELL"/></label>
                        </div>
                    </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.amountLimits"/></label>
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.max"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="maxAmountSell" name="maxAmount" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.min"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="minAmountSell" name="minAmount" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="createTimeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.priceLimits"/></label>
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.max"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="maxPriceSell" name="maxPrice" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.min"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="minPriceSell" name="minPrice" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="createTimeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.priceDeviation"/></label>
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label for="maxPriceDeviationSell" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.max"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="maxPriceDeviationSell" name="maxDeviationPercent" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label for="minPriceDeviationSell" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.min"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="minPriceDeviationSell" name="minDeviationPercent" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="isPriceStepRandomSell" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.randomStep"/></label>
                        </div>
                        <div class="col-md-1 col-md-offset-1 input-block-wrapper__input-wrapper pull-left">
                            <input id="isPriceStepRandomSellInput" name="priceStepRandom" type="hidden">
                            <input id="isPriceStepRandomSell" class="input-block-wrapper__input" type="checkbox">
                        </div>
                        <div class="col-md-3 col-md-offset-1 input-block-wrapper__label-wrapper">
                            <label for="priceStepDeviationSell" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.stepDeviation"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="priceStepDeviationSell" name="priceStepDeviationPercent" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="priceStepSell" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.priceStep"/></label>
                        </div>
                        <div class="col-md-4 col-md-offset-1 input-block-wrapper__input-wrapper">
                            <input id="priceStepSell" name="priceStep" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>


                </form>
                <hr/>
                <form id="trade-settings-form-buy" class="form_full_width form_auto_height">
                    <div class="input-block-wrapper">
                        <div class="col-md-12 input-block-wrapper__label-wrapper text-center">
                            <label class="input-block-wrapper__label"><loc:message code="operationtype.BUY"/></label>
                        </div>
                    </div>
                    <input hidden id="trade-settings-id-buy" name="id">
                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.amountLimits"/></label>
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.max"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="maxAmountBuy" name="maxAmount" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.min"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="minAmountBuy" name="minAmount" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="createTimeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.priceLimits"/></label>
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.max"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="maxPriceBuy" name="maxPrice" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.min"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="minPriceBuy" name="minPrice" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="createTimeout" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.priceDeviation"/></label>
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.max"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="maxPriceDeviationBuy" name="maxDeviationPercent" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                        <div class="col-md-1 input-block-wrapper__label-wrapper">
                            <label class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.min"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="minPriceDeviationBuy" name="minDeviationPercent" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="isPriceStepRandomBuy" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.randomStep"/></label>
                        </div>
                        <div class="col-md-1 col-md-offset-1 input-block-wrapper__input-wrapper pull-left">
                            <input id="isPriceStepRandomBuyInput" name="priceStepRandom" type="hidden">
                            <input id="isPriceStepRandomBuy" class="input-block-wrapper__input" type="checkbox">
                        </div>
                        <div class="col-md-3 col-md-offset-1 input-block-wrapper__label-wrapper">
                            <label for="priceStepDeviationBuy" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.stepDeviation"/></label>
                        </div>
                        <div class="col-md-4 input-block-wrapper__input-wrapper">
                            <input id="priceStepDeviationBuy" name="priceStepDeviationPercent" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>

                    <div class="input-block-wrapper">
                        <div class="col-md-2 input-block-wrapper__label-wrapper">
                            <label for="priceStepBuy" class="input-block-wrapper__label"><loc:message code="admin.autoTrading.settings.priceStep"/></label>
                        </div>
                        <div class="col-md-4 col-md-offset-1 input-block-wrapper__input-wrapper">
                            <input id="priceStepBuy" name="priceStep" class="input-block-wrapper__input admin-form-input" type="number">
                        </div>
                    </div>
                </form>



                    <button id="submitTradeSettings" class="blue-box admin-form-submit" type="submit"><loc:message code="admin.refSubmitEditCommonRoot"/></button>
            </div>
        </div>
    </div>
</div>



<span hidden id="launch-settings-loc"><loc:message code="admin.autoTrading.settings.bot.launch"/> </span>
<span hidden id="trading-settings-loc"><loc:message code="admin.autoTrading.settings.bot.trading"/></span>
<span hidden id="errorNoty">${errorNoty}</span>
<span hidden id="successNoty">${successNoty}</span>
</body>
</html>
