package com.madnan.pfbackenddemo.service;

import com.madnan.pfbackenddemo.model.TaskDetailRepresentation;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.web.request.AnswerRequest;
import com.madnan.pfbackenddemo.web.request.OperationRequest;

import java.util.List;
import java.util.Map;

public interface ProcessService {

    TaskRepresentation startProcess(Map<String, Object> variables, String flowName);


    List<TaskRepresentation> getAllTasks(OperationRequest operationRequest);

    TaskRepresentation answerOperation(AnswerRequest answerRequest);

    TaskDetailRepresentation getTaskDetail(String taskId);
}
