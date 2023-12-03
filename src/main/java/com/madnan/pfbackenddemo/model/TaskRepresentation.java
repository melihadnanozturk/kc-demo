package com.madnan.pfbackenddemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRepresentation {

    private String operationId;
    private String operationName;
    private String operationStatus;
    private Date createdDate;
    private Date endDate;

}
