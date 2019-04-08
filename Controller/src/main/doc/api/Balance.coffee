###
@api {get} /api/private/v2/balances/myBalances Get active balances by currency names
@apiName Get active balances by currency names
@apiVersion 0.0.1
@apiGroup Balance
@apiUse Exrates
@apiParam (names) {String[]} names array of currency names
@apiDescription Getting balance. If names is null, will return all currencies cost in btc and usd amounts

@apiExample {curl} Example usage:
curl -X GET \
  http://dev1.exrates.tech/api/private/v2/balances/myBalances?names=BTC,EUR \
  -H 'exrates-rest-token: $token'

@apiSuccessExample {json} Success-Response:
{
    "data": {
        "BTC":"0.34354353",
        "EUR":"100.23323"
        }
    "error": null
}

@apiExample {curl} Example usage:
curl -X GET \
  http://dev1.exrates.tech/api/private/v2/balances/myBalances \
  -H 'exrates-rest-token: $token'

@apiSuccessExample {json} Success-Response:
{
    "data": {
        "BTC":"0.34354353",
        "USD":"100.23323"
        }
    "error": null
}
###
