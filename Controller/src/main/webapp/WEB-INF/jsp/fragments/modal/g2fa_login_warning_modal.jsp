<div id="first_info_modal" class="modal fade order-info__modal modal-form-dialog in" tabindex="-1" role="dialog">
    <div class="modal-dialog" style="width: 900px;">
        <div class="modal-content">
            <div class="modal-body">
                <h5 style="font-weight: bold; margin: 0 0 12px; font-size: 24px;"><loc:message code="message.modal.title.risks"/> </h5>
                <style>
                    .custom-inp-check{
                        position: absolute;
                        width: 100%;
                        height: 100%;
                        opacity: 0;
                        margin: 0;
                        z-index: 1;
                    }
                    .custom-check{
                        position: relative;
                        z-index: 2;
                        width: 18px;
                        height: 18px;
                        border: 1px solid #bcbcbc;
                        border-radius: 4px;
                        background-color: rgba(236, 236, 236, 0.3);
                    }
                    .custom-check:hover{
                        cursor: pointer;
                    }
                    .custom-inp-check:checked + .custom-check:before{
                        content:"" ;
                        display: block;
                        position: absolute;
                        left: 2px;
                        top: 4px;
                        width: 14px;
                        height: 10px;
                        background-image: url('/client/img/accept-green.svg');
                        -webkit-background-size: 93%;
                        background-size: 93%;
                        background-repeat: no-repeat;
                        background-position: center;
                    }
                </style>
                <!-- row start -->
                <div style="padding: 12px 0;display: flex; justify-content: space-between; border-bottom: 1px solid #ececec;">
                    <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center; font-size: 16px;">
                        <div class="" style="margin-right: 20px;">
                            <svg width="64" height="60" viewBox="0 0 52 60" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M48.3957 40.0676C46.783 44.4383 44.344 48.2387 41.1456 51.362C37.505 54.917 32.7379 57.7418 26.9765 59.7569C26.7874 59.8228 26.5901 59.8769 26.3919 59.9167C26.1301 59.9684 25.8636 59.9963 25.5995 60H25.5478C25.2663 60 24.9834 59.9716 24.7028 59.9167C24.5045 59.8769 24.31 59.8228 24.1219 59.7583C18.3536 57.7464 13.5805 54.9229 9.93621 51.3679C6.73644 48.2446 4.29793 44.4466 2.68752 40.0758C-0.2408 32.129 -0.0741743 23.3748 0.0599505 16.3398L0.0622393 16.2318C0.0892474 15.6509 0.106642 15.0407 0.116255 14.3669C0.165236 11.0586 2.79555 8.32166 6.10427 8.13718C13.0028 7.7522 18.3394 5.50229 22.8992 1.05786L22.939 1.02124C23.6961 0.326811 24.6501 -0.013765 25.5995 0.000425731C26.515 0.0123276 27.4269 0.352446 28.157 1.02124L28.196 1.05786C32.7567 5.50229 38.0933 7.7522 44.9918 8.13718C48.3005 8.32166 50.9308 11.0586 50.9798 14.3669C50.9894 15.0453 51.0068 15.6546 51.0338 16.2318L51.0352 16.2776C51.1688 23.3258 51.3345 32.0975 48.3957 40.0676Z" fill="#00B43D"></path>
                                <path d="M48.3958 40.0672C46.7831 44.4379 44.3441 48.2382 41.1457 51.3616C37.5051 54.9166 32.738 57.7414 26.9766 59.7565C26.7875 59.8224 26.5902 59.8764 26.392 59.9163C26.1302 59.968 25.8637 59.9959 25.5996 59.9996V0C26.5151 0.0119019 27.427 0.35202 28.1571 1.02081L28.196 1.05743C32.7567 5.50186 38.0934 7.75177 44.9919 8.13675C48.3006 8.32123 50.9309 11.0582 50.9799 14.3665C50.9895 15.0449 51.0069 15.6542 51.0339 16.2314L51.0353 16.2772C51.1689 23.3254 51.3346 32.097 48.3958 40.0672Z" fill="#6AC489"></path>
                                <path d="M40.4999 30.0002C40.4999 38.2276 33.8197 44.9247 25.5992 44.954H25.5465C17.3018 44.954 10.5928 38.2454 10.5928 30.0002C10.5928 21.7554 17.3018 15.0469 25.5465 15.0469H25.5992C33.8197 15.0762 40.4999 21.7733 40.4999 30.0002Z" fill="white"></path>
                                <path d="M32.3338 27.8471L25.5992 34.5827L24.144 36.0379C23.8002 36.3817 23.3493 36.5533 22.8989 36.5533C22.448 36.5533 21.9975 36.3817 21.6533 36.0379L18.5245 32.9077C17.8369 32.2202 17.8369 31.1064 18.5245 30.4184C19.2111 29.7308 20.3262 29.7308 21.0138 30.4184L22.8989 32.3035L29.8445 25.3578C30.5321 24.6698 31.6472 24.6698 32.3338 25.3578C33.0214 26.0454 33.0214 27.1605 32.3338 27.8471Z" fill="#00B43D"></path>
                            </svg>
                        </div>
                        <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center;">
                            <loc:message code="message.modal.make.sure"/>
                            <span style=" position: relative; height: 28px; padding: 0 20px 0 36px; margin: 0 18px; font-size: 12px; display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center; border: 1px solid #ececec; border-radius: 4px; background-color: rgba(236, 236, 236, 0.3);">
                  <span style="position: absolute; left: 0; width: 28px; height: 28px; border-radius: 4px; background-color: #00B43D; display: -webkit-flex;display: -ms-flex;display: flex; justify-content: center; -ms-align-items: center;align-items: center;">
                    <svg width="15" height="13" viewBox="0 0 15 13" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M14.325 3.84713L7.59041 10.5827L6.13518 12.0379C5.7914 12.3817 5.3405 12.5533 4.89006 12.5533C4.43917 12.5533 3.98873 12.3817 3.64449 12.0379L0.515671 8.90771C-0.17189 8.22015 -0.17189 7.10641 0.515671 6.41839C1.20232 5.73083 2.31743 5.73083 3.00499 6.41839L4.89006 8.30346L11.8357 1.35781C12.5233 0.669792 13.6384 0.669792 14.325 1.35781C15.0126 2.04537 15.0126 3.16049 14.325 3.84713Z" fill="white"></path>
                    </svg>
                  </span>
                  www.exrates.me
                </span>
                            <loc:message code="message.modal.prevent.phishing"/>
                        </div>
                    </div>
                    <label style="position: relative; -ms-align-self: center;align-self: center;">
                        <input class="custom-inp-check" type="checkbox" name="2fa" value="">
                        <div style="" class="custom-check"></div>
                    </label>
                </div>
                <!-- row end -->
                <!-- row start -->
                <div style="padding: 12px 0;display: flex; justify-content: space-between; border-bottom: 1px solid #ececec;">
                    <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center; font-size: 16px;">
                        <div class="" style="margin-right: 20px;">
                            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <rect x="2" y="2" width="60" height="60" rx="4" fill="#ECECEC" fill-opacity="0.3" stroke="#EB5757" stroke-width="4"></rect>
                                <mask id="mask0" mask-type="alpha" maskUnits="userSpaceOnUse" x="2" y="2" width="60" height="60">
                                    <rect x="2.5" y="2.5" width="59" height="59" rx="3.5" fill="#ECECEC" stroke="#EB5757"></rect>
                                </mask>
                                <g mask="url(#mask0)">
                                    <path d="M49 51V39.4773C47.5762 39.4773 46.2107 38.917 45.204 37.9196C44.1972 36.9223 43.6316 35.5696 43.6316 34.1591C43.6316 32.7486 44.1972 31.3959 45.204 30.3986C46.2107 29.4012 47.5762 28.8409 49 28.8409V17.3182H37.3684C37.3684 15.9077 36.8028 14.555 35.796 13.5577C34.7893 12.5603 33.4238 12 32 12C30.5762 12 29.2107 12.5603 28.204 13.5577C27.1972 14.555 26.6316 15.9077 26.6316 17.3182H15V51H49Z" fill="#A0A0A0"></path>
                                    <rect x="-4.70312" y="43.3809" width="73.7525" height="5" transform="rotate(-20 -4.70312 43.3809)" fill="#EB5757"></rect>
                                </g>
                            </svg>
                        </div>
                        <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center;">
                            <loc:message code="message.modal.never.install"/>
                        </div>
                    </div>
                    <label style="position: relative; -ms-align-self: center;align-self: center;">
                        <input class="custom-inp-check" type="checkbox" name="2fa" value="">
                        <div style="" class="custom-check"></div>
                    </label>
                </div>
                <!-- row end -->
                <!-- row start -->
                <div style="padding: 12px 0;display: flex; justify-content: space-between; border-bottom: 1px solid #ececec;">
                    <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center; font-size: 16px;">
                        <div class="" style="margin-right: 20px;">
                            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <rect x="2" y="2" width="60" height="60" rx="4" fill="#ECECEC" fill-opacity="0.3" stroke="#EB5757" stroke-width="4"></rect>
                                <mask id="mask0" mask-type="alpha" maskUnits="userSpaceOnUse" x="2" y="2" width="60" height="60">
                                    <rect x="2.5" y="2.5" width="59" height="59" rx="3.5" fill="white" stroke="#EB5757"></rect>
                                </mask>
                                <g mask="url(#mask0)">
                                    <path d="M40.8493 37.4925C39.7385 36.3959 38.3517 36.3959 37.248 37.4925C36.4061 38.3274 35.5641 39.1623 34.7363 40.0113C34.5099 40.2448 34.3189 40.2943 34.0429 40.1387C33.4981 39.8415 32.918 39.601 32.3944 39.2755C29.9534 37.7402 27.9087 35.7662 26.0974 33.5445C25.1989 32.4408 24.3994 31.2592 23.8404 29.9291C23.7272 29.6602 23.7484 29.4833 23.9678 29.264C24.8097 28.4504 25.6304 27.6155 26.4582 26.7806C27.6115 25.6203 27.6115 24.2618 26.4512 23.0944C25.7932 22.4293 25.1352 21.7784 24.4772 21.1133C23.798 20.4341 23.1258 19.7478 22.4395 19.0757C21.3287 17.9931 19.9419 17.9931 18.8382 19.0827C17.9892 19.9176 17.1755 20.7737 16.3123 21.5944C15.5128 22.3515 15.1095 23.2784 15.0246 24.3609C14.8902 26.1226 15.3218 27.7853 15.9303 29.4055C17.1755 32.7592 19.0717 35.7379 21.3711 38.4689C24.4772 42.1622 28.1846 45.0843 32.5217 47.1927C34.4745 48.1408 36.498 48.8695 38.6984 48.9898C40.2125 49.0747 41.5285 48.6927 42.5827 47.5111C43.3044 46.7045 44.1181 45.9687 44.8822 45.1975C46.0142 44.0513 46.0213 42.6645 44.8964 41.5325C43.5521 40.1811 42.2007 38.8368 40.8493 37.4925Z" fill="#A0A0A0"></path>
                                    <path d="M39.4977 31.853L42.1085 31.4073C41.6981 29.0087 40.5661 26.8366 38.8468 25.1103C37.0284 23.2919 34.729 22.1458 32.196 21.792L31.8281 24.4169C33.788 24.6928 35.5709 25.5773 36.9789 26.9852C38.3091 28.3154 39.1793 29.9993 39.4977 31.853Z" fill="#A0A0A0"></path>
                                    <path d="M43.5801 20.5045C40.5661 17.4905 36.7525 15.5872 32.5427 15L32.1748 17.6249C35.8115 18.1343 39.1086 19.7829 41.7122 22.3795C44.1815 24.8488 45.8017 27.9689 46.389 31.4004L48.9998 30.9547C48.3135 26.9784 46.4385 23.37 43.5801 20.5045Z" fill="#A0A0A0"></path>
                                    <rect x="-4.70312" y="43.3809" width="73.7525" height="5" transform="rotate(-20 -4.70312 43.3809)" fill="#EB5757"></rect>
                                </g>
                            </svg>

                        </div>
                        <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center;">
                            <loc:message code="message.modal.never.call"/>
                        </div>
                    </div>
                    <label style="position: relative; -ms-align-self: center;align-self: center;">
                        <input class="custom-inp-check" type="checkbox" name="2fa" value="">
                        <div style="" class="custom-check"></div>
                    </label>
                </div>
                <!-- row end -->
                <!-- row start -->
                <div style="padding: 12px 0;display: flex; justify-content: space-between; border-bottom: 1px solid #ececec;">
                    <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center; font-size: 16px;">
                        <div class="" style="margin-right: 20px;">
                            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <rect x="2" y="2" width="60" height="60" rx="4" fill="#ECECEC" fill-opacity="0.3" stroke="#EB5757" stroke-width="4"></rect>
                                <mask id="mask0" mask-type="alpha" maskUnits="userSpaceOnUse" x="2" y="2" width="60" height="60">
                                    <rect x="2.5" y="2.5" width="59" height="59" rx="3.5" fill="white" stroke="#EB5757"></rect>
                                </mask>
                                <g mask="url(#mask0)">
                                    <path d="M45.1411 31.1307C44.6902 30.6799 44.1431 30.4547 43.4989 30.4547H42.7266V25.8181C42.7266 22.856 41.6644 20.3127 39.5393 18.1876C37.4144 16.0625 34.8713 15 31.9088 15C28.9464 15 26.4028 16.0625 24.278 18.1876C22.1529 20.3127 21.0907 22.8559 21.0907 25.8181V30.4547H20.3181C19.6744 30.4547 19.1269 30.6799 18.6761 31.1307C18.2253 31.5812 18 32.1286 18 32.773V46.6819C18 47.3256 18.2254 47.8733 18.6761 48.3241C19.1269 48.7745 19.6744 49 20.3181 49H43.4995C44.1437 49 44.6907 48.7748 45.1417 48.3241C45.5921 47.8733 45.8178 47.3256 45.8178 46.6819V32.7727C45.8181 32.1289 45.5921 31.5815 45.1411 31.1307ZM38.0905 30.4547H25.7271V25.8181C25.7271 24.1117 26.3309 22.6547 27.5382 21.4475C28.7457 20.2402 30.2025 19.6366 31.909 19.6366C33.6157 19.6366 35.0721 20.2401 36.2797 21.4475C37.4867 22.6546 38.0905 24.1117 38.0905 25.8181V30.4547Z" fill="#A0A0A0"></path>
                                    <rect x="-4.70312" y="43.3809" width="73.7525" height="5" transform="rotate(-20 -4.70312 43.3809)" fill="#EB5757"></rect>
                                </g>
                            </svg>
                        </div>
                        <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center;">
                            <loc:message code="message.modal.never.tell"/>
                        </div>
                    </div>
                    <label style="position: relative; -ms-align-self: center;align-self: center;">
                        <input class="custom-inp-check" type="checkbox" name="2fa" value="">
                        <div style="" class="custom-check"></div>
                    </label>
                </div>
                <!-- row end -->
                <!-- row start -->
                <div style="padding: 12px 0;display: flex; justify-content: space-between; border-bottom: 1px solid #ececec;">
                    <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center; font-size: 16px;">
                        <div class="" style="margin-right: 20px;">
                            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <rect x="2" y="2" width="60" height="60" rx="4" fill="#ECECEC" fill-opacity="0.3" stroke="#EB5757" stroke-width="4"></rect>
                                <mask id="mask0" mask-type="alpha" maskUnits="userSpaceOnUse" x="2" y="2" width="60" height="60">
                                    <rect x="2.5" y="2.5" width="59" height="59" rx="3.5" fill="white" stroke="#EB5757"></rect>
                                </mask>
                                <g mask="url(#mask0)">
                                    <path d="M39.5156 33.8524V38.6137C39.5173 39.9956 40.6374 41.1157 42.0193 41.1174H48.7178V31.3486H42.0193C40.6374 31.3503 39.5173 32.4704 39.5156 33.8524Z" fill="#A0A0A0"></path>
                                    <path d="M40.4846 17.5037C40.4846 16.6906 40.0895 15.9281 39.4257 15.459C38.7617 14.9899 37.9111 14.8722 37.1447 15.1437L19.7334 21.3111H40.4846V17.5037Z" fill="#A0A0A0"></path>
                                    <path d="M46.2138 22.9805H16V46.4962C16.0013 47.8784 17.1215 48.9983 18.5037 48.9999H46.2138C47.5961 48.9983 48.7159 47.8784 48.7175 46.4962V42.7859H42.0191C39.7156 42.7833 37.8488 40.9166 37.8462 38.6131V33.8518C37.8488 31.5482 39.7156 29.6815 42.0191 29.6789H48.7175V25.4842C48.7159 24.1023 47.5961 22.9821 46.2138 22.9805Z" fill="#A0A0A0"></path>
                                    <rect x="-4.70312" y="43.3809" width="73.7525" height="5" transform="rotate(-20 -4.70312 43.3809)" fill="#EB5757"></rect>
                                </g>
                            </svg>
                        </div>
                        <div style="display: -webkit-flex;display: -ms-flex;display: flex; -ms-align-items: center;align-items: center;">
                            <loc:message code="message.modal.never.send"/>
                        </div>
                    </div>
                    <label style="position: relative; -ms-align-self: center;align-self: center;">
                        <input class="custom-inp-check" type="checkbox" name="2fa" value="">
                        <div style="" class="custom-check"></div>
                    </label>
                </div>
                <!-- row end -->
                <div style="text-align: center; font-size: 14px; margin: 16px 0;"><loc:message code="message.modal.besure"/></div>
                <div style="text-align: center;"><button class="safety_agree_button btn btn-default" disabled=""><loc:message code="message.modal.understand"/> &gt;&gt;</button></div>

            </div>
        </div>
    </div>
</div>