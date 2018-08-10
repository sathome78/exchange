<%@ taglib prefix="loc" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="C" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: maks
  Date: 01.04.2017
  Time: 9:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section id="api-options">
    <h4 class="h4_green">
        <loc:message code="api.user.settings"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-md-8 col-md-offset-2 content">

                <div id="tokens-table-wrapper">
                    <table class="table table-bordered" id="api-tokens-table" style="width:100%">
                        <thead>
                        <tr>
                            <th><loc:message code="api.user.settings.token.alias"/></th>
                            <th><loc:message code="api.user.settings.publicKey"/></th>
                            <th><loc:message code="api.user.settings.token.perms.trade"/></th>
                            <th><loc:message code="api.user.settings.token.date"/></th>
                            <th></th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <h4 class="h4_green">
        <loc:message code="api.user.settings.generate"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row" id="generate-form-wrapper">
            <form class="form-inline" action="/settings/token/create" method="post">
                <div class="form-group">
                    <label style="font-size: 1.5rem" for="alias-input"><loc:message code="api.user.settings.token.alias"/></label>
                    <input name="alias" style="width: 300px; margin: 5px 20px" class="form-control" id="alias-input" type="text">
                </div>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button id="generate-token-button" class="blue-box">
                    <loc:message code="api.user.settings.generate.btn"/></button>
            </form>
        </div>
    </div>
    <span hidden id="prompt-perm-loc"><loc:message code="api.user.settings.token.perms.prompt"/> </span>
    <span hidden id="prompt-del-loc"><loc:message code="api.user.settings.token.delete.prompt"/> </span>

</section>
