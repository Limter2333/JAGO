package org.example.service;

import org.example.skill.CalculatorSkill;
import org.example.skill.DateTimeSkill;
import org.example.skill.EnterpriseSearchSkill;
import org.example.skill.TicketStatusSkill;
import org.example.skill.WeatherSkill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QwenServiceTicketStatusToolRouteTest {

    @Test
    void ticketStatusRouteShouldReturnStructuredResult() {
        QwenService service = new QwenService(
                null,
                new WeatherSkill(),
                new CalculatorSkill(),
                new DateTimeSkill(),
                new EnterpriseSearchSkill(),
                new TicketStatusSkill(),
                null
        );

        String result = service.callTicketStatusTool("INC-1001");

        assertTrue(result.contains("工单:INC-1001"));
        assertTrue(result.contains("归一化状态:"));
    }

    @Test
    void ticketStatusRouteShouldReturnSafeFallbackOnInvalidInput() {
        QwenService service = new QwenService(
                null,
                new WeatherSkill(),
                new CalculatorSkill(),
                new DateTimeSkill(),
                new EnterpriseSearchSkill(),
                new TicketStatusSkill(),
                null
        );

        String result = service.callTicketStatusTool(" ");

        assertTrue(result.contains("工单:未知工单"));
        assertTrue(result.contains("归一化状态:未找到"));
    }
}
