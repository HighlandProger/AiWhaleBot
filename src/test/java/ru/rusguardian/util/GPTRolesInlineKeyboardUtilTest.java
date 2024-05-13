package ru.rusguardian.util;

import org.junit.jupiter.api.Test;
import ru.rusguardian.constant.ai.AssistantRole;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GPTRolesInlineKeyboardUtilTest {

    @Test
    void getPaged() {
        List<AssistantRole> paged = GPTRolesInlineKeyboardUtil.getPaged(0);
        assertTrue(paged.contains(AssistantRole.USUAL));
        assertFalse(paged.contains(AssistantRole.DEISTVUET_KAK_SEO));
    }

    @Test
    void getPageString() {
        String expected = "GPT-ROLES-BLIND_PAGE_4";
        String actual = GPTRolesInlineKeyboardUtil.getPageString(4);
        assertEquals(expected, actual);
    }

}