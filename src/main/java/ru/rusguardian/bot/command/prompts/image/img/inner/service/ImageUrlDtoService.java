package ru.rusguardian.bot.command.prompts.image.img.inner.service;


import java.util.HashMap;
import java.util.Map;

public class ImageUrlDtoService {

    private ImageUrlDtoService(){}

    private static final Map<Long, String> chatImageUrlMap = new HashMap<>();

    public static void addImageUrl(Long chatId, String imageUrl) {
        chatImageUrlMap.put(chatId, imageUrl);
    }

    public static String getImageUrlAndRemove(Long chatId) {
        String url = chatImageUrlMap.get(chatId);
        chatImageUrlMap.remove(chatId);
        return url;
    }

}
