package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;

public interface PaymentService {

    TaskRepresentation createPaymentOperation();

    void makePayment();

    void deniedPayment();
}
