package ru.rusguardian.service.process.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;

@Service
@RequiredArgsConstructor
public class ProcessCheckChatChannelsSubscription {

    //TODO functional
    public boolean check(Chat chat) {
        return false;
    }
}
