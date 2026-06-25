package lld.notification.model;

public class SendResult {
    private final boolean success;
    private final String detail;

    private SendResult(boolean success, String detail) {
        this.success = success;
        this.detail = detail;
    }

    public static SendResult ok(String detail) {
        return new SendResult(true, detail);
    }

    public static SendResult fail(String detail) {
        return new SendResult(false, detail);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDetail() {
        return detail;
    }
}
