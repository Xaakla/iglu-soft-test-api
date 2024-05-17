package com.iglusoft.api.commons;

public record ObjectValidationResponse(boolean result, String message) {

    public boolean isValid() {
        return result;
    }

    public boolean isInvalid() {
        return !result;
    }
}
