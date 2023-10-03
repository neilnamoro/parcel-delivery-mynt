package com.mynt.parceldelivery.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorMessage {

    private String code;
    private String message;

}
