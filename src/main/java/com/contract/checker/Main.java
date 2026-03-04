package com.contract.checker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 合同条款检测系统入口类。
 *
 * <p>演示如何使用 {@link ContractComplianceChecker} 检测 IT 项目交付物是否符合合同约定。
 *
 * <p>运行方式：
 * <pre>
 *   mvn package
 *   java -jar target/contract-compliance-checker-1.0.0.jar
 * </pre>
 */
public class Main {

    public static void main(String[] args) {

        // ----------------------------------------------------------------
        // 1. 定义合同条款
        // ----------------------------------------------------------------

        // 条款1：需求规格说明书须包含"功能需求"、"非功能需求"和"验收标准"，且不得出现"待定"
        ContractClause clause1 = new ContractClause(
                "C001",
                "需求规格说明书要求",
                "交付物必须包含完整的功能需求、非功能需求及验收标准，且内容不得有未确定项。",
                Arrays.asList("功能需求", "非功能需求", "验收标准"),
                Arrays.asList("待定", "TBD")
        );

        // 条款2：系统设计文档须包含"架构设计"、"数据库设计"和"接口设计"
        ContractClause clause2 = new ContractClause(
                "C002",
                "系统设计文档要求",
                "系统设计文档需涵盖架构设计、数据库设计及接口设计三个部分。",
                Arrays.asList("架构设计", "数据库设计", "接口设计"),
                Collections.emptyList()
        );

        // 条款3：测试报告须包含"测试用例"和"缺陷列表"，且不得出现"未测试"
        ContractClause clause3 = new ContractClause(
                "C003",
                "测试报告要求",
                "测试报告须包含完整的测试用例及缺陷列表，且不得存在未完成的测试项。",
                Arrays.asList("测试用例", "缺陷列表"),
                Arrays.asList("未测试")
        );

        List<ContractClause> clauses = Arrays.asList(clause1, clause2, clause3);

        // ----------------------------------------------------------------
        // 2. 定义 IT 项目交付物
        // ----------------------------------------------------------------

        // 交付物A：完整的需求规格说明书（合规）
        Deliverable deliverableA = new Deliverable(
                "D001",
                "需求规格说明书_v1.0",
                "本文档描述了系统的功能需求与非功能需求，并明确了验收标准，供各方确认。"
        );

        // 交付物B：缺少"架构设计"且存在禁止词"待定"的系统设计文档（不合规）
        Deliverable deliverableB = new Deliverable(
                "D002",
                "系统设计文档_v0.9（草稿）",
                "本文档包含数据库设计和接口设计部分，架构方案待定，请后续补充。"
        );

        // 交付物C：完整的测试报告（合规）
        Deliverable deliverableC = new Deliverable(
                "D003",
                "测试报告_v1.0",
                "本次测试共编写测试用例 120 条，执行完毕后整理缺陷列表如附件所示，无遗留问题。"
        );

        // 交付物D：含"未测试"禁止词的测试报告（不合规）
        Deliverable deliverableD = new Deliverable(
                "D004",
                "测试报告_v0.8（不完整）",
                "性能测试部分未测试，测试用例和缺陷列表将在下一版本补充。"
        );

        List<Deliverable> deliverables = Arrays.asList(
                deliverableA, deliverableB, deliverableC, deliverableD);

        // ----------------------------------------------------------------
        // 3. 执行全量合规检测
        // ----------------------------------------------------------------
        ContractComplianceChecker checker = new ContractComplianceChecker();
        List<ComplianceResult> results = checker.checkAllDeliverables(clauses, deliverables);

        // ----------------------------------------------------------------
        // 4. 输出检测报告
        // ----------------------------------------------------------------
        System.out.println("========================================");
        System.out.println("     合同条款 IT 项目交付物合规检测报告");
        System.out.println("========================================");

        long compliantCount = results.stream().filter(ComplianceResult::isCompliant).count();
        long nonCompliantCount = results.size() - compliantCount;

        for (ComplianceResult result : results) {
            System.out.println(result);
        }

        System.out.println("----------------------------------------");
        System.out.printf("共检测 %d 项（%d 条条款 × %d 个交付物）：合规 %d 项，不合规 %d 项%n",
                results.size(), clauses.size(), deliverables.size(),
                compliantCount, nonCompliantCount);
        System.out.println("========================================");
    }
}
