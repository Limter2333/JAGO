package org.example.skill;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TicketStatusSkill {

    private static final Map<String, String> MOCK_TICKET_STATUS = Map.of(
            "INC-1001", "IN_PROGRESS",
            "INC-1002", "OPEN",
            "INC-1003", "RESOLVED",
            "INC-1004", "CLOSED"
    );

    private static final Map<String, String> NORMALIZED_STATE = Map.of(
            "OPEN", "待处理",
            "IN_PROGRESS", "处理中",
            "RESOLVED", "已解决",
            "CLOSED", "已关闭",
            "UNKNOWN", "未找到"
    );

    @Tool(description = "查询运维工单状态并返回归一化结果")
    public String queryTicketStatus(
            @ToolParam(description = "工单ID，例如: INC-1001") String ticketId
    ) {
        try {
            String normalizedTicketId = normalizeTicketId(ticketId);
            String rawState = lookupRawStatus(normalizedTicketId);
            if (rawState == null) {
                return safeFallback(normalizedTicketId);
            }
            return formatResult(normalizedTicketId, rawState, normalizeState(rawState));
        } catch (IllegalArgumentException ex) {
            return safeFallback("未知工单");
        } catch (RuntimeException ex) {
            String normalizedTicketId = ticketId == null || ticketId.isBlank() ? "未知工单" : ticketId.trim().toUpperCase();
            return timeoutOrErrorFallback(normalizedTicketId);
        }
    }

    protected String lookupRawStatus(String normalizedTicketId) {
        return MOCK_TICKET_STATUS.get(normalizedTicketId);
    }

    private String normalizeTicketId(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("ticketId must not be blank");
        }
        String normalized = ticketId.trim().toUpperCase();
        if (!normalized.matches("^[A-Z]{3}-\\d{4}$")) {
            throw new IllegalArgumentException("ticketId format is invalid");
        }
        return normalized;
    }

    private String normalizeState(String rawState) {
        return NORMALIZED_STATE.getOrDefault(rawState, "未找到");
    }

    private String formatResult(String ticketId, String state, String normalizedState) {
        return String.format("工单:%s;状态:%s;归一化状态:%s", ticketId, state, normalizedState);
    }

    private String safeFallback(String ticketId) {
        return String.format("工单:%s;状态:UNKNOWN;归一化状态:%s", ticketId, NORMALIZED_STATE.get("UNKNOWN"));
    }

    private String timeoutOrErrorFallback(String ticketId) {
        return String.format("工单:%s;状态:UNKNOWN;归一化状态:服务不可用", ticketId);
    }
}