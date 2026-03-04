package com.contract.checker;

/**
 * IT项目交付物 —— 表示项目中需要提交的一项交付成果。
 *
 * <p>每个交付物包含：
 * <ul>
 *   <li>交付物编号（deliverableId）</li>
 *   <li>交付物名称（name）</li>
 *   <li>交付物正文内容（content）：将与合同条款进行合规性核查</li>
 * </ul>
 */
public class Deliverable {

    private final String deliverableId;
    private final String name;
    private final String content;

    public Deliverable(String deliverableId, String name, String content) {
        if (deliverableId == null || deliverableId.isBlank()) {
            throw new IllegalArgumentException("deliverableId 不能为空");
        }
        this.deliverableId = deliverableId;
        this.name = name != null ? name : "";
        this.content = content != null ? content : "";
    }

    public String getDeliverableId() {
        return deliverableId;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("Deliverable{id='%s', name='%s'}", deliverableId, name);
    }
}
