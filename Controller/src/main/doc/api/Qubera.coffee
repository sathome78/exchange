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
	    "dateOfBirth":"30/07/1968",
	    "zipCode":"92200",
	    "street":"Neuilly sur seine",
	    "country":"France",
	    "phone":"33123456789"
}'

@apiParam {String} firstName - first name
@apiParam {String} lastName - last name
@apiParam {String} dateOfBirth - date of birth
@apiParam {String} zipCode - zip code
@apiParam {String} street - street
@apiParam {String} country - country
@apiParam {String} phone - phone

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
@api {get} /api/private/v2/merchants/qubera/payment/toMaster Create payment to master
@apiName  Create payment to master
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/toMaster \
  -H 'Content-Type: application/json' \
  -H 'apiKey: e993670a-b7f7-4e0a-9742-68ff3b9ac09d' \
  -d '{
	    "amount":10.0,
	    "currencyCode":"EUR"
}'

@apiParam {String} amount - for example: 10 or 10.0 or 10.00
@apiParam {String} currencyCode - currency, min=3 chars, max=3 chars

@apiSuccess {Object} data Data
@apiSuccess {String} data.currencyFrom
@apiSuccess {String} data.currencyTo
@apiSuccess {Number} data.feeAmount
@apiSuccess {String} data.feeCurrencyCode
@apiSuccess {Number} data.paymentId
@apiSuccess {Number} data.rate
@apiSuccess {Number} data.transactionAmount
@apiSuccess {String} data.transactionCurrencyCode

@apiSuccessExample {json} Success-Response:
      {
        "data": {
            "currencyFrom": "string",
            "currencyTo": "string",
            "feeAmount": 0,
            "feeCurrencyCode": "string",
            "paymentId": 0,
            "rate": 0,
            "transactionAmount": 0,
            "transactionCurrencyCode": "string"
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
@api {get} /api/private/v2/merchants/qubera/payment/fromMaster Create payment from master
@apiName  Create payment from master
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X POST \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/fromMaster \
  -H 'Content-Type: application/json' \
  -H 'apiKey: e993670a-b7f7-4e0a-9742-68ff3b9ac09d' \
  -d '{
	    "amount":10.0,
	    "currencyCode":"EUR"
}'

@apiParam {String} amount - for example: 10 or 10.0 or 10.00
@apiParam {String} currencyCode - currency, min=3 chars, max=3 chars

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
@api {put} /merchants/qubera/confirm/{paymentId}/toMaster Confirm payment to master
@apiName  Confirm payment to master
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates

@apiExample {curl} Example usage:
 curl -X PUT \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/234235436467/toMaster \
  -H 'apiKey: e993670a-b7f7-4e0a-9742-68ff3b9ac09d'

@apiSuccess {String} data Data

@apiSuccessExample {json} Success-Response:
      {
        "data": "SUCCESS"
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
@api {put} /merchants/qubera/confirm/{paymentId}/fromMaster Confirm payment from master
@apiName  Confirm payment from master
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates

@apiExample {curl} Example usage:
 curl -X PUT \
  http://localhost:8080/api/private/v2/merchants/qubera/payment/234235436467/fromMaster \
  -H 'apiKey: e993670a-b7f7-4e0a-9742-68ff3b9ac09d'

@apiSuccess {String} data Data

@apiSuccessExample {json} Success-Response:
      {
        "data": "SUCCESS"
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
