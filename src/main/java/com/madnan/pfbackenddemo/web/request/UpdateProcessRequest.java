package com.madnan.pfbackenddemo.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProcessRequest {
    private String olderProcessKey;
    private String processName;
}
