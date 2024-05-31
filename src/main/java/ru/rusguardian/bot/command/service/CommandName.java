package ru.rusguardian.bot.command.service;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum CommandName {

    START("/start"),
    ADMIN("/admin"),
    NOT_FOUND(null, "/notFound"),
    EMPTY(null, "/empty"),
    MAIN_MENU("\uD83C\uDFE0 В главное меню", "/mainMenu"),
    ERROR("ERROR", "/error"),
    TEXT_COMMAND_DISTRIBUTOR(),

    //START
    WELCOME(),
    GPT_ROLES_VIEW("\uD83C\uDFAD GPT - Роли"),
    GPT_ROLES_BLIND_D(GPT_ROLES_VIEW.viewName),
    MY_ACCOUNT("\uD83D\uDC64 Мой аккаунт"),
    SUBSCRIPTION_VIEW("\uD83D\uDE80 Премиум"),
    SUBSCRIPTION_BLIND_D(SUBSCRIPTION_VIEW.viewName),
    SETTINGS_VIEW("⚙\uFE0F Настройки"),
    SETTINGS_BLIND(SETTINGS_VIEW.viewName),

    //SUBSCRIPTION
    BUY_SEPARATE_BLIND("⭐\uFE0F Купить запросы отдельно"),
    BUY_GPT_4_BLIND("\uD83E\uDD16 Купить GPT-4"),
    BUY_IMAGE_BLIND("\uD83C\uDF06 Купить Midjourney/SD/DALLE 3"),
    BUY_CLAUDE_BLIND("\uD83E\uDDE0 Купить токены Claude"),
    BUY_SUNO_BLIND("\uD83C\uDFB8 Купить песни Suno"),

    BUY_SUBSCRIPTION_BLIND_D(),

    //SETTINGS
    CHOOSE_AI_MODEL_BLIND_D(null),
    CHOOSE_AI_ROLE_BLIND(null),
    CHOOSE_TEMPERATURE_BLIND(null),
    SWITCH_CONTEXT_BLIND(null),
    SWITCH_VOICE_RESPONSE_BLIND(null),
    INTERFACE_LANGUAGE_BLIND(null),

    //CHOOSE_TEMPERATURE
    SET_TEMPERATURE_BLIND_D(null),

    //PURCHASE
    CHS_SEP_PURCH_TYPE_BLIND_D(),
    CHS_SUBS_PURCH_TYPE_BLIND_D(),

    //CHOOSE_AI_MODEL_BLIND_D()
    CHANGE_AI_MODEL_BLIND(),

    //PURCHASE_TYPE
    PURCH_SEP_RUS_BLIND_D("\uD83C\uDDF7\uD83C\uDDFA \uD83D\uDCB3 РФ Банковские карты"),
    PURCH_SEP_INTERN_BLIND_D("\uD83C\uDF0F \uD83D\uDCB3 Международные карты"),
    PURCH_SEP_CRYPTO_BLIND_D("\uD83D\uDD11 Криптовалюта"),

    PURCH_SUBS_RUS_BLIND_D("\uD83C\uDDF7\uD83C\uDDFA \uD83D\uDCB3 РФ Банковские карты"),
    PURCH_SUBS_INTERN_BLIND_D("\uD83C\uDF0F \uD83D\uDCB3 Международные карты"),
    PURCH_SUBS_CRYPTO_BLIND_D("\uD83D\uDD11 Криптовалюта"),

    //MY_ACCOUNT
    //SETTINGS(),
    PARTNER_CABINET_VIEW("\uD83D\uDCBC Кабинет Партнера"),
    PARTNER_CABINET_BLIND(PARTNER_CABINET_VIEW.viewName),

    //PARTNER
    INVITE_WITH_BUTTON_BLIND("✉\uFE0F Приглашение с кнопкой"),
    CASH_OUT_BLIND("\uD83D\uDCE5 Вывести"),

    //SUPPORT_BUTTON(),
    BUY_PREMIUM("\uD83D\uDE80 Купить Премиум"),

    //GPT_ROLES_PROCESS
    PROCESS_ASSISTANT_TYPE_CHANGE(),

    //PROMPTS
    PROMPT(),
    EXECUTE_TEXT_PROMPT(),
    OBTAIN_IMAGE_PROMPT_VIEW_D("/img"),
    EXECUTE_IMAGE_PROMPT_BLIND_D(),
    EXECUTE_VOICE_PROMPT(),
    OBTAIN_VISION_PROMPT_VIEW_D("/vision"),
    EXECUTE_VISION_PROMPT(),

    //ADMIN
    GET_INPUT_FILE_ID_CHAIN("Получить id файла"),
    ASK_INPUT_FILE(),
    RETURN_INPUT_FILE_ID(),

    SEND_MESSAGE_TO_USER_CHAIN("Отправить сообщение пользователю"),
    ASK_USER_ID(),
    ASK_MESSAGE(),
    CHECK_MESSAGE_TO_USER(),
    SEND_MESSAGE_TO_USER(),

    GIVE_ME_MESSAGE_FOR_MAILING(),
    CHECK_MAILING_MESSAGE(),
    CONFIRM_MAILING(),

    GET_INVOICE_LINK("Получить ссылку для оплаты", "/getInvoiceLink"),
    ASK_PRICE_FOR_INVOICE(null),
    ASK_INFO_FOR_INVOICE(null),
    CONFIRM_ORDER(null),
    GET_ORDER_WITH_LINK(null),

    BACK("⬅\uFE0F Назад");


    private final String viewName;
    private final String blindName;
    private static final Map<String, CommandName> BLIND_NAME_MAP;

    static {
        BLIND_NAME_MAP = new HashMap<>();
        for (CommandName c : CommandName.values()) {
            if (c.getBlindName() != null) BLIND_NAME_MAP.put(c.blindName, c);
        }
    }

    CommandName(String viewName, String blindName) {
        this.viewName = viewName;
        this.blindName = blindName;
    }

    CommandName(String commandName) {
        this.viewName = commandName;
        this.blindName = this.name();
    }

    CommandName() {
        this.viewName = null;
        this.blindName = this.name();
    }

    public static CommandName getByBlind(String blindName) {
        return BLIND_NAME_MAP.get(blindName);
    }

}
