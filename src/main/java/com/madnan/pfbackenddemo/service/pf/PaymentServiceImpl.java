package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.PaymentDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ProcessService processService;

    @Override
    public TaskRepresentation createPaymentOperation() {

        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.USER_NAME.name(), "Payment User");
        variables.put(OperationVariables.DELEGATE_EXPRESSION.name(), new PaymentDelegateService());
        variables.put("TV1", "PaymentDetay1");
        variables.put("TV2", "PaymentDetay2");

        return processService.startProcess(variables, "paymentFlow");
    }

    @Override
    public TaskRepresentation makePayment() {
        System.out.println("payment was maked");
        return null;
    }


}
