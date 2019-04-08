###
@api {get} /api/private/v2/balances/myBalances/:currencyName Get active balance by currency name
@apiName Get active balance by currency name
@apiVersion 0.0.1
@apiGroup Balance
@apiUse Exrates

@apiExample {curl} Example usage:
curl -X GET \
  http://dev1.exrates.tech/api/private/v2/balances/myBalances/BTC \
  -H 'exrates-rest-token: $token'

@apiSuccessExample {json} Success-Response:
{
    "data": "0.23434343",
    "error": null
}
###
