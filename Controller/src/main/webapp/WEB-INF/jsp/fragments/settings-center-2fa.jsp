<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section id="2fa-options">
    <h4 class="h4_green">
        <loc:message code="message.2fa.title"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">

                    <form method="post" action="/settings/2FaOptions/submit" id="2faSettings_form">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <table id="2fa-options-table" class="table">
                            <thead>
                            <tr>
                                <th></th>
                                <c:set var = "subscriptions" value = "${user2faOptions.get('subscriptions')}"/>
                                <c:forEach items="${user2faOptions.get('notificators')}" var="notificatorHead">
                                    <th>
                                        <c:if test="${notificatorHead.needSubscribe}">
                                            <c:choose>
                                                <c:when test="${notificatorHead.enabled && subscriptions.get(notificatorHead.id) == null
                                                or not subscriptions.get(notificatorHead.id).isConnected()}">
                                                    <a class="btn btn-default" id="subscribe_${notificatorHead.name}">
                                                    <loc:message code="notificator.conect"/></a>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:if test="${subscriptions.get(notificatorHead.id).getContactStr() != null}">
                                                        <a class="btn btn-default contact_info" data-id="${notificatorHead.id}"
                                                           data-contact="${subscriptions.get(notificatorHead.id).getContactStr()}">
                                                        <loc:message code="message.info"/></a>
                                                    </c:if>
                                                    <c:if test="${notificatorHead.enabled}">
                                                    <a class="btn btn-default" id="reconnect_${notificatorHead.name}">
                                                        <loc:message code="notificator.reconnect"/></a>
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if><br>${notificatorHead.name}<br>
                                        <c:if test="${!notificatorHead.enabled}"><loc:message code="news.status.disabled"/></c:if>
                                    </th>
                                </c:forEach>
                                <th>Disable</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:set var = "settings" value = "${user2faOptions.get('settings')}"/>
                            <c:forEach items="${user2faOptions.get('events')}" var="event">
                                    <tr>
                                        <td><loc:message code="settings.message.event.${event}"/></td>
                                        <c:forEach items="${user2faOptions.get('notificators')}" var="notificator">
                                            <td><input type="radio" name="${event.code}" value="${notificator.id}"
                                                    <c:if test="${notificator.needSubscribe and (subscriptions.get(notificator.id) == null
                                                    or not subscriptions.get(notificator.id).isConnected())}">
                                                        disabled
                                                    </c:if>
                                                       <c:if test="${!notificator.enabled}">disabled</c:if>
                                                    <c:if test="${settings.get(event.code) != null
                                                    and settings.get(event.code).notificatorId == notificator.id}">CHECKED</c:if>>
                                            </td>
                                        </c:forEach>
                                            <td><input type="radio" name="${event.code}" value="0"
                                                    <c:if test="${not event.canBeDisabled}">disabled</c:if>
                                                    <c:if test="${(settings.get(event.code) == null or settings.get(event.code).notificatorId == null)
                                                        and event.canBeDisabled}"> CHECKED</c:if>
                                                />
                                                            </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <button id="submitSessionOptionsButton" type="submit" class="blue-box">
                            <loc:message code="button.update"/></button>
                    </form>
            </div>
        </div>
    </div>
</section>
