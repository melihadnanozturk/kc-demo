package com.madnan.pfbackenddemo.web.flow;

import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.web.request.AnswerRequest;
import com.madnan.pfbackenddemo.web.request.OperationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow")
@RequiredArgsConstructor
public class FlowController {

    private final ProcessService processService;

    @GetMapping("/list")
    public List<TaskRepresentation> getAllTasks(@RequestBody OperationRequest operationRequest) {
        return processService.getAllTasks(operationRequest);

    }

    @PostMapping("/answer")
    public TaskRepresentation answerOperation(@RequestBody AnswerRequest answerRequest) {
        return processService.answerOperation(answerRequest);
    }
}
