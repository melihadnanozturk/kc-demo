package com.madnan.pfbackenddemo.web.request;

import com.madnan.pfbackenddemo.model.OperationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {
    private String taskId;
    private OperationStatus operationStatus;
}
