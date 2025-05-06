package com.airbnb.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginDTO {
    @NotNull(message = "Email không được để trống")
    private String email;

    @NotNull(message = "Mật khẩu không được để trống")
    // @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Mật
    // khẩu phải có ít nhất 8 ký tự, bao gồm chữ cái và số")
    private String password;
}
