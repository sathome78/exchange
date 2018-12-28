<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: maks
  Date: 01.04.2017
  Time: 9:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section id="session-options">
    <h4 class="h4_green">
        <loc:message code="session.settings"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">
                <c:if test="${sessionSettings != null}">
                    <form:form method="post" action="/settings/sessionOptions/submit" modelAttribute="sessionSettings">
                        <table id="notification-options-table" class="table">
                            <tbody>
                                <tr>
                                    <td><loc:message code="session.time.minutes"/></td>
                                    <td><form:input id="sessionTime" onkeyup="this.value = this.value.replace (/[^0-9+]/,&quot;&quot;)"
                                                    path="sessionTimeMinutes" value="${sessionSettings.sessionTimeMinutes}"/><br>
                                        <div><loc:message code="session.time.from{0}To{1}Minutes" arguments="${sessionMinTime}, ${sessionMaxTime}"/></div>
                                    </td>
                                </tr>
                                <c:if test="${sessionLifeTimeTypes.size() > 1}">
                                    <tr>
                                        <td><loc:message code="session.AccountingOfsessionTime"/></td>
                                        <td><form:select path="sessionLifeTypeId" value="${sessionSettings.sessionTimeMinutes}">
                                            <c:forEach var="type" items="${sessionLifeTimeTypes}">
                                                <form:option value="${type.id}">
                                                    <loc:message code="session.${type.name}"/>
                                                </form:option>
                                            </c:forEach>
                                        </form:select></td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                        <button id="submitSessionOptionsButton" type="submit" class="blue-box"><loc:message code="button.update"/></button>
                    </form:form>
                </c:if>

            </div>
        </div>

    </div>
</section>
