package com.madnan.pfbackenddemo.service.pf.delegate;

import com.madnan.pfbackenddemo.service.pf.RefundServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.delegate.ActivityBehavior;
import org.springframework.stereotype.Service;

@Service
public class RefundApprovedDelegateService implements ActivityBehavior {


    @Override
    public void execute(DelegateExecution execution) {
        RefundServiceImpl refundService = new RefundServiceImpl();
        //todo: burada gerekli operetion bilgisi verilir.
        refundService.makeRefund();
    }
}
