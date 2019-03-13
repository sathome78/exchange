package me.exrates.model.constants;

public interface Constants {
    interface ErrorApi {

        int USER_WRONG_CURRENT_PASSWORD = 1010;
        int USER_INCORRECT_PASSWORDS = 1011;

        int QUBERA_PARAMS_OVER_LIMIT = 1200;
        int QUBERA_CREATE_ACCOUNT_RESPONSE_ERROR = 1201;
        int QUBERA_SAVE_ACCOUNT_RESPONSE_ERROR = 1202;
        int QUBERA_RESPONSE_CREATE_APPLICANT_ERROR = 1203;
        int QUBERA_RESPONSE_CREATE_ONBOARDING_ERROR = 1204;
    }
}
