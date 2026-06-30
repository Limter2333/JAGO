package org.example.service;

import org.example.skill.CalculatorSkill;
import org.example.skill.DateTimeSkill;
import org.example.skill.EnterpriseSearchSkill;
import org.example.skill.TicketStatusSkill;
import org.example.skill.WeatherSkill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QwenServiceWeatherToolRouteTest {

    @Test
    void weatherToolRouteShouldReturnStructuredResult() {
        QwenService service = new QwenService(
            null,
            new WeatherSkill(),
            new CalculatorSkill(),
            new DateTimeSkill(),
            new EnterpriseSearchSkill(),
            new TicketStatusSkill()
        );

        String result = service.callWeatherTool("上海");

        assertTrue(result.contains("城市:上海"));
        assertTrue(result.contains("天气:"));
    }

    @Test
    void weatherToolRouteShouldReturnSafeFallbackOnInvalidInput() {
        QwenService service = new QwenService(
            null,
            new WeatherSkill(),
            new CalculatorSkill(),
            new DateTimeSkill(),
            new EnterpriseSearchSkill(),
            new TicketStatusSkill()
        );

        String result = service.callWeatherTool(" ");

        assertTrue(result.contains("天气:未知"));
        assertTrue(result.contains("提示:"));
    }
}
