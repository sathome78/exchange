###
@api {get} /api/public/v2/news Get partner news
@apiName Get partner news
@apiVersion 0.0.1
@apiGroup News
@apiUse ApiJSON

@apiParam {Integer} [index]  Optional Number of resource, default value 0
@apiParam {Integer} [count]  Optional Number of count news, default value 10
@apiParam {Integer} [offset]  Optional Number of offset, default value 0
@apiExample {curl} Example usage:
curl -X GET \
  'http://localhost:8080/api/public/v2/news?index=0&count=10&offset=0' \
  -H 'Content-Type: application/json' \

{
    "data": {
        "feeds": [
            {
                "title": "XRP Price Analysis: XRP/USD Price May Reverse at $0.28 Level",
                "url": "https://www.coinspeaker.com/?p=80252",
                "date": 1557470394000
            }
        ],
        "count": 140
    },
    "error": null
}
@apiSuccess {Object} data Data
@apiSuccess {Object} data.feeds Data
@apiSuccess {Integer} data.count Integer
@apiSuccess {String} data.feeds.title String
@apiSuccess {String} data.feeds.url String
@apiSuccess {Date} data.feeds.date Timestamp
###