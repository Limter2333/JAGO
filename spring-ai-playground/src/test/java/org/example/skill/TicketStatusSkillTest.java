package org.example.skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketStatusSkillTest {

    private final TicketStatusSkill ticketStatusSkill = new TicketStatusSkill();

    @Test
    void shouldReturnNormalizedStructuredResultForKnownTicket() {
        String output = ticketStatusSkill.queryTicketStatus("INC-1001");
        String resolvedOutput = ticketStatusSkill.queryTicketStatus("INC-1003");

        assertTrue(output.contains("工单:INC-1001"));
        assertTrue(output.contains("状态:IN_PROGRESS"));
        assertTrue(output.contains("归一化状态:处理中"));
        assertTrue(resolvedOutput.contains("状态:RESOLVED"));
        assertTrue(resolvedOutput.contains("归一化状态:已解决"));
    }

    @Test
    void shouldBeIdempotentForSameInput() {
        String first = ticketStatusSkill.queryTicketStatus("INC-1002");
        String second = ticketStatusSkill.queryTicketStatus("INC-1002");

        assertEquals(first, second);
    }

    @Test
    void shouldReturnSafeFallbackForInvalidTicketId() {
        String output = ticketStatusSkill.queryTicketStatus(" ");

        assertTrue(output.contains("工单:未知工单"));
        assertTrue(output.contains("状态:UNKNOWN"));
        assertTrue(output.contains("归一化状态:未找到"));
    }

    @Test
    void shouldReturnTimeoutFallbackWhenLookupTimesOut() {
        TicketStatusSkill skill = new TicketStatusSkill() {
            @Override
            protected String lookupRawStatus(String normalizedTicketId) {
                throw new RuntimeException("timeout while querying ticket backend");
            }
        };

        String output = skill.queryTicketStatus("INC-1001");

        assertTrue(output.contains("工单:INC-1001"));
        assertTrue(output.contains("状态:UNKNOWN"));
        assertTrue(output.contains("归一化状态:服务不可用"));
    }
}
