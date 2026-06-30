package org.example.service;

import org.example.skill.CalculatorSkill;
import org.example.skill.DateTimeSkill;
import org.example.skill.EnterpriseSearchSkill;
import org.example.skill.TicketStatusSkill;
import org.example.skill.WeatherSkill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QwenServiceEnterpriseSearchToolRouteTest {

    @Test
    void enterpriseSearchRouteShouldReturnStructuredResult() {
        QwenService service = new QwenService(
                null,
                new WeatherSkill(),
                new CalculatorSkill(),
                new DateTimeSkill(),
            new EnterpriseSearchSkill(),
                new TicketStatusSkill(),
                null
        );

        String result = service.callEnterpriseSearchTool("报销流程");

        assertTrue(result.contains("主题:报销流程"));
        assertTrue(result.contains("文档:"));
    }

    @Test
    void enterpriseSearchRouteShouldReturnSafeFallbackOnInvalidInput() {
        QwenService service = new QwenService(
                null,
                new WeatherSkill(),
                new CalculatorSkill(),
                new DateTimeSkill(),
            new EnterpriseSearchSkill(),
                new TicketStatusSkill(),
                null
        );

        String result = service.callEnterpriseSearchTool(" ");

        assertTrue(result.contains("主题:未知"));
        assertTrue(result.contains("状态:未找到"));
    }
}
