package com.contract.checker;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 合同条款合规检测器单元测试。
 */
public class ContractComplianceCheckerTest {

    private final ContractComplianceChecker checker = new ContractComplianceChecker();

    // ------------------------------------------------------------------
    // 辅助方法
    // ------------------------------------------------------------------

    private ContractClause buildClause(List<String> required, List<String> forbidden) {
        return new ContractClause("C001", "测试条款", "测试用条款描述", required, forbidden);
    }

    private Deliverable buildDeliverable(String content) {
        return new Deliverable("D001", "测试交付物", content);
    }

    // ------------------------------------------------------------------
    // ContractClause 构造测试
    // ------------------------------------------------------------------

    @Test
    public void testContractClause_validConstruction() {
        ContractClause clause = buildClause(
                Arrays.asList("关键词A", "关键词B"),
                Arrays.asList("禁词X"));
        assertEquals("C001", clause.getClauseId());
        assertEquals("测试条款", clause.getTitle());
        assertEquals(2, clause.getRequiredKeywords().size());
        assertEquals(1, clause.getForbiddenKeywords().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContractClause_blankIdThrows() {
        new ContractClause("  ", "title", "desc",
                Collections.emptyList(), Collections.emptyList());
    }

    // ------------------------------------------------------------------
    // Deliverable 构造测试
    // ------------------------------------------------------------------

    @Test
    public void testDeliverable_validConstruction() {
        Deliverable d = buildDeliverable("内容示例");
        assertEquals("D001", d.getDeliverableId());
        assertEquals("测试交付物", d.getName());
        assertEquals("内容示例", d.getContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeliverable_blankIdThrows() {
        new Deliverable("", "name", "content");
    }

    // ------------------------------------------------------------------
    // 合规场景：包含所有必需关键词，不包含禁止关键词
    // ------------------------------------------------------------------

    @Test
    public void testCheck_compliant() {
        ContractClause clause = buildClause(
                Arrays.asList("功能需求", "验收标准"),
                Arrays.asList("待定"));
        Deliverable deliverable = buildDeliverable(
                "本文档包含功能需求描述和验收标准，内容完整。");

        ComplianceResult result = checker.check(clause, deliverable);

        assertTrue(result.isCompliant());
        assertTrue(result.getMissingKeywords().isEmpty());
        assertTrue(result.getViolatingKeywords().isEmpty());
    }

    // ------------------------------------------------------------------
    // 不合规场景：缺失必需关键词
    // ------------------------------------------------------------------

    @Test
    public void testCheck_missingRequiredKeyword() {
        ContractClause clause = buildClause(
                Arrays.asList("功能需求", "非功能需求", "验收标准"),
                Collections.emptyList());
        Deliverable deliverable = buildDeliverable(
                "本文档仅包含功能需求描述，其余部分暂缺。");

        ComplianceResult result = checker.check(clause, deliverable);

        assertFalse(result.isCompliant());
        assertEquals(2, result.getMissingKeywords().size());
        assertTrue(result.getMissingKeywords().contains("非功能需求"));
        assertTrue(result.getMissingKeywords().contains("验收标准"));
    }

    // ------------------------------------------------------------------
    // 不合规场景：包含禁止关键词
    // ------------------------------------------------------------------

    @Test
    public void testCheck_forbiddenKeywordPresent() {
        ContractClause clause = buildClause(
                Collections.emptyList(),
                Arrays.asList("待定", "TBD"));
        Deliverable deliverable = buildDeliverable(
                "部分功能待定，将在后续版本确认。");

        ComplianceResult result = checker.check(clause, deliverable);

        assertFalse(result.isCompliant());
        assertEquals(1, result.getViolatingKeywords().size());
        assertTrue(result.getViolatingKeywords().contains("待定"));
    }

    // ------------------------------------------------------------------
    // 不合规场景：同时缺失关键词且包含禁止词
    // ------------------------------------------------------------------

    @Test
    public void testCheck_bothMissingAndForbidden() {
        ContractClause clause = buildClause(
                Arrays.asList("架构设计"),
                Arrays.asList("TBD"));
        Deliverable deliverable = buildDeliverable(
                "接口设计已完成，架构方案 TBD。");

        ComplianceResult result = checker.check(clause, deliverable);

        assertFalse(result.isCompliant());
        assertTrue(result.getMissingKeywords().contains("架构设计"));
        assertTrue(result.getViolatingKeywords().contains("TBD"));
    }

    // ------------------------------------------------------------------
    // 关键词匹配不区分大小写
    // ------------------------------------------------------------------

    @Test
    public void testCheck_caseInsensitiveMatch() {
        ContractClause clause = buildClause(
                Arrays.asList("api"),
                Arrays.asList("deprecated"));
        Deliverable deliverable = buildDeliverable(
                "本文档包含 API 设计，所有接口均为最新版本。");

        ComplianceResult result = checker.check(clause, deliverable);

        assertTrue(result.isCompliant());
    }

    // ------------------------------------------------------------------
    // 空条款列表的批量检测
    // ------------------------------------------------------------------

    @Test
    public void testCheckAll_emptyClauses() {
        List<ComplianceResult> results = checker.checkAll(
                Collections.emptyList(), buildDeliverable("任意内容"));
        assertTrue(results.isEmpty());
    }

    // ------------------------------------------------------------------
    // 多条款批量检测
    // ------------------------------------------------------------------

    @Test
    public void testCheckAll_multipleClauses() {
        ContractClause c1 = new ContractClause("C001", "条款1", "",
                Arrays.asList("关键词1"), Collections.emptyList());
        ContractClause c2 = new ContractClause("C002", "条款2", "",
                Arrays.asList("关键词2"), Collections.emptyList());
        Deliverable d = buildDeliverable("包含关键词1的内容");

        List<ComplianceResult> results = checker.checkAll(Arrays.asList(c1, c2), d);

        assertEquals(2, results.size());
        assertTrue(results.get(0).isCompliant());
        assertFalse(results.get(1).isCompliant());
    }

    // ------------------------------------------------------------------
    // 全量检测（多条款 × 多交付物）
    // ------------------------------------------------------------------

    @Test
    public void testCheckAllDeliverables() {
        ContractClause c1 = new ContractClause("C001", "条款1", "",
                Arrays.asList("关键词A"), Collections.emptyList());
        Deliverable d1 = buildDeliverable("包含关键词A");
        Deliverable d2 = new Deliverable("D002", "交付物2", "无相关内容");

        List<ComplianceResult> results = checker.checkAllDeliverables(
                Collections.singletonList(c1), Arrays.asList(d1, d2));

        assertEquals(2, results.size());
        assertTrue(results.get(0).isCompliant());
        assertFalse(results.get(1).isCompliant());
    }

    // ------------------------------------------------------------------
    // null 参数守卫
    // ------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testCheck_nullClauseThrows() {
        checker.check(null, buildDeliverable("content"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheck_nullDeliverableThrows() {
        checker.check(buildClause(Collections.emptyList(), Collections.emptyList()), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckAll_nullClausesThrows() {
        checker.checkAll(null, buildDeliverable("content"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckAllDeliverables_nullDeliverablesThrows() {
        checker.checkAllDeliverables(Collections.emptyList(), null);
    }

    // ------------------------------------------------------------------
    // ComplianceResult.toString() 可读性验证
    // ------------------------------------------------------------------

    @Test
    public void testComplianceResult_toString_compliant() {
        ComplianceResult r = new ComplianceResult(
                buildClause(Collections.emptyList(), Collections.emptyList()),
                buildDeliverable("内容"),
                Collections.emptyList(), Collections.emptyList());
        assertTrue(r.toString().contains("[合规]"));
    }

    @Test
    public void testComplianceResult_toString_nonCompliant() {
        ComplianceResult r = new ComplianceResult(
                buildClause(Collections.emptyList(), Collections.emptyList()),
                buildDeliverable("内容"),
                Arrays.asList("缺失词"), Collections.emptyList());
        assertTrue(r.toString().contains("[不合规]"));
        assertTrue(r.toString().contains("缺失词"));
    }
}
