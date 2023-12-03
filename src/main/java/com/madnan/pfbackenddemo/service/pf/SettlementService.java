package com.madnan.pfbackenddemo.service.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;

public interface SettlementService {

    TaskRepresentation createSettlementOperation();

    TaskRepresentation makeSettlement();
}
