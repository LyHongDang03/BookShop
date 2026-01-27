package lyhongdang.book.enums;

public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password"),
    ORDER_CONFIRMATION("order_confirmation"),
    DAILY_REPORT("daily-report"),
    ;
    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
