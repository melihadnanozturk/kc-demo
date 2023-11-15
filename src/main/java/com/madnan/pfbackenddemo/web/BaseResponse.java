package com.madnan.pfbackenddemo.web;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.ErrorResponse;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {

    public static final BaseResponse<Object> SUCCESS_RESPONSE = BaseResponse.builder().success(true).build();

    @Builder.Default
    private boolean success = true;

    private ErrorResponse error;

    private T data;
}
