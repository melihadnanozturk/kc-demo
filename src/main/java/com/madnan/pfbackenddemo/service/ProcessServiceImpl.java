package com.madnan.pfbackenddemo.service;

import com.madnan.pfbackenddemo.model.OperationStatus;
import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskDetailRepresentation;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.web.request.AnswerRequest;
import com.madnan.pfbackenddemo.web.request.OperationRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private ProcessEngine processEngine;

    private static final String VERSION_CONTROL_KEY = "V2";

    @Transactional
    public TaskRepresentation startProcess(Map<String, Object> variables, String flowName) {

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(flowName, VERSION_CONTROL_KEY, variables);

        //todo: burada birden fazla istek atılırsa hata fırlatma mekanizması eklenebilir

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .orderByTaskCreateTime()
                .desc().singleResult();

        changeStatusKeyValueForLocal(variables);
        taskService.setVariablesLocal(task.getId(), variables);

        Map<String, Object> taskVariables = taskService.getVariables(task.getId());

        return new TaskRepresentation(task.getId(),
                task.getName(),
                (String) taskVariables.get(OperationVariables.LOCAL_STATUS.name()),
                (String) variables.get(OperationVariables.USER_NAME.name()), //todo: burada kullanıcı ismi disaridan gelecek, buradakki logic degisebilir
                task.getCreateTime());
    }


    @Override
    @SneakyThrows
    public List<TaskRepresentation> getAllTasks(OperationRequest request) {
        //todo: STATUS degeri variables icerisinde oldugu icin variablesı forEach ile almak zorunda kaldik
        List<TaskRepresentation> representations = new ArrayList<>();
        processEngine = ProcessEngines.getDefaultProcessEngine();

        List<HistoricTaskInstance> tasks = processEngine
                .getHistoryService()
                .createHistoricTaskInstanceQuery()
                .orderByTaskCreateTime()
                .processInstanceBusinessKey(VERSION_CONTROL_KEY)
                .desc()
                .listPage((request.getPageNumber() - 1) * 10, request.getPageSize());

        if (tasks == null) {
            throw new Exception("Aradığınız kriterde bekleyen operation yoktur");
        }

        tasks.forEach(task -> {
            TaskRepresentation representation = new TaskRepresentation();
            HistoricVariableInstance map = processEngine.getHistoryService()
                    .createHistoricVariableInstanceQuery()
                    .taskId(task.getId())
                    .variableName(OperationVariables.LOCAL_STATUS.name())
                    .singleResult();

            representation.setOperationId(task.getId());
            representation.setOperationName(task.getName());
            representation.setOperationStatus((String) map.getValue());
            representation.setCreatedDate(task.getCreateTime());
            representation.setCreatedBy("TESTING / UserName will add");

            representations.add(representation);
        });

        return representations;
    }


    //todo: approved veya denied olmus bir taskın detayı getirilecek mi ?
    @Override
    @SneakyThrows
    public TaskDetailRepresentation getTaskDetail(String taskId) {

        processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoricTaskInstance task = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskId(taskId)
                .processInstanceBusinessKey(VERSION_CONTROL_KEY)
                .singleResult();

        if (task == null) {
            throw new Exception("Girdiginiz taskId degerinde Operation bulunamamistir");
        }

        List<HistoricVariableInstance> variable = processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .taskId(taskId)
                .list();

        TaskDetailRepresentation representation = new TaskDetailRepresentation();
        variable.forEach(value ->
                setVariableDetail(value, representation));
        setTaskDetail(task, representation);

        return representation;
    }

    @Override
    @SneakyThrows
    public TaskRepresentation answerOperation(AnswerRequest answerRequest) {
        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(VERSION_CONTROL_KEY)
                .taskId(answerRequest.getTaskId())
                .singleResult();

        if (task == null) {
            throw new Exception("Girdiginiz bilgilerde Operation bulunamamistir");
        }

        taskService.setVariable(task.getId(), OperationVariables.STATUS.name(), answerRequest.getOperationStatus().name());
        taskService.setVariable(task.getId(), OperationVariables.LOCAL_STATUS.name(), answerRequest.getOperationStatus().name());

        taskService.complete(task.getId());

        return new TaskRepresentation(task.getId(), task.getName()
                , answerRequest.getOperationStatus().name()
                , "{TEST}"
                , task.getCreateTime());
    }

    private void setTaskDetail(HistoricTaskInstance task, TaskDetailRepresentation taskDetailRepresentation) {
        taskDetailRepresentation.setOperationId(task.getId());
        taskDetailRepresentation.setOperationName(task.getName());
        taskDetailRepresentation.setCreatedDate(task.getCreateTime());
        taskDetailRepresentation.setOperationId(task.getId());
    }

    private void setVariableDetail(HistoricVariableInstance detail, TaskDetailRepresentation representation) {
        switch (detail.getVariableName()) {
            case "LOCAL_STATUS" -> representation.setOperationStatus((String) detail.getValue());
            case "USER_NAME" -> representation.setOperationName((String) detail.getValue());
            case "TV1" -> representation.setValue1((String) detail.getValue());
            case "TV2" -> representation.setValue2((String) detail.getValue());
        }
    }

    private void changeStatusKeyValueForLocal(Map<String, Object> map) {
        Object delegate = map.get(OperationVariables.DELEGATE_EXPRESSION.name());

        map.put(OperationVariables.LOCAL_STATUS.name(), OperationStatus.PENDING.name());
        map.put(OperationVariables.LOCAL_DELEGATE_EXPRESSION.name(), delegate);
    }
}
