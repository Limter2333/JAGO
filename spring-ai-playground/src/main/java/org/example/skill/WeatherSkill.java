package org.example.skill;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class WeatherSkill {

    @Tool(description = "查询指定城市的天气信息")
    public String queryWeather(
            @ToolParam(description = "城市名称，如：北京、上海") String city
    ) {
        try {
            String normalizedCity = normalizeCity(city);
            return switch (normalizedCity) {
                case "北京" -> formatWeather(normalizedCity, "晴朗", "25", "空气质量优");
                case "上海" -> formatWeather(normalizedCity, "多云", "28", "空气质量良好");
                case "广州" -> formatWeather(normalizedCity, "有雨", "30", "湿度较大");
                case "深圳" -> formatWeather(normalizedCity, "晴朗", "32", "适宜户外活动");
                default -> safeFallback(normalizedCity);
            };
        } catch (IllegalArgumentException ex) {
            return safeFallback("未知城市");
        } catch (RuntimeException ex) {
            return "天气服务暂时不可用，请稍后重试。";
        }
    }

    private String normalizeCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city must not be blank");
        }
        String normalized = city.trim();
        if (normalized.length() > 20) {
            throw new IllegalArgumentException("city is too long");
        }
        return normalized;
    }

    private String formatWeather(String city, String weather, String temperature, String detail) {
        return String.format("城市:%s;天气:%s;温度:%s°C;提示:%s", city, weather, temperature, detail);
    }

    private String safeFallback(String city) {
        return String.format("城市:%s;天气:未知;温度:未知;提示:暂无可用天气数据", city);
    }
}
