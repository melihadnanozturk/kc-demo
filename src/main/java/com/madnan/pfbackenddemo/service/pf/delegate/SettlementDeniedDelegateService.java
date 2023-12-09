package com.madnan.pfbackenddemo.service.pf.delegate;

import com.madnan.pfbackenddemo.service.pf.SettlementServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.delegate.ActivityBehavior;
import org.springframework.stereotype.Service;

@Service
public class SettlementDeniedDelegateService implements ActivityBehavior {


    @Override
    public void execute(DelegateExecution execution) {
        SettlementServiceImpl settlementService = new SettlementServiceImpl();
        //todo: burada gerekli operetion bilgisi verilir.
        settlementService.deniedSettlement();
    }
}
