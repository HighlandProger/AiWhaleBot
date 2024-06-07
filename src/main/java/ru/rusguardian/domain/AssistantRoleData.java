package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.constant.ai.AILanguage;

@Entity
@Table(schema = "ncs_bot", name = "assistant_role_data")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class AssistantRoleData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private AILanguage language;
    @Column(name = "view_name")
    private String viewName;
    @Column(name = "description")
    private String description;
}
