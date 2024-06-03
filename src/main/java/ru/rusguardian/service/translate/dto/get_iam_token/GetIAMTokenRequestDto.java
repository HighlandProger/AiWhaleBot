package ru.rusguardian.service.translate.dto.get_iam_token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetIAMTokenRequestDto {

    private String yandexPassportOauthToken;
}
