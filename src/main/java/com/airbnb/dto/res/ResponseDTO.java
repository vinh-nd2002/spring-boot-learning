package com.airbnb.dto.res;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseDTO {

    private String message;
    private Object data;

}
