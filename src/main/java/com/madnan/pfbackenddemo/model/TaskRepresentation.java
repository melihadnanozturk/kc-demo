package com.madnan.pfbackenddemo.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRepresentation {

    private String refId;
    private String operationName;
    private String operationStatus;
    private String operationId;
    private String createdBy;
    private Date createdDate;

}
