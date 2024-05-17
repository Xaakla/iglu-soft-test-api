package com.iglusoft.api.interfaces;

import com.iglusoft.api.commons.ObjectValidationResponse;

public interface IValidatesObject<T> {
    ObjectValidationResponse validate(T t);
}
