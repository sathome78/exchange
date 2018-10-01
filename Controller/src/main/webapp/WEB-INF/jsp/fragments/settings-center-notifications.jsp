<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: OLEG
  Date: 15.11.2016
  Time: 8:09
  To change this template use File | Settings | File Templates.
--%>
<section id="notification-options">
    <h4 class="h4_green">
        <loc:message code="notifications.settings"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">
                <c:set var="notificationOptions" value="${notificationOptionsForm.options}"/>
                <c:if test="${notificationOptions.size() > 0}">
                    <form:form method="post" action="/settings/notificationOptions/submit" modelAttribute="notificationOptionsForm">
                    <table id="notification-options-table" class="table">
                        <thead>
                        <tr>
                            <th></th>
                            <%--<th><loc:message code="notification.options.sendNotification"/></th>--%>
                            <th><loc:message code="notification.options.sendEmail"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${notificationOptions}" var="notificationOption" varStatus="optStatus">
                            <tr>
                                <td>${notificationOption.eventLocalized}</td>
                                <%--<td><form:checkbox path="options[${optStatus.index}].sendNotification" value="${notificationOption.sendNotification}"/></td>--%>
                                <td><form:checkbox path="options[${optStatus.index}].sendEmail" value="${notificationOption.sendEmail}"/></td>
                                <td hidden><form:input path="options[${optStatus.index}].event" value="${notificationOption.event}"/></td>
                            </tr>
                        </c:forEach>
                        <tr>
                            <td colspan="5"><span id="optionsError" class="red"><loc:message code="notifications.invalid" /></span> </td>
                        </tr>
                        </tbody>
                    </table>
                        <button id="submitNoitficationOptionsButton" type="submit" class="blue-box"><loc:message code="admin.submit"/></button>
                    </form:form>
                </c:if>

            </div>
        </div>

    </div>
</section>
