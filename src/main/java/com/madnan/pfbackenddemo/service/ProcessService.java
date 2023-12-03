package com.madnan.pfbackenddemo.service;

import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.web.request.AnswerRequest;
import com.madnan.pfbackenddemo.web.request.OperationRequest;
import org.flowable.engine.impl.delegate.ActivityBehavior;

import java.util.List;

public interface ProcessService {

    TaskRepresentation startProcess(ActivityBehavior delegateService, String taskName, String userName);


    List<TaskRepresentation> getAllTasks(OperationRequest operationRequest);

    TaskRepresentation answerOperation(AnswerRequest answerRequest);
}
