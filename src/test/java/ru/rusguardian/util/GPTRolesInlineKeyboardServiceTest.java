package ru.rusguardian.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class GPTRolesInlineKeyboardServiceTest {

    @Autowired
    private GPTRolesInlineKeyboardService keyboardService;


    @Test
    void getPageString() {
        String expected = "GPT-ROLES-BLIND_PAGE_4";
        String actual = keyboardService.getPageCallback(4);
        assertEquals(expected, actual);
    }

}