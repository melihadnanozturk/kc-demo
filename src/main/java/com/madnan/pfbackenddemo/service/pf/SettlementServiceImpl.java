package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.OperationVariables;
import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.SettlementApprovedDelegateService;
import com.madnan.pfbackenddemo.service.pf.delegate.SettlementDeniedDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private ProcessService processService;

    @Value("${pf.flowable.process-key.Settlement}")
    private String processName;

    @Override
    public TaskRepresentation createSettlementOperation() {

        Map<String, Object> variables = new HashMap<>();
        variables.put(OperationVariables.USER_NAME.name(), "Settlement User");
        variables.put(OperationVariables.REF_ID.name(), "ST-" + createRandomRefId());
        variables.put(OperationVariables.APPROVED_EXPRESSION.name(), new SettlementApprovedDelegateService());
        variables.put(OperationVariables.DENIED_EXPRESSION.name(), new SettlementDeniedDelegateService());

        return processService.startProcess(variables, "SettlementFlow", processName);
    }

    @Override
    public void makeSettlement() {
        System.out.println("Settlement was maked");
    }

    @Override
    public void deniedSettlement() {
        System.out.println("Settlement was rejected");
    }

    private String createRandomRefId() {
        Random rand = new Random();
        Integer integer = rand.nextInt(100);
        return integer.toString();
    }
}
