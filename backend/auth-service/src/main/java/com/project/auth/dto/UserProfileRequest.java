
package com.project.auth.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserProfileRequest(

        @Size(max = 150, message = "Họ tên không được vượt quá 150 ký tự") String fullName,

        @Past(message = "Ngày sinh phải nằm trong quá khứ") LocalDate dateOfBirth,

        @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự") String phone,

        @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự") String address,

        @Size(max = 30, message = "Giới tính không được vượt quá 30 ký tự") String gender,

        @Size(max = 100, message = "Quốc tịch không được vượt quá 100 ký tự") String nationality,

        @Size(max = 50, message = "Số giấy tờ không được vượt quá 50 ký tự") String identityNumber,

        @Size(max = 500, message = "URL ảnh đại diện không được vượt quá 500 ký tự") String avatarUrl) {
}
