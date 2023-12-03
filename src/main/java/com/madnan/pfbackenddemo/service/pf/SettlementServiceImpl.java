package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.SettlementDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private ProcessService processService;

    @Override
    public TaskRepresentation createSettlementOperation() {
        return processService.startProcess(new SettlementDelegateService(), "settlementOperation", null);
    }

    @Override
    public TaskRepresentation makeSettlement() {
        return null;
    }
}
