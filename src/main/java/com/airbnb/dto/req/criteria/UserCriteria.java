package com.airbnb.dto.req.criteria;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCriteria {
    private Set<Long> ids;
    private String name;
    private String email;
}
