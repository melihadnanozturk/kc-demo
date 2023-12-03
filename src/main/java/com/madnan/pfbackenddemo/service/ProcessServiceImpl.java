package com.madnan.pfbackenddemo.service;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.web.request.AnswerRequest;
import com.madnan.pfbackenddemo.web.request.OperationRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.delegate.ActivityBehavior;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    @Transactional
    @SneakyThrows
    public TaskRepresentation startProcess(ActivityBehavior delegateService, String taskName, String userName) {
        Map<String, Object> variables = setOperationVariables(taskName, userName, delegateService);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("makerChecker", variables);

        //todo: burada birden fazla istek atılırsa hata fırlatma mekanizması ekle

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .orderByTaskCreateTime()
                .desc().singleResult();
        //checkOperation(processInstance);

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        Map<String, Object> taskVariables = taskService.getVariables(task.getId());

        return new TaskRepresentation(task.getId(),
                (String) taskVariables.get(OperationVariables.TASK_NAME.name()),
                (String) taskVariables.get(OperationVariables.STATUS.name()),
                task.getCreateTime(),
                task.getDueDate());
    }

    @Override
    public List<TaskRepresentation> getAllTasks(OperationRequest request) {

        List<HistoricTaskInstance> tasks = getQuery(request).list();

        List<TaskRepresentation> dtos = new ArrayList<>();

        tasks.forEach(task -> {
            TaskRepresentation representation = new TaskRepresentation();
            List<HistoricVariableInstance> variables = getTaskVariablesWithInstanceId(task.getProcessInstanceId());
            variables.forEach(value -> setVariables(representation, value));
            representation.setOperationId(task.getId());
            dtos.add(representation);
        });

        return dtos;
    }

    private void setVariables(TaskRepresentation representation, HistoricVariableInstance instance) {
        switch (instance.getVariableName()) {
            case "STATUS" -> representation.setOperationStatus((String) instance.getValue());
            case "TASK_NAME" -> representation.setOperationName((String) instance.getValue());
        }
        representation.setCreatedDate(instance.getCreateTime());
    }

    @Override
    public TaskRepresentation answerOperation(AnswerRequest answerRequest) {
        Task task = taskService.createTaskQuery().taskId(answerRequest.getTaskId()).singleResult();

        Map<String, Object> map = taskService.getVariables(answerRequest.getTaskId());
        map.put(OperationVariables.STATUS.name(), answerRequest.getOperationStatus().name());

        taskService.setVariables(task.getId(), map);
        taskService.complete(answerRequest.getTaskId());

        return new TaskRepresentation(task.getId(), task.getName()
                , answerRequest.getOperationStatus().name()
                , task.getCreateTime()
                , task.getDueDate());
    }

    private List<HistoricVariableInstance> getTaskVariablesWithInstanceId(String processInstanceId) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        return processEngine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
    }


    private HistoricTaskInstanceQuery getQuery(OperationRequest request) {

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        var query = processEngine.getHistoryService().createHistoricTaskInstanceQuery();

        if (StringUtils.hasText(request.getOperationName())) {
            query.taskVariableValueEquals(OperationVariables.TASK_NAME.name(), request.getOperationName());
        }

        if (request.getAfterCreatedTime() != null) {
            query.taskCreatedAfter(request.getAfterCreatedTime());
        }

        if (request.getSortedBy().equals("DESC")) {
            query.orderByTaskCreateTime()
                    .desc();
        } else {
            query.orderByTaskCreateTime()
                    .asc();
        }

        return query;
    }

    private Map<String, Object> setOperationVariables(String taskName, String userName, ActivityBehavior delegateService) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.STATUS.name(), "PENDING");
        variables.put(OperationVariables.TASK_NAME.name(), taskName);
        variables.put(OperationVariables.USER_NAME.name(), userName);
        variables.put(OperationVariables.DELEGATE_EXPRESSION.name(), delegateService);

        return variables;
    }

    //todo: onceden atılan processlerden "PENDING" statusune aship bir process varsa, bunun kontrolunu saglayabilir
    //todo: suanda boyle bir istek olmadigi icin tamamlanmadi
    /*private Task checkOperation(ProcessInstance processInstance) throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<HistoricTaskInstance> instanceList = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processDefinitionId(processInstance.getProcessDefinitionId()).list();

        instanceList.forEach(task -> {
            List<HistoricVariableInstance> variables = processEngine.getHistoryService()
                    .createHistoricVariableInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .list();

            variables.forEach(degisken -> {
                String degiskenName = degisken.getVariableName();
                String o = (String) degisken.getValue();
                System.out.println(o);
            });


            System.out.println(variables);
        });
        return null;
    }*/
}
