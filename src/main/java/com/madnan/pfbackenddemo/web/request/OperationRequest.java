package com.madnan.pfbackenddemo.web.request;

import com.madnan.pfbackenddemo.model.OperationStatus;
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
    int pageSize;
    private String sortedBy;
    private Date afterCreatedTime;
    private String operationName;
    private String operationId;
    private String refId;
    private OperationStatus operationStatus;

}
