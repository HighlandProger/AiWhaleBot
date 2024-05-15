package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.constant.user.PartnerLevel;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.*;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.SubscriptionInfoService;
import ru.rusguardian.telegram.bot.util.util.TelegramStartInfoUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.rusguardian.constant.ai.AILanguage.RUSSIAN;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessChatCreationService {

    private static final String INVITED_BY = "invitedBy";
    private final SubscriptionInfoService subscriptionInfoService;
    private final ChatService chatService;

    @Transactional
    public Chat process(Update update) {

        Chat chat = new Chat();
        chat.setId(TelegramUtils.getChatId(update));
        chat.setUsername(TelegramUtils.getUsername(update));
        chat.setTelegramFirstName(TelegramUtils.getFirstname(update));
        chat.setTelegramLastName(TelegramUtils.getLastname(update));
        chat.setRegistrationTime(LocalDateTime.now());
        chat.setAdmin(false);
        chat.setStartInfo(TelegramUtils.getStartInfo(update));
        chat.setTrafficLink(TelegramStartInfoUtils.getTrafficLink(update).orElse(null));
        chat.setCampaign(TelegramStartInfoUtils.getCampaign(update).orElse(null));

        chat.setAiSettingsEmbedded(getAiSetting());
        chat.setSubscriptionEmbedded(getSubscription());
        chat.setPartnerEmbeddedInfo(getPartner(update));
        chat.setUserBalanceEmbedded(getUserBalance());

        return chatService.save(chat);
    }

    private AISettingsEmbedded getAiSetting() {
        AISettingsEmbedded aiSettingsEmbedded = new AISettingsEmbedded();
        aiSettingsEmbedded.setAssistantRole(AssistantRole.USUAL);
        aiSettingsEmbedded.setAiActiveModel(AIModel.GPT_3_5_TURBO);
        aiSettingsEmbedded.setTemperature(1.0f);
        aiSettingsEmbedded.setAiLanguage(RUSSIAN);
        aiSettingsEmbedded.setContextEnabled(true);
        aiSettingsEmbedded.setVoiceResponseEnabled(false);
        return aiSettingsEmbedded;
    }

    private SubscriptionEmbedded getSubscription() {
        SubscriptionInfo info = subscriptionInfoService.findById(SubscriptionType.FREE);

        SubscriptionEmbedded subscription = new SubscriptionEmbedded();
        subscription.setSubscriptionInfo(info);
        return subscription;
    }

    private PartnerEmbedded getPartner(Update update) {

        PartnerEmbedded partner = new PartnerEmbedded();
        partner.setPartnerLevel(PartnerLevel.STANDARD);
        partner.setBalance(0);
        partner.setInvitedBy(getInvitedBy(update));
        partner.setReferralsCount(0);
        partner.setReferralsPurchaseValue(0);
        partner.setReferralsExtraPurchaseCount(0);
        partner.setReferralsSubscriptionsPurchaseCount(0);

        return partner;
    }

    private Chat getInvitedBy(Update update) {
        Optional<String> invitedByStringOptional = TelegramStartInfoUtils.getValueByKeyFromStartInfo(update, INVITED_BY);
        if (invitedByStringOptional.isPresent()) {
            String invitedByString = invitedByStringOptional.get();
            Long invitedById;
            try {
                invitedById = Long.parseLong(invitedByString);
            } catch (NumberFormatException e) {
                log.error("FORMATTING EXCEPTION for invited by in start info {}", invitedByString);
                return null;
            }
            Optional<Chat> chatOptional = chatService.findByIdOptional(invitedById);
            if (chatOptional.isEmpty()) {
                log.error("USER with id {} not found. Start info {}", TelegramUtils.getStartInfo(update));
                return null;
            }
            return chatOptional.get();
        }
        return null;
    }

    private UserBalanceEmbedded getUserBalance() {
        UserBalanceEmbedded userBalance = new UserBalanceEmbedded();
        userBalance.setClaudeTokens(0);
        userBalance.setExtraImageRequests(0);
        userBalance.setExtraGPT4Requests(0);
        userBalance.setExtraSunoRequests(0);

        return userBalance;
    }
}
