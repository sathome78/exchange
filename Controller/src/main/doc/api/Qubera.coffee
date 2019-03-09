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
@apiSuccess {String} data.currency
@apiSuccess {Number} data.availableBalance
@apiSuccess {Number} data.currentBalance

@apiSuccessExample {json} Success-Response:
      {
        "data": {
          "accountState": "ACTIVE",
          "availableBalance": 500,
          "currency": "EUR",
          "currentBalance": 800
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