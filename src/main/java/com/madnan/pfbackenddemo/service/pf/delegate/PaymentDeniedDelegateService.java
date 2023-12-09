package com.madnan.pfbackenddemo.service.pf.delegate;

import com.madnan.pfbackenddemo.service.pf.PaymentServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.delegate.ActivityBehavior;
import org.springframework.stereotype.Service;

@Service
public class PaymentDeniedDelegateService implements ActivityBehavior {


    @Override
    public void execute(DelegateExecution execution) {
        PaymentServiceImpl paymentService = new PaymentServiceImpl();
        //todo: burada gerekli operetion bilgisi verilir.
        paymentService.deniedPayment();
    }
}
