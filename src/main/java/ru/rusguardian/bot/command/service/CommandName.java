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

    //START
    WELCOME(),
    GPT_ROLES_VIEW("\uD83C\uDFAD GPT - Роли"),
    GPT_ROLES_BLIND(null, "GPT-ROLES-BLIND"),
    MY_ACCOUNT("\uD83D\uDC64 Мой аккаунт"),
    PREMIUM_SUBSCRIPTION("\uD83D\uDE80 Премиум"),
    SETTINGS("⚙\uFE0F Настройки"),

    //GPT_ROLES_PROCESS
    PROCESS_ASSISTANT_TYPE_CHANGE(),
    //PROMPTS
    PROMPT(),
    EXECUTE_TEXT_PROMPT(),
    EXECUTE_VOICE_PROMPT(),

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
    GET_ORDER_WITH_LINK(null);


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
