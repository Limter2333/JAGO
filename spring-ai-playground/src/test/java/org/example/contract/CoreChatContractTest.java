package org.example.contract;

import org.example.service.QwenService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CoreChatContractTest {

    @Test
    void qwenServiceShouldImplementCoreChatContract() {
        assertTrue(CoreChatContract.class.isAssignableFrom(QwenService.class));
    }
}
