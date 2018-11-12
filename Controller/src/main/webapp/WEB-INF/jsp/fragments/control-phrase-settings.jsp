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
        <loc:message code="controlPhrase"/>
    </h4>
    <h4 class="under_h4_margin"></h4>
    <div class="container">
        <div class="row">
            <div class="col-sm-6 content">
                    <form:form method="post" action="/settings/controlPhrase/update" modelAttribute="controlPhrase">
                        <table id="notification-options-table" class="table">
                            <tbody>
                            <tr>
                                <td><loc:message code="controlPhrase.input"/></td>
                                <td><form:input path="phrase" id="controlPhraseInput" value="${controlPhrase.phrase}" maxlength="20"/><br>
                                    <div><loc:message code="controlPhrase.lenght.from{0}To{1}Char" arguments="1, 20"/></div>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                        <div id="control_phrase_wrong" class="field__error" style="display: none;">
                            <loc:message code="controlPhrase.error" arguments="1, 20"/>
                        </div>
                        <button id="submitPhraseButton" type="submit" class="blue-box"><loc:message code="button.update"/></button>
                    </form:form>
            </div>
        </div>

    </div>
</section>
