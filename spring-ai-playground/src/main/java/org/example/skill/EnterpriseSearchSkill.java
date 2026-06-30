package org.example.skill;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnterpriseSearchSkill {

    private static final Map<String, String[]> MOCK_KNOWLEDGE = Map.of(
            "报销流程", new String[]{"财务部", "enterprise-handbook#expense", "已发布"},
            "请假政策", new String[]{"人事部", "enterprise-handbook#leave", "已发布"},
            "安全规范", new String[]{"安全委员会", "security-policy#baseline", "已发布"}
    );

    @Tool(description = "按主题检索企业内部结构化知识")
    public String searchInternalKnowledge(
            @ToolParam(description = "检索主题，例如：报销流程、请假政策、安全规范") String topic
    ) {
        try {
            String normalizedTopic = normalizeTopic(topic);
            String[] record = MOCK_KNOWLEDGE.get(normalizedTopic);
            if (record == null) {
                return safeFallback(normalizedTopic);
            }
            return formatResult(normalizedTopic, record[0], record[1], record[2]);
        } catch (IllegalArgumentException ex) {
            return safeFallback("未知");
        } catch (RuntimeException ex) {
            return "主题:未知;部门:未知;文档:未知;状态:服务不可用";
        }
    }

    private String normalizeTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("topic must not be blank");
        }
        String normalized = topic.trim();
        if (normalized.length() > 40) {
            throw new IllegalArgumentException("topic is too long");
        }
        return normalized;
    }

    private String formatResult(String topic, String department, String document, String status) {
        return String.format("主题:%s;部门:%s;文档:%s;状态:%s", topic, department, document, status);
    }

    private String safeFallback(String topic) {
        return String.format("主题:%s;部门:未知;文档:未知;状态:未找到", topic);
    }
}