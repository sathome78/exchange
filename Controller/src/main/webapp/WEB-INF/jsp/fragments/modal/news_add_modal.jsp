<%--
  Created by IntelliJ IDEA.
  User: Valk
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%----------%>
<script src="<c:url value="/client/js/jquery.form.js"/>"></script>
<%----------%>

<div id="news-add-modal" class="modal fade modal-form-dialog" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title"><loc:message code="news.add"/></h4>
            </div>
            <div class="modal-body news-add-info">
                <ul class="nav nav-pills">
                    <li class="active"><a data-toggle="pill" href="#editor"><loc:message code="news.createInEditor"/></a></li>
                    <li><a data-toggle="pill" href="#archive"><loc:message code="news.uploadArchive"/></a></li>
                </ul>
                <div class="tab-content">
                    <div id="editor" class="tab-pane fade in active">
                        <form id="imageUploadForm" action="/news/uploadImage" method="post" enctype="multipart/form-data">
                            <input name="file" id="imageUpload" type="file" />
                        </form>
                        <div id="clearFormDialog">
                            <loc:message code="news.clearform.prompt"/>
                        </div>

                        <form id="news-add-editor-form" class="form_full_width form_auto_height">
                            <div class="input-block-wrapper">
                            <div class="col-md-3 input-block-wrapper__label-wrapper">
                                <label for="variantEd" class="input-block-wrapper__label"><loc:message code="news.locale"/></label>
                            </div>
                            <div class="col-md-9 input-block-wrapper__input-wrapper">
                                <select id="variantEd" name="newsVariant"
                                        class="form-control input-block-wrapper__input">
                                    <option value="en">EN</option>
                                    <option value="ru">RU</option>
                                    <option value="cn">CN</option>
                                    <option value="in">ID</option>
                                    <option value="ar">AR</option>
                                    <option value="ko">KO</option>
                                </select>
                            </div>
                            </div>
                            <div class="input-block-wrapper">

                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label for="titleEd" class="input-block-wrapper__label"><loc:message code="news.newsTitle"/> </label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <input id="titleEd" name="title"
                                           class="form-control input-block-wrapper__input"/>
                                </div>
                            </div>
                            <div class="input-block-wrapper">
                                <div class="col-md-3 input-block-wrapper__label-wrapper">
                                    <label for="briefEd" class="input-block-wrapper__label"><loc:message code="news.brief"/></label>
                                </div>
                                <div class="col-md-9 input-block-wrapper__input-wrapper">
                                    <textarea id="briefEd" name="brief"
                                           class="form-control input-block-wrapper__input"></textarea>
                                </div>
                            </div>
                            <div class="input-block-wrapper">
                                <textarea id="tinymce"></textarea>
                            </div>
                            <input id="newsIdEd" hidden name="id"/>
                            <input id="resourceEd" hidden name="resource"/>
                            <input id="dateEd" hidden name="date"/>
                        </form>


                    </div>

                    <div id="archive" class="tab-pane fade">
                        <form id="news-add-info__form" action="" method="post"
                              accept="application/zip,application/x-zip,application/x-zip-compressed">
                            <div class="input-block-wrapper news-add-info__date">
                                <div class="col-md-5 input-block-wrapper__label-wrapper">
                                    <label class="input-block-wrapper__label"><loc:message code="news.date"/></label>
                                </div>
                                <div class="col-md-7 input-block-wrapper__input-wrapper">
                                    <input id="newsDate" name="date"
                                           placeholder='<loc:message code="news.datetimeplaceholder"/>'
                                           autocomplete="off"
                                           class="form-control input-block-wrapper__input"/>
                                </div>
                                <div for="newsDate" hidden class="col-md-7 input-block-wrapper__error-wrapper">
                                    <label for="newsDate" class="input-block-wrapper__input"><loc:message
                                            code="news.errordatetime"/></label>
                                </div>
                            </div>
                            <br/>
                            <br/>
                            <input id="uploadFile" class="file-choice__input" required type="file" name="file"/>
                            <input id="newsId" hidden name="id"/>
                            <input id="resource" hidden name="resource"/>
                        </form>
                    </div>

                </div>
            </div>
            <div class="modal-footer">
                <div class="news-add-info__button-wrapper">
                    <button id="news-add-info__add-news" class="delete-order-info__button">
                        <loc:message code="news.add"/></button>
                    <button class="delete-order-info__button" data-dismiss="modal"
                            ><loc:message
                            code="submitorder.cancell"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

