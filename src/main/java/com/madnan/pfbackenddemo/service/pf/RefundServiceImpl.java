package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.ProcessService;
import com.madnan.pfbackenddemo.service.pf.delegate.RefundDelegateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private ProcessService processService;

    @Override
    public TaskRepresentation createRefundOperation() {
        return processService.startProcess(new RefundDelegateService(), "refundOperation", null);
    }

    @Override
    public TaskRepresentation makeRefund() {
        System.out.println("refund was maked");
        return null;
    }


}
