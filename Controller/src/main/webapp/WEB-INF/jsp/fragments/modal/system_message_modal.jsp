<%--
  Author: Vlad Dziubak
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%----------%>
<script src="<c:url value="/client/js/jquery.form.js"/>"></script>
<%----------%>

<%-----TinyMCE editor-----%>
<script src='//cdn.tinymce.com/4/tinymce.min.js'></script>
<%----------%>

<div id="alert-sys-mess-update-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="news.add"/></h4>
            </div>
            <div class="modal-body news-add-info">
                <div class="tab-content">
                    <div id="editor" class="tab-pane fade in active">
                        <form id="news-add-editor-form" class="form_full_width form_auto_height">
                            <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label for="languageVarSysMess" class="input-block-wrapper__label"><loc:message code="news.locale"/></label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <select id="languageVarSysMess" name="language"
                                        class="form-control input-block-wrapper__input">
                                    <option value="en">EN</option>
                                    <option value="ru">RU</option>
                                    <option value="cn">CN</option>
                                    <option value="in">ID</option>
                                    <option value="ar">AR</option>
                                </select>
                            </div>
                            </div>
                            <div class="input-block-wrapper">

                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label for="titleSysMess" class="input-block-wrapper__label"><loc:message code="news.newsTitle"/> </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="titleSysMess" name="title"
                                           class="form-control input-block-wrapper__input"/>
                                </div>
                            </div>

                            <div class="input-block-wrapper">
                                <textarea id="tinymce-sys-editor" name="text"></textarea>
                            </div>
                            <input id="sysMessageId" hidden name="id"/>
                        </form>

                    </div>

                </div>
            </div>
            <div class="modal-footer">
                <div class="news-add-info__button-wrapper">
                    <button id="system-alert-message-update" class="delete-order-info__button" type="submit">
                        <loc:message code="news.add"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                            ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

