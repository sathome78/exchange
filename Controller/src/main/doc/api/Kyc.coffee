###
@api {get} /api/private/v2/kyc/status Get status KYC
@apiName Get status KYC
@apiVersion 0.0.1
@apiGroup KYC
@apiUse Exrates

@apiExample {curl} Example usage:
curl -X GET \
  http://dev1.exrates.tech/api/private/v2/kyc/status \
  -H 'exrates-rest-token: $token'

@apiSuccess {String} data [CREATED, CLICKED, CAPTURE_ONGOING, SUCCESS, TECHNICAL_ERROR, TOO_MANY_ANALYSIS, EXPIRED]

@apiSuccessExample {json} Success-Response:
{
    "data": "SUCCESS",
    "error": null
}
###

###
@api {post} /api/private/v2/kyc/start Start KYC Processing
@apiName  Start KYC Processing
@apiVersion 0.0.1
@apiGroup KYC
@apiUse Exrates
@apiUse ApiJSON

@apiExample {curl} Example usage:
curl -X POST \
  http://dev1.exrates.tech/api/private/v2/kyc/start \
  -H 'Content-Type: application/json' \
  -H 'exrates-rest-token: $token' \
  -d '{
          "birthDay": "12",
          "birthMonth": "5",
          "birthYear": "1987",
          "firstNames": [
            "Ivan"
          ],
          "lastName": "Ivanov",
          "docType":"P"
}'

@apiSuccess {Object} data Data
@apiSuccess {String} data.uid uid request
@apiSuccess {String} data.url link to kyc processing
@apiSuccess {Date}   data.expirationDate date of valid link
@apiSuccess {Object} data.dispatchInfo Data
@apiSuccess {String} data.dispatchInfo.notificationType String
@apiSuccess {String} data.dispatchInfo.msg String

@apiSuccessExample {json} Success-Response:
{
    "data": {
        "uid": "5c8902f803ae9d5a22e5208b",
        "url": "https://sdkweb-test.idcheck.io/AriadNEXT/5c8902f803ae9d5a22e5208b/welcome",
        "expirationDate": "2019-03-14T14:17:44+0100",
        "dispatchInfo": {
            "notificationType": "EMAIL",
            "msg": "You started a IDCHECKIO SDK Web document capture for PROD tests. Click here https://sdkweb-test.idcheck.io/AriadNEXT/5c8902f803ae9d5a22e5208b/welcome to begin the document capture service."
        }
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

