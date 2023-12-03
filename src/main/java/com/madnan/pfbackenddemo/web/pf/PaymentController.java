/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.madnan.pfbackenddemo.web.pf;

import com.madnan.pfbackenddemo.model.TaskRepresentation;
import com.madnan.pfbackenddemo.service.pf.PaymentService;
import com.madnan.pfbackenddemo.web.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping()
    public BaseResponse<Object> getPayment() {
        String message = "1-) Test Payments : 39.90";

        return BaseResponse.builder().success(true).data(message).build();
    }

    @GetMapping("/make")
    public BaseResponse<Object> makePaymentOperation() {
        TaskRepresentation data = paymentService.createPaymentOperation();

        return BaseResponse.builder()
                .success(true)
                .data(data)
                .build();
    }

    @GetMapping("/remove")
    public BaseResponse<Object> removePayment() {
        String message = "Payment was removed ";

        return BaseResponse.builder().success(true).data(message).build();
    }
}
