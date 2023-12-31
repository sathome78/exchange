package me.exrates.model.constants;

public interface ErrorApiTitles {

    String USER_WRONG_CURRENT_PASSWORD = "USER_WRONG_CURRENT_PASSWORD";
    String USER_INCORRECT_PASSWORDS = "USER_INCORRECT_PASSWORDS";
    String USER_EMAIL_NOT_FOUND = "USER_EMAIL_NOT_FOUND";
    String USER_CREDENTIALS_NOT_COMPLETE = "USER_CREDENTIALS_NOT_COMPLETE";
    String EMAIL_AUTHORIZATION_FAILED = "EMAIL_AUTHORIZATION_FAILED";
    String USER_REGISTRATION_NOT_COMPLETED = "USER_REGISTRATION_NOT_COMPLETED";
    String USER_NOT_ACTIVE = "USER_NOT_ACTIVE";
    String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    String FAILED_TO_GET_USER_TOKEN = "FAILED_TO_GET_USER_TOKEN";
    String GOOGLE_AUTHORIZATION_FAILED = "GOOGLE_AUTHORIZATION_FAILED";
    String GOOGLE2FA_SUBMIT_FAILED = "GOOGLE2FA_SUBMIT_FAILED";
    String VERIFY_GOOGLE2FA_FAILED = "VERIFY_GOOGLE2FA_FAILED";
    String GOOGLE2FA_DISABLE_FAILED = "GOOGLE2FA_DISABLE_FAILED";
    String GOOGLE2FA_DISABLED = "GOOGLE2FA_DISABLED";
    // pin code sent over email needed to proceed
    String REQUIRED_EMAIL_AUTHORIZATION_CODE = "REQUIRED_EMAIL_AUTHORIZATION_CODE";
    String FAILED_TO_REGISTER_USER = "FAILED_TO_REGISTER_USER";
    // Google or email
    String REQUIRED_MODE_AUTHORIZATION_CODE = "REQUIRED_%s_AUTHORIZATION_CODE";

    String QUBERA_PARAMS_OVER_LIMIT = "QUBERA_PARAMS_OVER_LIMIT";
    String QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR = "QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR";
    String QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR = "QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR";
    String QUBERA_RESPONSE_CREATE_APPLICANT_ERROR = "QUBERA_RESPONSE_CREATE_APPLICANT_ERROR";
    String QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR = "QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR";
    String QUBERA_ACCOUNT_NOT_FOUND_ERROR = "QUBERA_ACCOUNT_NOT_FOUND_ERROR";
    String QUBERA_ACCOUNT_RESPONSE_ERROR = "QUBERA_ACCOUNT_RESPONSE_ERROR";
    String QUBERA_PAYMENT_TO_MASTER_ERROR = "QUBERA_PAYMENT_TO_MASTER_ERROR";
    String QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR = "QUBERA_CONFIRM_PAYMENT_TO_MASTER_ERROR";
    String QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT = "QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT";
    String QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT = "QUBERA_ERROR_RESPONSE_CREATE_EXTERNAL_PAYMENT";
    String QUBERA_KYC_ERROR_GET_STATUS = "QUBERA_KYC_ERROR_GET_STATUS";
    String QUBERA_KYC_RESPONSE_ERROR_GET_STATUS = "QUBERA_KYC_RESPONSE_ERROR_GET_STATUS";

    String IEO_INSUFFICIENT_BUYER_FUNDS = "IEO_INSUFFICIENT_BUYER_FUNDS";
    String IEO_CLAIM_SAVE_FAILURE = "IEO_CLAIM_SAVE_FAILURE";
    String IEO_USER_RESERVE_BTC_FAILURE = "IEO_USER_RESERVE_BTC_FAILURE";
    String IEO_NOT_STARTED_YET_OR_ALREADY_FINISHED = "IEO_NOT_STARTED_YET_OR_ALREADY_FINISHED";
    String IEO_DETAILS_NOT_FOUND = "IEO_DETAILS_NOT_FOUND";
    String IEO_MIN_AMOUNT_FAILURE = "IEO_MIN_AMOUNT_FAILURE";
    String IEO_MAX_AMOUNT_FAILURE = "IEO_MAX_AMOUNT_FAILURE";
    String IEO_MAX_AMOUNT_PER_USER_FAILURE = "IEO_MAX_AMOUNT_PER_USER_FAILURE";
    String IEO_CHECK_KYC_STATUS_FAILURE = "IEO_CHECK_KYC_STATUS_FAILURE";
    String IEO_UNKNOWN_STATUS = "IEO_UNKNOWN_STATUS";
    String IEO_USER_NOT_ADMIN = "IEO_USER_NOT_ADMIN";
    String IEO_ALREADY_PROCESSED = "IEO_ALREADY_PROCESSED";
    String IEO_MARKET_MAKER_RESTRICTION = "IEO_MARKET_MAKER_RESTRICTION";
    String IEO_NOT_FOUND = "IEO_NOT_FOUND";
    String IEO_FAILED_MOVE_TO_SUCCESS = "IEO_FAILED_MOVE_TO_SUCCESS";

    String EMPTY_CHAT_MESSAGE = "EMPTY_CHAT_MESSAGE";
    String FAIL_TO_PERSIST_CHAT_MESSAGE = "FAIL_TO_PERSIST_CHAT_MESSAGE";
    String FAIL_TO_GET_CURRENCY_PAIR_INFO = "FAIL_TO_GET_CURRENCY_PAIR_INFO";
    String FAILED_ACCEPT_TRANSFER = "FAILED_ACCEPT_TRANSFER";
    String FAILED_TO_SEND_USER_EMAIL = "FAILED_TO_SEND_USER_EMAIL";
    String CREATE_ORDER_FAILED = "CREATE_ORDER_FAILED";
    String DELETE_ORDER_FAILED = "DELETE_ORDER_FAILED";
    String FAILED_TO_GET_BALANCE_BY_CURRENCY = "FAILED_TO_GET_BALANCE_BY_CURRENCY";
    String FAILED_TO_FILTERED_ORDERS = "FAILED_TO_FILTERED_ORDERS";
    String FAILED_TO_GET_LAST_ORDERS = "FAILED_TO_GET_LAST_ORDERS";
    String FAILED_TO_SEND_RECOVERY_PASSWORD = "FAILED_TO_SEND_RECOVERY_PASSWORD";
    String FAILED_TO_CREATE_RECOVERY_PASSWORD = "FAILED_TO_CREATE_RECOVERY_PASSWORD";
    String PREFERRED_LOCALE_NOT_SAVE = "PREFERRED_LOCALE_NOT_SAVE";
    String UPDATE_SESSION_PERIOD_FAILED = "UPDATE_SESSION_PERIOD_FAILED";
    String UPDATE_USER_NOTIFICATION_FAILED = "UPDATE_USER_NOTIFICATION_FAILED";
    String UPLOAD_USER_VERIFICATION_FAILED = "UPLOAD_USER_VERIFICATION_FAILED";
    String UPLOAD_USER_VERIFICATION_DOCS_FAILED = "UPLOAD_USER_VERIFICATION_DOCS_FAILED";
    String FAILED_MANAGE_USER_FAVORITE_CURRENCY_PAIRS = "FAILED_MANAGE_USER_FAVORITE_CURRENCY_PAIRS";
    String FAILED_TO_CREATE_WITHDRAW_REQUEST = "FAILED_TO_CREATE_WITHDRAW_REQUEST";
    String FAILED_OUTPUT_CREDITS = "FAILED_OUTPUT_CREDITS";
    String FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL = "FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL";

    String API_WRONG_CURRENCY_PAIR_PATTERN = "API_WRONG_CURRENCY_PAIR_PATTERN";
    String API_UNAVAILABLE_CURRENCY_PAIR = "API_UNAVAILABLE_CURRENCY_PAIR";
    String API_CREATE_ORDER_ERROR = "API_CREATE_ORDER_ERROR";
    String API_ACCEPT_ORDER_ERROR = "API_ACCEPT_ORDER_ERROR";
    String API_ORDER_NOT_FOUND = "API_ORDER_NOT_FOUND";
    String API_ORDER_CREATED_BY_ANOTHER_USER = "API_ORDER_CREATED_BY_ANOTHER_USER";
    String API_ORDER_CANCEL_ERROR = "API_ORDER_CANCEL_ERROR";
    String API_ORDER_ADD_CALLBACK_ERROR = "API_ORDER_ADD_CALLBACK_ERROR";
    String API_USER_RESOURCE_ACCESS_DENIED = "API_USER_RESOURCE_ACCESS_DENIED";
    String API_REQUEST_ERROR_DATES = "API_REQUEST_ERROR_DATES";
    String API_REQUEST_ERROR_LIMIT = "API_REQUEST_ERROR_LIMIT";
    String API_VALIDATE_NUMBER_ERROR = "API_VALIDATE_NUMBER_ERROR";
    String API_INVALID_ORDER_CREATION_PARAMS = "API_INVALID_ORDER_CREATION_PARAMS ";
    String API_INVALID_CURRENCY_PAIR_NAME = "API_INVALID_CURRENCY_PAIR_NAME";
    String API_INSUFFICIENT_FUNDS_ERROR = "API_INSUFFICIENT_FUNDS_ERROR";
    String API_KEY_ALIAS_ERROR = "API_KEY_ALIAS_ERROR";

    String USER_OPERATION_DENIED = "USER_OPERATION_DENIED";
    String ORDER_TYPE_NOT_SUPPORTED = "ORDER_TYPE_NOT_SUPPORTED";
}