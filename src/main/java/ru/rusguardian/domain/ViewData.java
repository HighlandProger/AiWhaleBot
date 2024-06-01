package ru.rusguardian.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(schema = "ncs_bot", name = "view_data")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ViewData {

    @Id
    private String name;
    @Column(name = "ru_value")
    private String ruValue;
    @Column(name = "en_value")
    private String enValue;
    @Column(name = "de_value")
    private String deValue;
    @Column(name = "uz_value")
    private String uzValue;
}
