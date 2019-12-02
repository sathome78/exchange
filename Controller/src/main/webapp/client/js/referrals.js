// var onPage = 20;
// var isFilterApplied = false;
//
// var mainUser ;
// var $pagination;
// var search;
// var defaultOpts = {
//     /*hideOnlyOnePage:true,*/
//     totalPages: 4,
//     first: "<<",
//     prev:"<",
//     next:">",
//     last:">>",
//     onPageClick: function (event, page) {
//         loadInfo(mainUser, page, null,'init')
//     }
// };
//
//
//
//
//
// function loadInfo(userId, page, port, action) {
//     var url = '/2a8fy7b07dxe44/findReferral?action=' + action + '&userId='+ userId
//         + "&profitUser=" + mainUser + "&onPage=" + onPage + "&page=" + page;
//     if (isFilterApplied) {
//         url = url + "&" + $('#ref_download_form').serialize();
//     }
//     $.ajax({
//         url: url,
//         type: 'GET',
//         success: function (data) {
//             append(data.referralInfoDtos, userId, port);
//             if (data.currentPage != -1) {
//                 refreshPagination(data.totalPages)
//             }
//             if (data.totalSize < 1) {
//                 $pagination.twbsPagination('destroy');
//             }
//             if (action == 'init') {
//                 appendRefBonuses(data.referralProfitDtos);
//             }
//         }
//     });
// }
//
//
// function ShowHide(userId) {
//     if(userId) {
//         var port = $(".reffil_" + userId + " ul");
//         if (port.children().length > 0 ) {
//             $(".reffil_" + userId).slideUp();
//             port.empty();
//         } else {
//             var action = 'toggle';
//             var page = -1;
//             loadInfo(userId, page, port, action);
//         }
//     }
// }
//
// function append(data, userId, port) {
//     $("#refTemplate").template("listRefs");
//     if (mainUser === userId || !port) {
//         $('.reffil_' + mainUser).html($.tmpl("listRefs", data))
//     } else {
//         port.append($.tmpl("listRefs", data));
//         $(".reffil_" + userId).slideDown();
//     }
// }
//
// function refreshPagination(totalPages) {
//     var currentPage = $pagination.twbsPagination('getCurrentPage');
//     $pagination.twbsPagination('destroy');
//     $pagination.twbsPagination($.extend({}, defaultOpts,  {
//         startPage: currentPage,
//         totalPages: totalPages,
//         initiateStartPageClick: false
//     }));
// }
//
// function searchRef(value, page, action) {
//     var formParams = $('#ref_download_form').serialize();
//     var url = '/2a8fy7b07dxe44/findReferral?action=' + action + '&profitUser=' + mainUser + '&' + formParams;
//     $.ajax({
//         url: url,
//         type: 'GET',
//         success: function (data) {
//             append(data.referralInfoDtos, mainUser, $('.reffil_' + mainUser));
//             if(data.referralInfoDtos.length > 0) {
//                 $('#level-outer').show();
//                 $('#level').text(data.currentLevel);
//             }
//             if (data.currentPage != -1) {
//                 refreshPagination(data.totalPages)
//             }
//             if (data.totalSize < 1) {
//                 $pagination.twbsPagination('destroy');
//             }
//             appendRefBonuses(data.referralProfitDtos);
//             isFilterApplied = true;
//         }
//     });
// }
//
// function downloadCsv() {
//     var url = '/2a8fy7b07dxe44/downloadRef?profitUser=' + mainUser;
//     if (isFilterApplied) {
//         url = url + "&" + $('#ref_download_form').serialize();
//     }
//     window.open(url);
// }
//
// function appendRefBonuses(data) {
//     var port = $('#refAccrualsPort');
//     port.html('');
//     $.each(data, function(i, item) {
//         port.append(item.amount + ' ' + item.currencyName).append('<br>')
//     });
// }
//
// function getCurrencies() {
//     var url = '/dashboard/getAllCurrencies';
//     $.ajax({
//         url: url,
//         type: 'GET',
//         success: function (data) {
//             addCheckboxes(data);
//             currenciesLoaded = true;
//         }
//     });
// }
//
// function addCheckboxes(data) {
//     var container = $('#currency_container');
//     $("#currencyTemplate").template("currencies");
//     container.html($.tmpl("currencies", data));
// }
//
//
// $( document ).ready(function(){
//
//    getCurrencies();
//
//     $.datetimepicker.setDateFormatter({
//         parseDate: function (date, format) {
//             var d = moment(date, format);
//             return d.isValid() ? d.toDate() : false;
//         },
//         formatDate: function (date, format) {
//             return moment(date).format(format);
//         }
//     });
//
//     $('#ref_download_start').datetimepicker({
//         format: 'YYYY-MM-DD HH:mm',
//         formatDate: 'YYYY-MM-DD',
//         formatTime: 'HH:mm',
//         onShow:function( ct ){
//             this.setOptions({
//                 maxDate:jQuery('#trans_download_end').val()?jQuery('#trans_download_end').val():false
//             })
//         },
//         lang:'ru',
//         defaultDate: new Date(),
//         defaultTime: '00:00'
//     });
//
//     $('#ref_download_end').datetimepicker({
//         format: 'YYYY-MM-DD HH:mm',
//         formatDate: 'YYYY-MM-DD',
//         formatTime: 'HH:mm',
//         onShow:function( ct ){
//             this.setOptions({
//                 minDate:jQuery('#trans_download_start').val()?jQuery('#trans_download_start').val():false
//             })
//         },
//         lang:'ru',
//         defaultDate: new Date(),
//         defaultTime: '00:00'
//     });
//     $('#ref-table-init').click(function () {
//         mainUser = $('#user-id').val();
//         $pagination = $('#pagination-demo');
//         $pagination.twbsPagination(defaultOpts);
//     });
//
//     $('#refSearchButton').on("click", function () {
//         searchId = $('#refSearch').val();
//         var action = 'search';
//         searchRef(searchId, 1, action);
//         $pagination.twbsPagination('destroy');
//     });
//
//     $('#refExtFilter').on("click", function () {
//         $('.filters').toggle("slow");
//     });
//
//     $('#refDownloadButton').on("click", function () {
//         downloadCsv();
//     });
//
//     $('#refSearchClearButton').on("click", function () {
//         isFilterApplied = false;
//         $pagination.twbsPagination('destroy');
//         loadInfo(mainUser, 1, null,'init');
//         $('#level-outer').hide();
//         $('.filter_input').val("");
//         $('.currency_check').prop('checked', true);
//     });
// });
//
//
//
