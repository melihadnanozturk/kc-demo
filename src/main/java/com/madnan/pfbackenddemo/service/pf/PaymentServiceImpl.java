package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.PaymentApprovedDelegateService;
import com.madnan.pfbackenddemo.service.pf.delegate.PaymentDeniedDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ProcessService processService;

    @Value("${pf.flowable.process-key.Payment}")
    private String processName;

    @Override
    public TaskRepresentation createPaymentOperation() {

        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.USER_NAME.name(), "Payment User");
        variables.put(OperationVariables.REF_ID.name(), "PY-" + createRandomRefId());
        variables.put(OperationVariables.APPROVED_EXPRESSION.name(), new PaymentApprovedDelegateService());
        variables.put(OperationVariables.DENIED_EXPRESSION.name(), new PaymentDeniedDelegateService());

        return processService.startProcess(variables, "PaymentFlow", processName);
    }

    @Override
    public void makePayment() {
        System.out.println("payment was maked");
    }

    @Override
    public void deniedPayment() {
        System.out.println("payment was rejected");
    }

    private String createRandomRefId() {
        Random rand = new Random();
        Integer integer = rand.nextInt(100);
        return integer.toString();
    }


}
