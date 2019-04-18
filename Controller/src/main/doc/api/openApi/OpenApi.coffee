###
@api {post} /openapi/v1/orders/create Create order
@apiName Creates order
@apiGroup Order API
@apiUse APIHeaders
@apiUse APIJson
@apiPermission NonPublicAuth
@apiDescription Creates Order
@apiParam {String} currency_pair Name of currency pair (e.g. btc_usd)
@apiParam {String} order_type Type of order (BUY or SELL)
@apiParam {Number} amount Amount in base currency
@apiParam {Number} price Exchange rate
@apiParamExample Request Example:
/openapi/v1/orders/create
{
"currencyPair": "btc_usd",
"orderType": "BUY",
"amount": 2.3,
"price": 1.0
}
@apiSuccess {Object} orderCreationResult Order creation result information
@apiSuccess {Integer} orderCreationResult.created_order_id Id of created order (not shown in case of partial accept)
@apiSuccess {Integer} orderCreationResult.auto_accepted_quantity Number of orders accepted automatically (not shown if no orders were auto-accepted)
@apiSuccess {Number} orderCreationResult.partially_accepted_amount Amount that was accepted partially (shown only in case of partial accept)

@apiError API_UNAVAILABLE_CURRENCY_PAIR error with currency pair, this pair available only through website
@apiError API_USER_RESOURCE_ACCESS_DENIED user doesn't have permission for create order
@apiError API_INVALID_ORDER_CREATION_PARAMS input params is incorrect
@apiError API_CREATE_ORDER_ERROR error while creating order
@apiError API_WRONG_CURRENCY_PAIR_PATTERN incorrect pattern of currency pair as expected: btc_usd

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "title": "API_UNAVAILABLE_CURRENCY_PAIR",
  "message": "message exception",
}

###

###

@api {post} /openapi/v1/orders/create/extended Create order extended
@apiName Creates order extended
@apiGroup Order API
@apiUse APIHeaders
@apiPermission NonPublicAuth
@apiDescription Creates Order
@apiParam {String} currency_pair Name of currency pair (e.g. btc_usd)
@apiParam {String} order_type Type of order (BUY or SELL)
@apiParam {Number} amount Amount in base currency
@apiParam {Number} price Exchange rate
@apiParamExample Request Example:
/openapi/v1/orders/create
{
"currencyPair": "btc_usd",
"orderType": "BUY",
"amount": 2.3,
"price": 1.0
}
@apiSuccess {Object} orderCreationResult Order creation result information
@apiSuccess {Integer} orderCreationResult.created_order_id Id of created order (not shown in case of partial accept)
@apiSuccess {Integer} orderCreationResult.auto_accepted_quantity Number of orders accepted automatically (not shown if no orders were auto-accepted)
@apiSuccess {Number} orderCreationResult.partially_accepted_amount Amount that was accepted partially (shown only in case of partial accept)
@apiSuccess {Array} orderCreationResult.fully_accepted_orders_ids ids of orders that has been fully accepted
@apiSuccess {Integer} orderCreationResult.partially_accepted_order_id id of order that partially accepted and splitted as a result)
@apiSuccess {Integer} orderCreationResult.order_id_to_accept id of order that opened and accepted as a result of partially accept)
@apiSuccess {Integer} orderCreationResult.order_id_to_open id of order that opened and placed in common stack as a results of partially accept)

@apiError API_UNAVAILABLE_CURRENCY_PAIR error with currency pair, this pair available only through website
@apiError API_USER_RESOURCE_ACCESS_DENIED user doesn't have permission for create order
@apiError API_INVALID_ORDER_CREATION_PARAMS input params is incorrect
@apiError API_CREATE_ORDER_ERROR error while creating order

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "title": "API_UNAVAILABLE_CURRENCY_PAIR",
  "message": "message exception",
}
###

###

@api {get} /openapi/v1/orders/accept/{orderId} Accept order by id
@apiName Accept order
@apiGroup Order API
@apiUse APIHeaders
@apiUse APIJson
@apiPermission NonPublicAuth
@apiDescription Accepts order
@apiParam {Integer} order id
@apiParamExample Request Example:
/openapi/v1/orders/accept/1233
@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK

@apiError API_ACCEPT_ORDER_ERROR error while not found order

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
"errorCode": "API_ACCEPT_ORDER_ERROR",
"url" : String,
"detail" : String
}

###

###

@api {get} /openapi/v1/orders/{orderId} Find order by id
@apiName Get order by id
@apiGroup Order API
@apiUse APIHeaders
@apiPermission NonPublicAuth
@apiDescription Accepts order
@apiParam {Integer} order_id Id of requested order
@apiParamExample Request Example:
/openapi/v1/orders/123
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.id Order id
@apiSuccess {Integer} data.currencyPairId Currency pair id
@apiSuccess {String} data.operationType type of order operation (BUY or SELL)
@apiSuccess {Number} data.exRate Rate
@apiSuccess {Number} data.amountBase Amount to process
@apiSuccess {Number} data.amountConvert Base amount multiply by exRate
@apiSuccess {Number} data.commission Commission's amount
@apiSuccess {Integer} data.userAcceptorId User-acceptor id
@apiSuccess {LocalDateTime} data.created When order was created
@apiSuccess {LocalDateTime} data.accepted When order was accepted
@apiSuccess {Integer} data.userAcceptorId User-acceptor id
@apiSuccess {String} data.status type of order status (INPROCESS, OPENED, CLOSED, CANCELLED, DELETED, DRAFT, SPLIT_CLOSED)
@apiSuccess {Integer} data.sourceId Source id
@apiSuccess {Number} data.stop  Stop price
@apiSuccess {String} data.baseType type of order status (LIMIT, STOP_LIMIT, ICO)
@apiSuccess {Number} data.partiallyAcceptedAmount  Partially accepted amount
@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
{
  "id": 402298,
  "currencyPairId": 1,
  "order_type": "BUY",
  "price": 2000.009900479,
  "amount": 1,
  "amountConvert": 2000.009900479,
  "commission": 4.000019801,
  "userAcceptorId": 0,
  "created": "2018-12-22 00:49:11",
  "accepted": null",
  "status": "OPENED",
  "baseType": "LIMIT",
  "stop": null,
  "partiallyAcceptedAmount": null
}

@apiError API_ORDER_NOT_FOUND error while not found order


@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_ORDER_NOT_FOUND",
  "url" : String,
  "detail" : String
}
###

###
@api {delete} /openapi/v1/orders/{orderId} Cancel order by id
@apiName Cancel order by order id
@apiGroup Order API
@apiUse APIHeaders
@apiPermission NonPublicAuth
@apiDescription Cancel order by order id
@apiParam {Integer} order_id Id of order to be cancelled
@apiParamExample Request Example:
/openapi/v1/orders/123
@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK

@apiError API_ORDER_CREATED_BY_ANOTHER_USER error, order was created by another user
@apiError API_ORDER_CANCEL_ERROR error while canceling order, order already closed

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_ORDER_CREATED_BY_ANOTHER_USER",
  "url" : String,
  "detail" : String
}

###

###

@api {post} /openapi/v1/orders/callback/add Add callback
@apiName add callback
@apiGroup Order API
@apiUse APIHeaders
@apiUse APIJson
@apiPermission NonPublicAuth
@apiDescription Add callback
@apiParamExample Request Example:
/openapi/v1/orders/callback/add

{
"callbackURL": String,
"pairId": Integer
}
@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
{
"status": true
}

@apiError API_ORDER_ADD_CALLBACK_ERROR callback already exist

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_ORDER_ADD_CALLBACK_ERROR",
  "url" : String,
  "detail" : String
}
###

###

@api {put} /openapi/v1/orders/callback/add Update callback
@apiName update callback
@apiGroup Order API
@apiUse APIHeaders
@apiUse APIJson
@apiPermission NonPublicAuth
@apiDescription Update callback
@apiParamExample Request Example:
/openapi/v1/orders/callback/update
{
"callbackURL": String,
"pairId": Integer
}
@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
{
"status": true
}
@apiErrorExample {json} Error-Response:
HTTP/1.1 200 OK
{
"status": "false",
"error" : " Callback url is null or empty"
}

###

###

@api {get} /openapi/v1/orders/open/{order_type}?currency_pair Open orders
@apiName Open orders
@apiGroup Order API
@apiUse APIHeaders
@apiPermission NonPublicAuth
@apiDescription Buy or sell open orders ordered by price (SELL ascending, BUY descending)
@apiParam {String} order_type Type of order (BUY or SELL)
@apiParam {String} currency_pair Name of currency pair
@apiParamExample Request Example:
/openapi/v1/orders/open/SELL?btc_usd
@apiSuccess {Array} openOrder Open Order Result
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.id Order id
@apiSuccess {String} data.order_type type of order (BUY or SELL)
@apiSuccess {Number} data.amount Amount in base currency
@apiSuccess {Number} data.price Exchange rate


@apiError API_USER_RESOURCE_ACCESS_DENIED user doesn't have permission for this operation

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_USER_RESOURCE_ACCESS_DENIED",
  "url" : String,
  "detail" : String
}

###

###

@api {get} /openapi/v1/public/ticker?currency_pair Ticker Info
@apiName Ticker
@apiGroup Public API
@apiPermission user
@apiDescription Returns array of ticker info objects

@apiParam {String} currency_pair Currency pair name (optional)

@apiParamExample Request Example:
/openapi/v1/public/ticker?currency_pair=btc_usd

@apiSuccess {Array} Ticker Infos result
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.id Currency pair id
@apiSuccess {String} data.name Currency pair name
@apiSuccess {Number} data.last Price of last accepted order
@apiSuccess {Number} data.lowestAsk 	Lowest price of opened sell order
@apiSuccess {Number} data.highestBid Highest price of opened buy order
@apiSuccess {Number} data.percentChange Change for period, %
@apiSuccess {Number} data.baseVolume Volume of trade in base currency
@apiSuccess {Number} data.quoteVolume Volume of trade in quote currency
@apiSuccess {Number} data.high Highest price of accepted orders
@apiSuccess {Number} data.low Lowest price of accepted orders

@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
[
 {
     "id": 123,
     "name": "currencyPairName",
     "last": 12341,
     "lowestAsk": 12342,
     "highestBid":  12343
     "percentChange":  1
     "baseVolume": 10
     "quoteVolume": 11
     "high": 10
     "low": 1
 }
]

###

###

@api {get} /openapi/v1/public/orderbook/{currency_pair}?order_type Order Book
@apiName Order Book
@apiGroup Public API
@apiPermission user
@apiDescription Books Order

@apiParam {String} order_type Order type (BUY or SELL) (optional)

@apiParamExample Request Example:
/openapi/v1/public/orderbook/btc_usd/?order_type=SELL

@apiSuccess {Map} Object with SELL and BUY fields, each containing array of open orders info objects
(sorted by price - SELL ascending, BUY descending).
amount -	order amount in base currency
rate	- exchange rate

###

###

@api {get} /openapi/v1/public/history/{currency_pair}?from_date&to_date&limit&direction Trade History
@apiName Trade History
@apiGroup Public API
@apiPermission user
@apiDescription Provides collection of trade info objects

@apiParam {LocalDate} from_date start date of search (date format: yyyy-MM-dd)
@apiParam {LocalDate} to_date end date of search (date format: yyyy-MM-dd)
@apiParam {Integer} limit limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
@apiParam {String} result direction (allowed values: ASC or DESC, default value: ASC) (optional)

@apiParamExample Request Example:
openapi/v1/public/history/btc_usd?from_date=2018-09-01&to_date=2018-09-05&limit=20&direction=DESC

@apiSuccess {Array} Array of trade info objects
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.order_id Order id
@apiSuccess {String} data.date_acceptance Order acceptance date
@apiSuccess {String} data.date_creation Order creation date
@apiSuccess {Number} data.amount Order amount in base currency
@apiSuccess {Number} data.price Exchange rate
@apiSuccess {Number} data.total Total sum
@apiSuccess {Number} data.commission commission
@apiSuccess {String} data.order_type Order type (BUY or SELL)

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_REQUEST_ERROR_DATES",
  "url" : String,
  "detail" : String
}
@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_REQUEST_ERROR_LIMIT",
  "url" : String,
  "detail" : String
}

###

###

@api {get} /openapi/v1/public/currency_pairs Currency Pairs
@apiName Currency Pairs
@apiGroup Public API
@apiPermission user
@apiDescription Provides collection of currency pairs

@apiParamExample Request Example:
openapi/v1/public/currency_pairs

@apiSuccess {Array} Array of currency pairs
@apiSuccess {Object} data Container object
@apiSuccess {String} data.name Currency pair name
@apiSuccess {String} data.url_symbol URL symbol (name to be passed as URL parameter or path variable)

###

###
@api {get} /openapi/v1/public/{currency_pair}/candle_chart?interval_type&interval_value Data for candle chart
@apiName Data for candle chart
@apiGroup Public API
@apiPermission user
@apiDescription Data for candle chart

@apiParam {String} interval_type type of interval (valid values: "HOUR", "DAY", "MONTH", "YEAR")
@apiParam {Integer} interval_value value of interval

@apiParamExample Request Example:
/openapi/v1/public/btc_usd/candle_chart?interval_type=DAY&interval_value=7

@apiSuccess {Array} chartData Request result
@apiSuccess {Object} data Candle chart data item
@apiSuccess {Object} data.beginPeriod beginning of period as Java8 LocalDateTime
@apiSuccess {Object} data.endPeriod end of period as Java8 LocalDateTime
@apiSuccess {Number} data.openRate open rate
@apiSuccess {Number} data.closeRate close rate
@apiSuccess {Number} data.lowRate low rate
@apiSuccess {Number} data.highRate high rate
@apiSuccess {Number} data.baseVolume base amount of order
@apiSuccess {Number} data.beginDate same as beginPeriod, different format
@apiSuccess {Number} data.endDate same as endPeriod, different format

@apiSuccessExample {json} Success-Response:
HTTP/1.1 200 OK
[
 {
     "openRate":0.1,
     "closeRate":0.1,
     "lowRate":0.1,
     "highRate":0.1,
     "baseVolume":0,
     "beginDate":1472132318000,
     "endDate":1472132378000
 },
 {
     "openRate":0.1,
     "closeRate":0.1,
     "lowRate":0.1,
     "highRate":0.1,
     "baseVolume":0,
     "beginDate":1472132378000,
     "endDate":1472132438000
 }
]

###

###
@api {get} /openapi/v1/user/balances User Balances
@apiName User Balances
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Returns array of wallet objects
@apiParamExample Request Example:
/openapi/v1/user/balances
@apiSuccess {Array} Wallet objects result
@apiSuccess {Object} data Container object
@apiSuccess {String} data.currencyName Name of currency
@apiSuccess {Number} data.activeBalance Balance that is available for spending
@apiSuccess {Number} data.reservedBalance Balance reserved for orders or withdraw

###

###
@api {get} /openapi/v1/user/orders/open?currency_pair User's open orders
@apiName Open orders
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Returns collection of user open orders
@apiParam {String} currency_pair Name of currency pair (optional)
@apiParamExample Request Example:
/openapi/v1/user/orders/open?currency_pair=btc_usd
@apiSuccess {Array} User orders result
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.id Order id
@apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
@apiSuccess {Number} data.amount Amount in base currency
@apiSuccess {String} data.order_type Type of order (BUY or SELL)
@apiSuccess {Number} data.price Exchange rate
@apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
@apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis

###

###

@api {get} /openapi/v1/user/orders/closed?currency_pair&limit&offset User's closed orders
@apiName Closed orders
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Returns collection of user closed orders sorted by creation time
@apiParam {String} currency_pair Name of currency pair (optional)
@apiParam {Integer} limit Number of orders returned (default - 20, max - 100) (optional)
@apiParam {Integer} offset Number of orders skipped (optional)
@apiParamExample Request Example:
/openapi/v1/user/orders/closed?currency_pair=btc_usd&limit=100&offset=10
@apiSuccess {Array} User orders result
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.id Order id
@apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
@apiSuccess {Number} data.amount Amount in base currency
@apiSuccess {String} data.order_type Type of order (BUY or SELL)
@apiSuccess {Number} data.price Exchange rate
@apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
@apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis

@apiError API_INVALID_CURRENCY_PAIR_NAME Currency name is incorrect
@apiError API_VALIDATE_NUMBER_ERROR limit or offset in not integer
@apiError API_ORDER_NOT_FOUND order not found

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_VALIDATE_NUMBER_ERROR",
  "url" : String,
  "detail" : String
}

###

###
@api {get} /openapi/v1/user/orders/canceled?currency_pair&limit&offset User's canceled orders
@apiName Canceled orders
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Returns collection of user canceled orders sorted by creation time
@apiParam {String} currency_pair Name of currency pair (optional)
@apiParam {Integer} limit Number of orders returned (default - 20, max - 100) (optional)
@apiParam {Integer} offset Number of orders skipped (optional)
@apiParamExample Request Example:
/openapi/v1/user/orders/canceled?currency_pair=btc_usd&limit=100&offset=10
@apiSuccess {Array} User orders result
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.id Order id
@apiSuccess {String} data.currency_pair Name of currency pair (e.g. btc_usd)
@apiSuccess {Number} data.amount Amount in base currency
@apiSuccess {String} data.order_type Type of order (BUY or SELL)
@apiSuccess {Number} data.price Exchange rate
@apiSuccess {Number} data.date_created Creation time as UNIX timestamp in millis
@apiSuccess {Number} data.date_accepted Acceptance time as UNIX timestamp in millis

@apiError API_VALIDATE_NUMBER_ERROR limit or offset in not integer
@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_VALIDATE_NUMBER_ERROR",
  "url" : String,
  "detail" : String
}
###

###

@api {get} /openapi/v1/user/commissions User’s commission rates
@apiName Commissions
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Returns info on user’s commission rates
(as per cent - for example, 0.5 rate means 0.5% of amount) by operation type.
Commissions for orders (sell and buy) are calculated and withdrawn from amount in quote currency.
@apiParamExample Request Example:
/openapi/v1/user/commissions
@apiSuccess {Object} data Container object
@apiSuccess {Number} data.input Commission for input operations
@apiSuccess {Number} data.output Commission for output operations
@apiSuccess {Number} data.sell Commission for sell operations
@apiSuccess {Number} data.buy Commission for buy operations
@apiSuccess {Number} data.transfer Commission for transfer operations

###

###

@api {get} /openapi/v1/user/history/{currency_pair}/trades?from_date&to_date&limit User trade history
@apiName User Trade History
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Provides collection of user trade info objects
@apiParam {LocalDate} from_date start date of search (date format: yyyy-MM-dd)
@apiParam {LocalDate} to_date end date of search (date format: yyyy-MM-dd)
@apiParam {Integer} limit limit number of entries (allowed values: limit could not be equals or be less then zero, default value: 50) (optional)
@apiParamExample Request Example:
openapi/v1/user/history/btc_usd/trades?from_date=2018-09-01&to_date=2018-09-05&limit=20
@apiSuccess {Array} Array of user trade info objects
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.user_id User id
@apiSuccess {Boolean} data.maker User is maker
@apiSuccess {Integer} data.order_id Order id
@apiSuccess {String} data.date_acceptance Order acceptance date
@apiSuccess {String} data.date_creation Order creation date
@apiSuccess {Number} data.amount Order amount in base currency
@apiSuccess {Number} data.price Exchange rate
@apiSuccess {Number} data.total Total sum
@apiSuccess {Number} data.commission commission
@apiSuccess {String} data.order_type Order type (BUY or SELL)

@apiError API_REQUEST_ERROR_DATES From date is after to date
@apiError API_REQUEST_ERROR_LIMIT Limit value equals or less than zero

@apiErrorExample {json} Error-Response:
HTTP/1.1 400 Bad request
{
  "errorCode": "API_REQUEST_ERROR_DATES",
  "url" : String,
  "detail" : String
}

###

###

@api {get} /openapi/v1/user/history/{order_id}/transactions Order transactions history
@apiName Order transactions history
@apiGroup User API
@apiPermission NonPublicAuth
@apiDescription Provides collection of user transactions info objects
@apiParamExample Request Example:
openapi/v1/user/history/1/transactions
@apiSuccess {Array} Array of user trade info objects
@apiSuccess {Object} data Container object
@apiSuccess {Integer} data.transaction_id Transaction id
@apiSuccess {Integer} data.wallet_id User wallet id
@apiSuccess {Number} data.amount Amount to sell/buy
@apiSuccess {Number} data.commission Commission
@apiSuccess {String} data.currency Operation currency
@apiSuccess {String} data.time Transaction creation date
@apiSuccess {String} data.operation_type Transaction operation type
@apiSuccess {String} data.transaction_status Transaction status

###