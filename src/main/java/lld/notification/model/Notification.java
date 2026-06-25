package lld.notification.model;


public class Notification {
    private final String recipient;
    private final String content;

    public Notification(String recipient, String content) {
        this.recipient = recipient;
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
}
