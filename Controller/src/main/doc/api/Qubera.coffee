###
@api {post} /api/private/v2/merchants/qubera/account/create Create qubera account
@apiName Create qubera account
@apiVersion 0.0.1
@apiGroup Qubera
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
 curl -X GET \
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