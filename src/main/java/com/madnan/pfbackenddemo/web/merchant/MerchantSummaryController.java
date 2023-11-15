package com.madnan.pfbackenddemo.web.merchant;

import com.madnan.pfbackenddemo.web.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchant/summary")
public class MerchantSummaryController {

    @GetMapping()
    public BaseResponse<Object> getAllSummary() {
        String message = "Merchant Summary : +1M €";

        return BaseResponse.builder().success(true).data(message).build();
    }

    @GetMapping("/create")
    public BaseResponse<Object> importSummary() {
        String message = "New Merchant Summary was sent melih@thy.com";

        return BaseResponse.builder().success(true).data(message).build();
    }

    @GetMapping("/edit")
    public BaseResponse<Object> exportSummary() {
        String message = "Summary filter was changed successfully";

        return BaseResponse.builder().success(true).data(message).build();
    }


}
