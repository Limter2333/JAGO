package org.example.skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherSkillTest {

    private final WeatherSkill weatherSkill = new WeatherSkill();

    @Test
    void shouldReturnStructuredSchemaForKnownCity() {
        String output = weatherSkill.queryWeather("北京");

        assertTrue(output.contains("城市:北京"));
        assertTrue(output.contains("天气:"));
        assertTrue(output.contains("温度:"));
        assertTrue(output.contains("提示:"));
    }

    @Test
    void shouldReturnSafeFallbackForUnknownCity() {
        String output = weatherSkill.queryWeather("苏州");

        assertTrue(output.contains("城市:苏州"));
        assertTrue(output.contains("天气:未知"));
        assertTrue(output.contains("暂无可用天气数据"));
    }

    @Test
    void shouldReturnSafeFallbackForInvalidArgs() {
        String output = weatherSkill.queryWeather(" ");

        assertTrue(output.contains("城市:未知城市"));
        assertTrue(output.contains("天气:未知"));
    }
}
