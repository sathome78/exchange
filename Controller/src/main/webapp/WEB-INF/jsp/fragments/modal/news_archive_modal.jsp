<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="loc" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<%----------%>
<script src="<c:url value="/client/js/jquery.form.js"/>"></script>
<%----------%>

<div id="news-archive-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="news.archive"/></h4>
            </div>
            <div class="modal-body">
                <div id="archive_news_table_wrapper">
                    <table id="archive_news_table" class="balance__table">
                        <thead>
                        <tr>
                            <th class="center blue-white"><loc:message code="news.newsTitle"/></th>
                            <th class="center blue-white"><loc:message code="news.date"/></th>
                        </tr>
                        </thead>
                        <script type="text/template" id="archive_news_table_row">
                            <tr>
                                <td>
                                    <a class="news_item__ref" href="<@=ref+'newstopic'@>">
                                        <@=title@>
                                    </a>
                                </td>
                                <td>
                                    <@=date@>
                                </td>
                            </tr>
                        </script>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
