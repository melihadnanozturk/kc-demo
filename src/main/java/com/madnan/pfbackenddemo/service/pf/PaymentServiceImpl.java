package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.PaymentDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ProcessService processService;

    @Override
    public TaskRepresentation createPaymentOperation() {
        return processService.startProcess(new PaymentDelegateService(), "paymentOperation", null);
    }

    @Override
    public TaskRepresentation makePayment() {
        System.out.println("payment was maked");
        return null;
    }


}
