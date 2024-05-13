package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(schema = "ncs_bot", name = "ai_user_requests")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiUserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
