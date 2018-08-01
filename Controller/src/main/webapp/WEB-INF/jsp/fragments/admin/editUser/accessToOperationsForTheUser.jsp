<%--
  User: Vlad Dziubak
  Date: 30.07.18 | Time: 15:04
--%>
<c:set var="adminWithAccess" value="${false}"/>

<sec:authorize access="hasAuthority('${admin_editUser}')">
    <c:set var="adminWithAccess" value="${true}"/>
</sec:authorize>

<div id="panel8" class="tab-pane">
    <div class="col-md-6 content">
        <div class="text-center"><h4><loc:message code="userOperation.title"/></h4></div>
        <hr/>
                <div>
                    <form:form method="post" action="/2a8fy7b07dxe44/editUserOperationTypeAuthorities/submit"
                               modelAttribute="userOperationAuthorityOptionsForm">

                        <table id="user-authoritiesTable" class="table table-striped table-bordered">
                            <tbody>
                            <c:forEach items="${userOperationAuthorityOptionsForm.options}" var="operationId"
                                       varStatus="authStat">

                                    <c:choose>
                                        <c:when test="${adminWithAccess}">
                                            <tr>
                                        </c:when>
                                        <c:otherwise>
                                            <tr class="active">
                                        </c:otherwise>
                                    </c:choose>

                                    <td>${operationId.userOperationAuthorityLocalized}</td>

                                        <c:choose>
                                            <c:when test="${adminWithAccess}">
                                                <td><form:checkbox
                                                        path="options[${authStat.index}].enabled"
                                                        value="${operationId.enabled}"/></td>
                                            </c:when>
                                            <c:otherwise>
                                                <td><form:checkbox
                                                        path="options[${authStat.index}].enabled"
                                                        value="${operationId.enabled}" onclick="return false"/></td>
                                            </c:otherwise>
                                        </c:choose>

                                    <form:hidden
                                            path="options[${authStat.index}].userOperationAuthority"/>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <form:hidden path="userId"/>

                        <c:choose>
                            <c:when test="${adminWithAccess}">
                                <button type="submit" class="blue-box"><loc:message code="admin.submit"/></button>
                            </c:when>
                            <c:otherwise>
                                <button id="userOperationSubmitButton" type="submit" class="blue-box" disabled><loc:message code="admin.submit"/></button>
                            </c:otherwise>
                        </c:choose>

                    </form:form>
                </div>
    </div>
</div>