package com.madnan.pfbackenddemo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailRepresentation extends TaskRepresentation {
    private String value1;
    private String value2;
}
