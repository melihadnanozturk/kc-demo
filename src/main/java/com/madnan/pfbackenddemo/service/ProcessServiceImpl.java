package com.madnan.pfbackenddemo.service;

import com.madnan.pfbackenddemo.model.OperationStatus;
import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskDetailRepresentation;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.web.request.AnswerRequest;
import com.madnan.pfbackenddemo.web.request.OperationRequest;
import com.madnan.pfbackenddemo.web.request.UpdateProcessRequest;
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

    @Transactional
    @SneakyThrows
    public TaskRepresentation startProcess(Map<String, Object> variables, String processName, String processKey) {
        setVariablesForProcess(variables, processKey);
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(processName, processKey, variables);

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .orderByTaskCreateTime()
                .desc().singleResult();

        changeVariablesForLocal(variables);
        taskService.setVariablesLocal(task.getId(), variables);

        Map<String, Object> taskVariables = taskService.getVariables(task.getId());

        return new TaskRepresentation(
                (String) taskVariables.get(OperationVariables.REF_ID.name()),
                task.getName(),
                (String) taskVariables.get(OperationVariables.LOCAL_STATUS.name()),
                task.getId(),
                (String) variables.get(OperationVariables.USER_NAME.name()),
                task.getCreateTime());
    }

    @Override
    @SneakyThrows
    public List<TaskRepresentation> getAllTasks(OperationRequest request) {
        List<TaskRepresentation> representations = new ArrayList<>();
        List<HistoricTaskInstance> tasks = filterTask(request)
                .listPage((request.getPageNumber() - 1) * 10, request.getPageSize());

        tasks.forEach(task -> {
            TaskRepresentation taskRepresentation = setVariables(task);
            representations.add(taskRepresentation);
        });

        return representations;
    }

    @Override
    @SneakyThrows
    public TaskDetailRepresentation getTaskDetail(String taskId) {
        HistoricTaskInstance task = getHistoricalQuery().taskId(taskId)
                .includeProcessVariables()
                .singleResult();

        if (task == null) {
            throw new Exception("Girdiginiz taskId degerinde Operation bulunamamistir");
        }

        return setTaskDetail(task);
    }

    @Override
    @SneakyThrows
    public TaskRepresentation answerOperation(AnswerRequest answerRequest) {
        Task task = taskService.createTaskQuery()
                .processInstanceBusinessKey(answerRequest.getProcessKey())
                .taskId(answerRequest.getTaskId())
                .includeProcessVariables()
                .singleResult();

        if (task == null) {
            throw new Exception("Girdiginiz bilgilerde Operation bulunamamistir");
        }

        taskService.setVariable(task.getId(), OperationVariables.STATUS.name(), answerRequest.getOperationStatus().name());
        taskService.setVariable(task.getId(), OperationVariables.LOCAL_STATUS.name(), answerRequest.getOperationStatus().name());

        Map<String, Object> variables = taskService.getVariables(task.getId());

        taskService.complete(task.getId());

        return new TaskRepresentation(
                (String) variables.get(OperationVariables.REF_ID.name())
                , task.getName()
                , (String) variables.get(OperationVariables.STATUS.name())
                , task.getId()
                , (String) variables.get(OperationVariables.USER_NAME.name())
                , task.getCreateTime());
    }

    @Override
    public String updateProcessVersion(UpdateProcessRequest request) {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        List<ProcessInstance> processInstances = runtimeService
                .createProcessInstanceQuery()
                .processInstanceBusinessKey(request.getOlderProcessKey())
                .list();

        processInstances.forEach(instance -> {
            List<Task> taskList = taskService
                    .createTaskQuery()
                    .processInstanceId(instance.getProcessInstanceId()).list();

            taskList.forEach(task -> {
                taskService.setVariable(task.getId(), OperationVariables.STATUS.name(), OperationStatus.CANCEL.name());
                taskService.complete(task.getId());
            });

        });

        return null;
    }

    private void changeVariablesForLocal(Map<String, Object> map) {
        Object delegate = map.get(OperationVariables.DENIED_EXPRESSION.name());

        map.remove(OperationVariables.STATUS.name());
        map.remove(OperationVariables.APPROVED_EXPRESSION.name());
        map.put(OperationVariables.LOCAL_STATUS.name(), OperationStatus.PENDING.name());
        map.put(OperationVariables.LOCAL_APPROVED_EXPRESSION.name(), delegate);
    }

    private void setVariablesForProcess(Map<String, Object> map, String processKey) {
        map.put(OperationVariables.STATUS.name(), OperationStatus.PENDING.name());
        map.put(OperationVariables.PROCESSS_KEY.name(), processKey);
    }


    private HistoricTaskInstanceQuery getHistoricalQuery() {
        processEngine = ProcessEngines.getDefaultProcessEngine();

        return processEngine
                .getHistoryService()
                .createHistoricTaskInstanceQuery()
                .includeProcessVariables();
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
            query.taskVariableValueEquals(OperationVariables.REF_ID.name(), request.getRefId());
        }
        /*if (request.getOperationStatus() != null) {
            query.taskVariableValueEquals(OperationVariables.STATUS.name(), request.getOperationStatus().name());
        }*/

        return query;
    }

    private TaskRepresentation setVariables(HistoricTaskInstance task) {
        return TaskRepresentation.builder()
                .refId((String) task.getProcessVariables().get(OperationVariables.REF_ID.name()))
                .operationName(task.getName())
                .operationStatus((String) task.getProcessVariables().get(OperationVariables.STATUS.name()))
                .operationId(task.getId())
                .createdDate(task.getCreateTime())
                .createdBy((String) task.getProcessVariables().get(OperationVariables.USER_NAME.name()))
                .build();
    }

    private TaskDetailRepresentation setTaskDetail(HistoricTaskInstance task) {
        Map<String, Object> variables = task.getProcessVariables();

        TaskDetailRepresentation taskRepresentation = new TaskDetailRepresentation();
        taskRepresentation.setRefId((String) variables.get(OperationVariables.REF_ID.name()));
        taskRepresentation.setOperationName(task.getName());
        taskRepresentation.setOperationStatus((String) variables.get(OperationVariables.STATUS.name()));
        taskRepresentation.setProcessKey((String) variables.get(OperationVariables.PROCESSS_KEY.name()));
        taskRepresentation.setOperationId(task.getId());
        taskRepresentation.setCreatedDate(task.getCreateTime());
        taskRepresentation.setCreatedBy((String) variables.get(OperationVariables.USER_NAME.name()));

        return taskRepresentation;
    }
}
