package me.exrates.openapi.service.controllers;


@SuppressWarnings({"DanglingJavadoc", "unused"})
public class AuthInfo {

    /**
     * @apiDefine APIHeaders
     * @apiHeader {String} API-KEY Public key
     * @apiHeader {String} API-TIME Current time as UNIX timestamp in milliseconds
     * @apiHeader {String} API-SIGN Signature
     */

    /**
     * @api {get, post} /openapi/v1/{user-orders} Non-public endpoints
     * @apiName Non-public endpoints
     * @apiGroup Authentication
     * @apiUse APIHeaders
     * @apiPermission APIHeaders
     * @apiDescription All requests to non-public API endpoints must contain the following headers
     */
    private void stubHeaders(){
        //stub for API doc
    }

    /**
     * @api {get, post} /openapi/v1/{user-orders} Algorithm
     * @apiName Algorithm
     * @apiGroup Authentication
     * @apiDescription Authentication is performed via HMAC signature (using HMAC SHA256 algorithm).
     * To create the signature, you need to create your own public key and private key from your personal settings page
     * (API settings section).
     * @apiParam {String} http_method Method of current request (GET or POST)
     * @apiParam {String} endpoint Relative URI of the request (e.g. ‘/openapi/v1/orders/create’)
     * @apiParam {Number} timestamp Current time as UNIX timestamp in milliseconds
     * @apiParam {String} public_key public key generated by user
     * @apiParam {String} private_key private key generated by user
     * @apiParamExample Signature build example:
     *  SIG = HEX(HMAC(http_method|endpoint|timestamp|public_key, private_key))
     *
     *  Python
     *  import hmac, hashlib, binascii
     *
     *  def generate_signature(delimiter, method, endpoint, timestamp, public_key, secret):
     *  payload = delimiter.join([method, endpoint, str(timestamp), public_key])
     *  payload_encoded = payload.encode(encoding='UTF-8')
     *  signature = hmac.new(key=bytes(secret, encoding='UTF-8'), msg=payload_encoded, digestmod=hashlib.sha256).digest()
     *  sig_hex = binascii.hexlify(signature)
     *  return sig_hex.decode('UTF-8')
     */
    private void stubAlgorithm(){
        //stub for API doc
    }


}
