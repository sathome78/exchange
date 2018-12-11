package me.exrates.ngcontroller.model.response;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseModel<T> {

    private T data;

    private ResponseCustomError error;

    public ResponseModel() {
    }

    public ResponseModel(T data) {
        this.data = data;
    }

    public ResponseModel(T data, ResponseCustomError error) {
        this.data = data;
        this.error = error;
    }

    @JsonProperty("data")
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @JsonProperty("error")
    public ResponseCustomError getError() {
        return error;
    }

    public void setError(ResponseCustomError error) {
        this.error = error;
    }
}