package org.example.contract;

import org.example.dto.ChatRequest;
import org.example.dto.ChatResponse;

/**
 * Core chat contract used as the stable baseline interface for Money Tree.
 */
public interface CoreChatContract {

    ChatResponse chat(ChatRequest request);
}
