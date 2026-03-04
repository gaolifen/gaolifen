package com.contract.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 合同条款 —— 表示合同中的一项具体约定。
 *
 * <p>每个条款包含：
 * <ul>
 *   <li>条款编号（clauseId）</li>
 *   <li>条款标题（title）</li>
 *   <li>条款描述（description）</li>
 *   <li>必须出现的关键词列表（requiredKeywords）：交付物中必须包含这些词才算合规</li>
 *   <li>禁止出现的关键词列表（forbiddenKeywords）：交付物中不得包含这些词</li>
 * </ul>
 */
public class ContractClause {

    private final String clauseId;
    private final String title;
    private final String description;
    private final List<String> requiredKeywords;
    private final List<String> forbiddenKeywords;

    public ContractClause(String clauseId, String title, String description,
                          List<String> requiredKeywords, List<String> forbiddenKeywords) {
        if (clauseId == null || clauseId.isBlank()) {
            throw new IllegalArgumentException("clauseId 不能为空");
        }
        this.clauseId = clauseId;
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.requiredKeywords = requiredKeywords != null
                ? Collections.unmodifiableList(new ArrayList<>(requiredKeywords))
                : Collections.emptyList();
        this.forbiddenKeywords = forbiddenKeywords != null
                ? Collections.unmodifiableList(new ArrayList<>(forbiddenKeywords))
                : Collections.emptyList();
    }

    public String getClauseId() {
        return clauseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredKeywords() {
        return requiredKeywords;
    }

    public List<String> getForbiddenKeywords() {
        return forbiddenKeywords;
    }

    @Override
    public String toString() {
        return String.format("ContractClause{id='%s', title='%s'}", clauseId, title);
    }
}
