package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.SettlementDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private ProcessService processService;

    @Override
    public TaskRepresentation createSettlementOperation() {

        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.USER_NAME.name(), "Settlement User");
        variables.put(OperationVariables.DELEGATE_EXPRESSION.name(), new SettlementDelegateService());
        variables.put("TV1", "SettlementDetay1");
        variables.put("TV2", "SettlementDetay2");

        return processService.startProcess(variables, "settlementFlow");
    }

    @Override
    public TaskRepresentation makeSettlement() {
        System.out.println("Settlement was maked");
        return null;
    }
}
