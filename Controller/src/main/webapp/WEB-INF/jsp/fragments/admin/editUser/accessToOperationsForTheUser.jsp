<%--
  User: Vlad Dziubak
  Date: 30.07.18 | Time: 15:04
--%>
<div id="panel8" class="tab-pane">
    <div class="col-md-6 content">
        <div class="text-center"><h4><loc:message code="admin.accessRights"/></h4></div>
        <hr/>
        <div>
            <form:form method="post" action="/2a8fy7b07dxe44/editUserOperationTypeAuthorities/submit"
                       modelAttribute="userOperationAuthorityOptionsForm">
                <table id="user-authoritiesTable" class="table table-striped table-bordered">
                    <tbody>
                    <c:forEach items="${userOperationAuthorityOptionsForm.options}" var="operationId"
                               varStatus="authStat">
                        <tr>
                            <td>${operationId.userOperationAuthorityLocalized}</td>
                            <td><form:checkbox
                                    path="options[${authStat.index}].enabled"
                                    value="${operationId.enabled}"/></td>
                            <form:hidden
                                    path="options[${authStat.index}].userOperationAuthority"/>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <form:hidden path="userId"/>
                <button type="submit" class="blue-box"><loc:message code="admin.submit"/></button>
            </form:form>
        </div>
    </div>
</div>
