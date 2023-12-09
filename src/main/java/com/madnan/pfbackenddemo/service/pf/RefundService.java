package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;

public interface RefundService {

    TaskRepresentation createRefundOperation();

    void makeRefund();

    void deniedRefund();
}
