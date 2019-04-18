###
@api {get} /api/private/v2/ieo/:name Get info for ieo token NOT IMPLEMENT YET
@apiName Get info for ieo token NOT IMPLEMENT YET
@apiVersion 0.0.1
@apiGroup IEO
@apiUse Exrates

@apiExample {curl} Example usage:
curl -X GET \
  http://dev1.exrates.tech/api/private/v2/ieo/btt \
  -H 'exrates-rest-token: $token'

@apiSuccessExample {json} Success-Response:
{
    "data": "SUCCESS",
    "error": null
}
###

###
@api {post} /api/private/v2/kyc/claim Add claim for buy tokens
@apiName  Add claim for buy tokens
@apiVersion 0.0.1
@apiGroup IEO
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
curl -X POST \
  http://dev1.exrates.tech/api/private/v2/kyc/claim \
  -H 'Content-Type: application/json' \
  -H 'exrates-rest-token: $token' \
  -d '{
          "currencyName": "btt",
          "amount": "10000"
}'

@apiSuccess {Object} data Data
@apiSuccess {Number} data.id id of claim
@apiSuccess {String} data.currencyName currency name
@apiSuccess {Number} data.amount amount

@apiSuccessExample {json} Success-Response:
{
    "data": {
        "id": 1,
        "currencyName": "btt",
        "amount": 10000
    },
    "error": null
}

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 OK
{
    "url": "url",
    "cause": "cause",
    "detail": "detail",
    "title": "title",
    "uuid": "uuid",
    "code": 1200
}
###

###
@api {put} /api/private/v2/dashboard/policy/:name Set state of policy for ieo
@apiName Set state of policy for ieo
@apiVersion 0.0.1
@apiGroup IEO
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
curl -X PUT \
  http://dev1.exrates.tech/api/private/v2/dashboard/policy/ieo \
  -H 'exrates-rest-token: $token'

@apiSuccessExample {json} Success-Response:
{
    "data": true,
    "error": null
}
###

###
@api {get} /api/private/v2/ieo/check/:idIEO Get state of conditions for user by id IEO
@apiName Get state of conditions for user by id IEO
@apiVersion 0.0.1
@apiGroup IEO
@apiUse Exrates

@apiExample {curl} Example usage:
curl -X GET \
  http://dev1.exrates.tech/api/private/v2/ieo/check/btt \
  -H 'exrates-rest-token: $token'

@apiSuccess {Object} data Data
@apiSuccess {Boolean} data.kycCheck kyc status, if true - allow user continue
@apiSuccess {Boolean} data.policyCheck policy status, if true - allow user continue
@apiSuccess {Boolean} data.countryCheck country status, if true - allow user continue
@apiSuccess {Object} data.country if countryCheck=false, country = null
@apiSuccess {String} data.country.countryName name country, example - Ukraine
@apiSuccess {String} data.country.countryCode code country, example - UKR

@apiSuccessExample {json} Success-Response:
{
    "data": {
        "kycCheck": true,
        "policyCheck": true,
        "countryCheck": true,
        "country": {
            "countryName": "Ukraine",
            "countryCode": "UKR",
          }
    },
    "error": null
}

###

