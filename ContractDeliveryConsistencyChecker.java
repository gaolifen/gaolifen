import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ContractDeliveryConsistencyChecker {

    public static final class Clause {
        private final String id;
        private final String description;
        private final List<String> requiredKeywords;

        public Clause(String id, String description, List<String> requiredKeywords) {
            this.id = id;
            this.description = description;
            this.requiredKeywords = new ArrayList<>(requiredKeywords);
        }

        public String getId() {
            return id;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getRequiredKeywords() {
            return Collections.unmodifiableList(requiredKeywords);
        }
    }

    public static final class CheckResult {
        private final Clause clause;
        private final boolean consistent;
        private final String matchedDeliverable;

        public CheckResult(Clause clause, boolean consistent, String matchedDeliverable) {
            this.clause = clause;
            this.consistent = consistent;
            this.matchedDeliverable = matchedDeliverable;
        }

        public Clause getClause() {
            return clause;
        }

        public boolean isConsistent() {
            return consistent;
        }

        public String getMatchedDeliverable() {
            return matchedDeliverable;
        }
    }

    public static List<CheckResult> checkConsistency(List<Clause> clauses, List<String> deliverables) {
        List<CheckResult> results = new ArrayList<>();

        for (Clause clause : clauses) {
            String matched = findMatchedDeliverable(clause, deliverables);
            results.add(new CheckResult(clause, matched != null, matched));
        }

        return results;
    }

    private static String findMatchedDeliverable(Clause clause, List<String> deliverables) {
        for (String deliverable : deliverables) {
            if (containsAllKeywords(deliverable, clause.getRequiredKeywords())) {
                return deliverable;
            }
        }
        return null;
    }

    private static boolean containsAllKeywords(String text, List<String> keywords) {
        String normalizedText = text.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (!normalizedText.contains(keyword.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        List<Clause> clauses = Arrays.asList(
            new Clause("C-001", "系统需支持单点登录并提供审计日志", Arrays.asList("单点登录", "审计日志")),
            new Clause("C-002", "系统需提供自动备份和恢复机制", Arrays.asList("自动备份", "恢复")),
            new Clause("C-003", "接口需输出OpenAPI文档", Arrays.asList("OpenAPI"))
        );

        List<String> deliverables = Arrays.asList(
            "《IT交付说明》：实现了单点登录、审计日志、用户权限控制。",
            "《运维手册》：描述了自动备份策略和一键恢复流程。"
        );

        List<CheckResult> results = checkConsistency(clauses, deliverables);
        for (CheckResult result : results) {
            if (result.isConsistent()) {
                System.out.println("[一致] " + result.getClause().getId() + " - " + result.getClause().getDescription());
                System.out.println("       匹配交付物: " + result.getMatchedDeliverable());
            } else {
                System.out.println("[不一致] " + result.getClause().getId() + " - " + result.getClause().getDescription());
                System.out.println("       原因: 未在交付物中找到包含全部关键字的内容: " + result.getClause().getRequiredKeywords());
            }
        }
    }
}
