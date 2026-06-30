package org.example.skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EnterpriseSearchSkillTest {

    private final EnterpriseSearchSkill enterpriseSearchSkill = new EnterpriseSearchSkill();

    @Test
    void shouldReturnStructuredResultForKnownQuery() {
        String output = enterpriseSearchSkill.searchInternalKnowledge("报销流程");

        assertTrue(output.contains("主题:报销流程"));
        assertTrue(output.contains("部门:"));
        assertTrue(output.contains("文档:"));
        assertTrue(output.contains("状态:"));
    }

    @Test
    void shouldReturnSafeFallbackForInvalidQuery() {
        String output = enterpriseSearchSkill.searchInternalKnowledge(" ");

        assertTrue(output.contains("主题:未知"));
        assertTrue(output.contains("状态:未找到"));
    }
}
