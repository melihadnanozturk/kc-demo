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
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
                .startProcessInstanceByKey(flowName, variables);
        processEngine = ProcessEngines.getDefaultProcessEngine();

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
        processEngine = ProcessEngines.getDefaultProcessEngine();
        List<TaskRepresentation> representations = new ArrayList<>();
        List<HistoricTaskInstance> tasks = filterTask(request)
                .listPage((request.getPageNumber() - 1) * 10, request.getPageSize());

        if (tasks == null) {
            throw new Exception("Aradığınız kriterde bekleyen operation yoktur");
        }

        tasks.forEach(task -> {
            TaskRepresentation taskRepresentation = setVariables(task);
            representations.add(taskRepresentation);
        });

        return representations;
    }

    //todo: approved veya denied olmus bir taskın detayı getirilecek mi ?
    @Override
    @SneakyThrows
    public TaskDetailRepresentation getTaskDetail(String taskId) {
        processEngine = ProcessEngines.getDefaultProcessEngine();

        HistoricTaskInstance task = getHistoricalQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new Exception("Girdiginiz taskId degerinde Operation bulunamamistir");
        }

        return setTaskDetail(task);
    }

    @Override
    @SneakyThrows
    public TaskRepresentation answerOperation(AnswerRequest answerRequest) {
        Task task = taskService.createTaskQuery()
                //.processInstanceBusinessKey(VERSION_CONTROL_KEY)
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

    private void changeStatusKeyValueForLocal(Map<String, Object> map) {
        Object delegate = map.get(OperationVariables.DELEGATE_EXPRESSION.name());

        map.put(OperationVariables.LOCAL_STATUS.name(), OperationStatus.PENDING.name());
        map.put(OperationVariables.LOCAL_DELEGATE_EXPRESSION.name(), delegate);
    }


    private HistoricTaskInstanceQuery getHistoricalQuery() {
        processEngine = ProcessEngines.getDefaultProcessEngine();

        return processEngine
                .getHistoryService()
                .createHistoricTaskInstanceQuery()
                //.processInstanceBusinessKey(VERSION_CONTROL_KEY)
                .includeTaskLocalVariables();
    }


    private HistoricTaskInstanceQuery filterTask(OperationRequest request) {
        var query = getHistoricalQuery().orderByTaskCreateTime().desc();

        if (StringUtils.hasText(request.getOperationName())) {
            query.taskName(request.getOperationName());
        }
        if (StringUtils.hasText(request.getOperationId())) {
            query.taskId(request.getOperationId());
        }
        if (StringUtils.hasText(request.getRefId())) {
            //query.taskVariableValueEquals(OperationVariables.REF_ID.name(),request.getRefId());
        }
        if (request.getOperationStatus() != null) {
            query.taskVariableValueEquals(OperationVariables.LOCAL_STATUS.name(), request.getOperationStatus().name());
        }

        //return query.processInstanceBusinessKey(VERSION_CONTROL_KEY);
        return query;
    }

    //todo: refactor
    private TaskRepresentation setVariables(HistoricTaskInstance task) {
        return TaskRepresentation.builder()
                .operationId(task.getId())
                .operationName(task.getName())
                .operationStatus((String) task.getTaskLocalVariables().get(OperationVariables.LOCAL_STATUS.name()))
                .createdDate(task.getCreateTime())
                .createdBy("TESTING / UserName will add")
                .build();
    }

    //todo: refactor
    private TaskDetailRepresentation setTaskDetail(HistoricTaskInstance task) {
        Map<String, Object> variables = task.getTaskLocalVariables();

        TaskDetailRepresentation taskRepresentation = new TaskDetailRepresentation();
        taskRepresentation.setOperationStatus((String) variables.get(OperationVariables.STATUS.name()));
        taskRepresentation.setOperationId(task.getId());
        taskRepresentation.setOperationName(task.getName());
        taskRepresentation.setCreatedDate(task.getCreateTime());
        taskRepresentation.setCreatedBy("Will be added");
        taskRepresentation.setValue1((String) variables.get("TV1"));
        taskRepresentation.setValue2((String) variables.get("TV1"));

        return taskRepresentation;
    }
}
