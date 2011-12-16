package com.grahamlea.glissmetrics.exampleapplication.services;

public class AuthorisationResult {
    private final ResultCode resultCode;

    public AuthorisationResult(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
