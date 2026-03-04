package com.contract.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 合规检测结果 —— 记录某交付物针对某条款的检测结论。
 */
public class ComplianceResult {

    private final ContractClause clause;
    private final Deliverable deliverable;
    private final boolean compliant;
    private final List<String> missingKeywords;
    private final List<String> violatingKeywords;

    public ComplianceResult(ContractClause clause, Deliverable deliverable,
                            List<String> missingKeywords, List<String> violatingKeywords) {
        this.clause = clause;
        this.deliverable = deliverable;
        this.missingKeywords = missingKeywords != null
                ? Collections.unmodifiableList(new ArrayList<>(missingKeywords))
                : Collections.emptyList();
        this.violatingKeywords = violatingKeywords != null
                ? Collections.unmodifiableList(new ArrayList<>(violatingKeywords))
                : Collections.emptyList();
        this.compliant = this.missingKeywords.isEmpty() && this.violatingKeywords.isEmpty();
    }

    /** 是否合规 */
    public boolean isCompliant() {
        return compliant;
    }

    public ContractClause getClause() {
        return clause;
    }

    public Deliverable getDeliverable() {
        return deliverable;
    }

    /** 交付物中缺失的必需关键词 */
    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    /** 交付物中出现的禁止关键词 */
    public List<String> getViolatingKeywords() {
        return violatingKeywords;
    }

    @Override
    public String toString() {
        if (compliant) {
            return String.format("[合规] 交付物「%s」符合条款「%s」",
                    deliverable.getName(), clause.getTitle());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[不合规] 交付物「%s」不符合条款「%s」",
                deliverable.getName(), clause.getTitle()));
        if (!missingKeywords.isEmpty()) {
            sb.append(String.format("；缺失必需关键词：%s", missingKeywords));
        }
        if (!violatingKeywords.isEmpty()) {
            sb.append(String.format("；包含禁止关键词：%s", violatingKeywords));
        }
        return sb.toString();
    }
}
