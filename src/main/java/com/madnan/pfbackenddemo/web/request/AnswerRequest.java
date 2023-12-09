package com.madnan.pfbackenddemo.web.request;

import com.madnan.pfbackenddemo.model.OperationStatus;
import jakarta.validation.constraints.NotBlank;
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
    private String operationName;
    private OperationStatus operationStatus;
    @NotBlank
    private String processKey;
}
