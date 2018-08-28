function approveKyc(userId) {
    console.log('approveKyc...');
    $.ajax('/2a8fy7b07dxe44/kyc/approveKyc?userId=' + userId, {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        method: 'PUT'
    }).done(function (data) {
        successNoty(data.message);
        $('#kyc__status').html('<h4>' + data.info.kycStatus + '</h4>');
        $('#status__admin').html('<h5>by admin:</h5> <h4>' + data.info.admin + '</h4>');
    }).fail(function(e){
        errorNoty(data);
    })
}

function rejectKyc(userId) {
    console.log('rejectKyc...');
    $.ajax('/2a8fy7b07dxe44/kyc/rejectKyc?userId=' + userId, {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        method: 'PUT'
    }).done(function (data) {
        errorNoty(data.message);
        $('#kyc__status').html('<h4>' + data.info.kycStatus + '</h4>');
        $('#status__admin').html('<h5>by admin:</h5> <h4>' + data.info.admin + '</h4>');
    }).fail(function(e){
        errorNoty(data);
    })
}

function approveWordCheck(userId) {
    console.log('approveWordCheck...');
    $.ajax('/2a8fy7b07dxe44/kyc/approveWorldCheck?userId=' + userId, {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        method: 'PUT'
    }).done(function (data) {
        successNoty(data.message);
        $('#wc__status').html('<h4>' + data.info.status + '</h4>');
        $('#wc__status__admin').html('<h5>by admin:</h5> <h4>' + data.info.admin + '</h4>');
    }).fail(function(e){
        errorNoty(data);
    })
}

function rejectWordCheck(userId) {
    console.log('rejectWordCheck...');
    $.ajax('/2a8fy7b07dxe44/kyc/rejectWorldCheck?userId=' + userId, {
        headers: {
            'X-CSRF-Token': $("input[name='_csrf']").val()
        },
        method: 'PUT'
    }).done(function (data) {
        errorNoty(data.message);
        $('#wc__status').html('<h4>' + data.info.status + '</h4>');
        $('#wc__status__admin').html('<h5>by admin:</h5> <h4>' + data.info.admin + '</h4>');
    }).fail(function(e){
        errorNoty(data);
    })
}

