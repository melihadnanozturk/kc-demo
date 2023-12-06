package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.RefundDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private ProcessService processService;

    @Override
    public TaskRepresentation createRefundOperation() {

        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.USER_NAME.name(), "Refund User");
        variables.put(OperationVariables.DELEGATE_EXPRESSION.name(), new RefundDelegateService());
        variables.put("TV1", "RefundDetay1");
        variables.put("TV2", "RefundDetay2");

        return processService.startProcess(variables, "refundFlow");
    }

    @Override
    public TaskRepresentation makeRefund() {
        System.out.println("refund was maked");
        return null;
    }


}
