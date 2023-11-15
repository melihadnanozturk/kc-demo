package com.madnan.pfbackenddemo.web.merchant;

import com.madnan.pfbackenddemo.web.BaseResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchant/user")
@CrossOrigin("*")
public class MerchantUserController {

    @GetMapping()
    public BaseResponse<Object> getAllUser() {
        String message = "User-1 => melih@thy.com";

        return BaseResponse.builder().success(true).data(message).build();
    }

    @GetMapping("/create")
    public BaseResponse<Object> createUser() {
        String message = "New User created successfully";

        return BaseResponse.builder().success(true).data(message).build();
    }

    @GetMapping("/remove")
    public BaseResponse<Object> removeUser() {
        String message = "User removed successfully";

        return BaseResponse.builder().success(true).data(message).build();
    }


}
