###
@api {post} /api/private/v2/merchants/qubera/account/create Create qubera account
@apiName Create qubera account
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
      http://localhost:8080/api/private/v2/merchants/qubera/account/create \
      -H 'Content-Type: application/json' \
      -H 'exrates-rest-token: $token' \
      -d '{
	    "firstName":"firstName",
	    "lastName":"lastName",
	    "address":"Flat 6, 28 street",
	    "countryCode":"UK",
	    "city":"London"
}'

@apiParam {String} firstName - first name
@apiParam {String} lastName - last name
@apiParam {String} city - city
@apiParam {String} address - Address line (without country name/code, postal code, city), example: Flat 6, 28 street
@apiParam {String} countryCode - country code

@apiSuccess {Object} data Data
@apiSuccess {String} data.iban
@apiSuccess {String} data.accountNumber

@apiSuccessExample {json} Success-Response:
{ "data": {
    "iban":"LT03123450000000005436872",
    "accountNumber":"410075436872",
  }
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
@api {get} /api/private/v2/merchants/qubera/account/check/:currencyName Check qubera account exist
@apiName  Check qubera account exist
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates

@apiExample {curl} Example usage:
 curl -X GET \
      http://localhost:8080/api/private/v2/merchants/qubera/account/check/EUR \

      -H 'exrates-rest-token: $token' \


@apiParam {String} currencyName - currency name


@apiSuccess {boolean} data Data

@apiSuccessExample {json} Success-Response:
      {
        "data": true
      }
###

###
@api {get} /api/private/v2/merchants/qubera/account/info Get balance info
@apiName  Get balance info
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates

@apiExample {curl} Example usage:
 curl -X GET \
      http://localhost:8080/api/private/v2/merchants/qubera/account/info \

      -H 'exrates-rest-token: $token' \

@apiSuccess {Object} data Data
@apiSuccess {String} data.accountState
@apiSuccess {Object} data.availableBalance
@apiSuccess {Object} data.availableBalance.amount
@apiSuccess {Object} data.availableBalance.currencyCode
@apiSuccess {Object} data.currentBalance.amount
@apiSuccess {Object} data.currentBalance.currencyCode

@apiSuccessExample {json} Success-Response:
      {
        "data": {
          "accountState": "ACTIVE",
          "availableBalance": {
                "amount": 800,
                "currencyCode": "EUR"
              }
          "currentBalance": {
                "amount": 1000,
                "currencyCode": "EUR"
              }
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
@api {post} api/private/v2/merchants/qubera/payment/create Create payment
@apiName  Create payment to master
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/createe \
  -H 'Content-Type: application/json' \
  -H 'exrates-rest-token: $Token' \
  -d '{
    "currency":3,
    "merchant":376,
    "sum":200.0,
    "destination":"description",
    "merchantImage":70,
    "operationType":"INPUT"
}'

@apiParam {String} sum - for example: 10 or 10.0 or 10.00
@apiParam {Integer} currency - currency id
@apiParam {Integer} merchant - merchant id
@apiParam {Integer} merchantImage - merchant image id
@apiParam {String} destination
@apiParam {String} operationType - INPUT, OUTPUT

@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK

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
@api {post} /api/private/v2/balances/withdraw/request/create Create payment withdraw
@apiName  Create payment withdraw
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/balances/withdraw/request/create \
  -H 'Content-Type: application/json' \
  -H 'apiKey: e993670a-b7f7-4e0a-9742-68ff3b9ac09d' \
  -d '{
	"currency":3,
	"merchant":376,
	"destination":"fwefwegwegwegweg",
	"merchantImage":1576,
	"sum":"100",
	"destinationTag":"",
	"securityCode":"88914109"
}'

@apiParam {String} sum - for example: 10 or 10.0 or 10.00
@apiParam {Integer} currency - currency id
@apiParam {Integer} merchant - merchant id
@apiParam {Integer} merchantImage - merchant image id
@apiParam {String} destination
@apiParam {String} destinationTag
@apiParam {String} securityCode - code from 2FA or email

@apiSuccess {Boolean} data Data

@apiSuccessExample {json} Success-Response:
      {
        "data": true
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
