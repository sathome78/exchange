jQuery(document).ready(function($) {

    $(function () {
        if ($('#kyc_type').val() && $('#kyc_type').val() === 'LEGAL_ENTITY' ) {
            $('#entity__box').prop('checked',true);
            $("#tab__entity").addClass('active');
            $('.kyc__type').val('LEGAL_ENTITY');
        } else {
            $('#individual__box').prop('checked',true);
            $("#tab__individual").addClass('active');
            $('.kyc__type').val('INDIVIDUAL');
        }
    });

    $(":radio[name='kyc__tab__change']").change(function () {
        var selection = $(this).val();
        if (selection == "individual") {
            $('#tab__individual').siblings().removeClass('active');
            $('#tab__individual').addClass('active');
            $('.kyc__type').val('INDIVIDUAL');
        } else {
            $('#tab__entity').siblings().removeClass('active');
            $('#tab__entity').addClass('active');
            $('.kyc__type').val('LEGAL_ENTITY');
        }
    });



    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    $('#date_of_birth').datetimepicker({
        format: 'YYYY-MM-DD HH:mm',
        formatDate: 'YYYY-MM-DD',
        formatTime: 'HH:mm',
        onShow: function (ct) {
            this.setOptions({
            })
        },
        lang: 'ru',
        defaultDate: new Date(),
        defaultTime: '00:00'
    });

});