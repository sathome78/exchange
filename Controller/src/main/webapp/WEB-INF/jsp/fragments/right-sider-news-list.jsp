<%--
  User: Valk
--%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--Twitter--%>
<div class="socials-icon-wrapper ">
    <a class="socials-icon-wrapper__icon" href="https://twitter.com/Exrates_Me" target="_blank">
        <img src="<c:url value='/client/img/twitter.png'/>" alt="TW"/>
    </a>

    <a class="socials-icon-wrapper__icon" data-toggle="modal" href="#qrWeChatModal">
        <img src="<c:url value='/client/img/icon_wechat.png'/>" alt="WeChat"/>
    </a>

    <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
        <a id="showAllNews" href="#"> <h4 class="h4_green"><loc:message code="news.title"/></h4></a>
    </sec:authorize>

    <sec:authorize access="<%=AdminController.nonAdminAnyAuthority%>">
        <h4 class="h4_green"><loc:message code="news.title"/></h4>
    </sec:authorize>
</div>

<%--MODAL ... --%>
<div class="modal fade" tabindex="-1" id="qrWeChatModal">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Exrates in WeChat</h4>
            </div>
            <div class="modal-body">
                <img src="<c:url value='/client/img/qr_code_wechat.jpg'/>" style="display:block; margin:0 auto;" alt="TW"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<%--... MODAL--%>


<hr class="under_h4">
<div id="news_table_wrapper">
    <div id="news-table" class="news">

        <script type="text/template" id="news_table_row">
            <div class="news_item">
                <%--<a class="news_item__ref" href="<@=ref+'newstopic'@>">--%>
                <a class="news_item__ref" target="_blank" href="https://twitter.com/Exrates_Me/status/<@=ref@>">
                    <h5 class="news_item__header"><@=title@></h5>

                    <p class="news_item__brief"><@=date@></p>
                </a>
                <sec:authorize access="<%=AdminController.adminAnyAuthority%>">
                    <div class="news__admin-section clearfix">
                        <button class="news__admin__delete-news-variant news__admin-section-button"><loc:message
                                code="news.deletevariant"/></button>
                        <button class="news__admin__add-news-variant news__admin-section-button"><loc:message
                                code="news.addvariant"/></button>
                    </div>
                </sec:authorize>
            </div>
        </script>
    </div>
</div>
<sec:authorize access="<%=AdminController.adminAnyAuthority%>">
    <button id="add-news-button" class="send_button"><loc:message code="news.addnews"/></button>
    <%--MODAL--%>
    <%@include file='modal/news_add_modal.jsp' %>
    <%@include file='modal/news_delete_modal.jsp' %>
    <%@include file='modal/news_list_modal.jsp' %>
    <%--#news-add-modal--%>
</sec:authorize>

<%@include file='modal/news_archive_modal.jsp' %>

<a id="newsArchive" href="#"> <h4 class="h4_green"><loc:message code="news.archive"/></h4></a>