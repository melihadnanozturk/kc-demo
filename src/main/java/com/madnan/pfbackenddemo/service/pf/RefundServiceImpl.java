package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.RefundApprovedDelegateService;
import com.madnan.pfbackenddemo.service.pf.delegate.RefundDeniedDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private ProcessService processService;

    @Value("${pf.flowable.process-key.Refund}")
    private String processName;

    @Override
    public TaskRepresentation createRefundOperation() {

        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.USER_NAME.name(), "Refund User");
        variables.put(OperationVariables.REF_ID.name(), "RF-" + createRandomRefId());
        variables.put(OperationVariables.APPROVED_EXPRESSION.name(), new RefundApprovedDelegateService());
        variables.put(OperationVariables.DENIED_EXPRESSION.name(), new RefundDeniedDelegateService());

        return processService.startProcess(variables, "RefundFlow", processName);
    }

    @Override
    public void makeRefund() {
        System.out.println("refund was maked");
    }

    @Override
    public void deniedRefund() {
        System.out.println("refund was rejected");

    }

    private String createRandomRefId() {
        Random rand = new Random();
        Integer integer = rand.nextInt(100);
        return integer.toString();
    }


}
