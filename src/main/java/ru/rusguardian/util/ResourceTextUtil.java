package ru.rusguardian.util;

import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

public class ResourceTextUtil {

    private ResourceTextUtil(){}

    public static String getTextByViewDataAndChatLanguage(String filePath, Chat chat) {
        String path = filePath + chat.getAiSettingsEmbedded().getAiLanguage().getValue() + ".txt";
        return FileUtils.getTextFromFile(FileUtils.getFileFromResources2(chat, path));
    }
}
