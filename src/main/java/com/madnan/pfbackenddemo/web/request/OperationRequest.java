package com.madnan.pfbackenddemo.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationRequest {

    int pageNumber;
    private Date afterCreatedTime;
    private String operationName;
    private String sortedBy;
}
