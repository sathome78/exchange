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
	"pin":"43851629"
}'

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
@api {get} api/private/v2/merchants/qubera/info Get info for payment
@apiName  Get info for payment
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates

@apiExample {curl} Example usage:
 curl -X GET \
      http://localhost:8080/api/private/v2/merchants/qubera/info \

      -H 'exrates-rest-token: $token' \

@apiSuccessExample {json} Success-Response:
{
    "data": {
        "iban": "LT551234500002717981",
        "bic": "STUALT21XXX",
        "swiftCode": "STUALT21",
        "bankName": "SATCHELPAY UAB",
        "country": "LITHUANIA (LT)",
        "city": "VILNIUS",
        "address": "LITHUANIA (LT)"
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
@api {get} api/private/v2/merchants/qubera/verification_status Get verification status
@apiName  Get verification status
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates

@apiExample {curl} Example usage:
curl -X GET \
  http://localhost:8080/api/private/v2/merchants/qubera/verification_status \
  -H 'exrates-rest-token: $token'

@apiSuccessExample {json} Success-Response:
{
    "data": "SUCCESS",
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
@api {post} api/private/v2/balances/refill/request/create Create payment
@apiName  Create payment to master
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/balances/refill/request/create \
  -H 'Content-Type: application/json' \
  -H 'exrates-rest-token: $Token' \
  -d '{
    "currency":3,
    "merchant":376,
    "sum":200.0,
    "destination":"description",
    "merchantImage":70,
    "operationType":"INPUT",
    "pin": "3432432
}'

@apiParam {String} sum - for example: 10 or 10.0 or 10.00
@apiParam {Integer} currency - currency id
@apiParam {Integer} merchant - merchant id
@apiParam {Integer} merchantImage - merchant image id
@apiParam {String} destination
@apiParam {String} operationType - INPUT, OUTPUT
@apiParam {String} pin - pin code from transfer request

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

###
@api {post} api/private/v2/merchants/qubera/request/pin Create pin code for create qubera account
@apiName  Create pin code for create qubera account
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/merchants/qubera/request/pin \
  -H 'Exrates-rest-token: $token' \

@apiSuccessExample {json} Success-Response:
HTTP/1.1 201 Created

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
@api {put} api/private/v2/merchants/qubera/payment/external/confirm Confirm external payment
@apiName  Confirm external payment
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X PUT \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/external/confirm \
   -H 'Content-Type: application/json' \
   -H 'exrates-rest-token: $token' \
   -d '{
	    "pin":"324234234",
	    "paymentId":376
}'

@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
  {
  "data":true
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
@api {post} /api/private/v2/merchants/qubera/payment/external Create external payment
@apiName  Create external payment
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON


@apiDescription Payment for 2 types: sepa and swift. FirstName and lastName setup if companyName is null, and vice versa.
@apiExample {curl} SEPA:
curl -X POST \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/external \
  -H 'Content-Type: application/json' \
  -H 'exrates-rest-token: $token' \
  -d '{
	"amount":"200",
	"currencyCode":"EUR",
	"firstName":"Jonh",
	"lastName":"Dou",
	"companyName":"Roga&Koputa",
	"iban":"wefewfwefwefwefwe",
	"narrative":"description",
	"type":"sepa"
}'

@apiExample {curl} SWIFT:
  curl -X POST \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/external \
  -H 'Content-Type: application/json' \
  -H 'exrates-rest-token: $token' \
  -d '{
	"amount":"200",
	"currencyCode":"EUR",
	"firstName":"Jonh",
	"lastName":"Dou",
	"companyName":"Roga&Koputa",
	"accountNumber":"wefewfwefwefwefwe",
	"swift": "wefewfw",
	"narrative":"description",
	"type":"swift",
	"address":"Flat 121, holodnogorskaya 6",
	"city":"Kharkov",
	"countryCode":"UA"
}'

@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
  {
    "id": 10000177,
    "paymentAmount": {
      "amount": 10.00,
      "currencyCode": "EUR"
      },
    "feeAmount": {
      "amount": 105.00,
      "currencyCode": "EUR"
      },
   "rate": {
      "from": "EUR",
       "to": "EUR",
      "value": 1
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
@api {post} /api/private/v2/balances/withdraw/request/pin Create pin code for withdraw from bank account
@apiName  Create pin code for withdraw from bank account
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/balances/withdraw/request/pin \
  -H 'Exrates-rest-token: $token' \
  -d '{
	"amount": 200,
	"currencyName":"EUR",

@apiSuccessExample {json} Success-Response:
HTTP/1.1 201 Created

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
@api {post} /api/private/v2/balances/transfer/request/pin Create pin code for transfer from bank account to trading account
@apiName  Create pin code for transfer from bank account to trading account
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/balances/transfer/request/pin \
  -H 'Exrates-rest-token: $token' \
  -d '{
	"amount": 200,
	"currencyName":"EUR",

@apiSuccessExample {json} Success-Response:
HTTP/1.1 201 Created

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