package ru.rusguardian.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import ru.rusguardian.constant.ai.AILanguage;

@Entity
@Table(schema = "ncs_bot", name = "button_view_data")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ButtonViewData {

    @Id
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "button_number")
    private int buttonNumber;
    @Column(name = "ru_value")
    private String ruValue;
    @Column(name = "en_value")
    private String enValue;
    @Column(name = "de_value")
    private String deValue;
    @Column(name = "uz_value")
    private String uzValue;

    public String getValueByLanguage(AILanguage language) {
        return switch (language) {
            case RUSSIAN -> ruValue;
            case ENGLISH -> enValue;
            case GERMAN -> deValue;
            case UZBEK -> uzValue;
        };
    }
}
