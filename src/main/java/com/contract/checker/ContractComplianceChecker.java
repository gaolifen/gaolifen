package com.contract.checker;

import java.util.ArrayList;
import java.util.List;

/**
 * 合同条款合规检测器。
 *
 * <p>核查逻辑：
 * <ol>
 *   <li>对每个合同条款，遍历其 <em>requiredKeywords</em>，检查交付物内容是否均包含这些词；
 *       不包含则记为"缺失关键词"，判定为不合规。</li>
 *   <li>遍历条款的 <em>forbiddenKeywords</em>，检查交付物内容是否出现了禁止词；
 *       出现则记为"违规关键词"，判定为不合规。</li>
 *   <li>两项均通过则该交付物对该条款合规。</li>
 * </ol>
 *
 * <p>关键词匹配默认不区分大小写。
 */
public class ContractComplianceChecker {

    /**
     * 检测单个交付物是否符合指定合同条款。
     *
     * @param clause      合同条款
     * @param deliverable 交付物
     * @return 检测结果
     */
    public ComplianceResult check(ContractClause clause, Deliverable deliverable) {
        if (clause == null) {
            throw new IllegalArgumentException("clause 不能为 null");
        }
        if (deliverable == null) {
            throw new IllegalArgumentException("deliverable 不能为 null");
        }

        String contentLower = deliverable.getContent().toLowerCase();

        // 找出缺失的必需关键词
        List<String> missing = new ArrayList<>();
        for (String keyword : clause.getRequiredKeywords()) {
            if (keyword != null && !keyword.isBlank()
                    && !contentLower.contains(keyword.toLowerCase())) {
                missing.add(keyword);
            }
        }

        // 找出出现的禁止关键词
        List<String> violating = new ArrayList<>();
        for (String keyword : clause.getForbiddenKeywords()) {
            if (keyword != null && !keyword.isBlank()
                    && contentLower.contains(keyword.toLowerCase())) {
                violating.add(keyword);
            }
        }

        return new ComplianceResult(clause, deliverable, missing, violating);
    }

    /**
     * 检测单个交付物是否符合多个合同条款（批量检测）。
     *
     * @param clauses     合同条款列表
     * @param deliverable 交付物
     * @return 每条条款对应的检测结果列表
     */
    public List<ComplianceResult> checkAll(List<ContractClause> clauses, Deliverable deliverable) {
        if (clauses == null) {
            throw new IllegalArgumentException("clauses 不能为 null");
        }
        List<ComplianceResult> results = new ArrayList<>();
        for (ContractClause clause : clauses) {
            results.add(check(clause, deliverable));
        }
        return results;
    }

    /**
     * 检测多个交付物是否符合多个合同条款（全量检测）。
     *
     * @param clauses      合同条款列表
     * @param deliverables 交付物列表
     * @return 所有条款与交付物组合的检测结果列表
     */
    public List<ComplianceResult> checkAllDeliverables(List<ContractClause> clauses,
                                                        List<Deliverable> deliverables) {
        if (clauses == null) {
            throw new IllegalArgumentException("clauses 不能为 null");
        }
        if (deliverables == null) {
            throw new IllegalArgumentException("deliverables 不能为 null");
        }
        List<ComplianceResult> results = new ArrayList<>();
        for (Deliverable deliverable : deliverables) {
            results.addAll(checkAll(clauses, deliverable));
        }
        return results;
    }
}
