package ru.rusguardian.service.translate.dto.get_iam_token;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GetIAMTokenResponseDto {

    private String iamToken;
    private String expiresAt;
}
