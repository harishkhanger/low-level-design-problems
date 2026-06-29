package lld.texteditor.model;

public class Document {
    private final StringBuilder content = new StringBuilder();

    public void addText(int position, String text) {
        content.insert(position, text);
    }

    public String deleteText(int position, int length) {
        String removed = content.substring(position, position + length);
        content.delete(position, position + length);
        return removed;
    }

    public int length() {
        return content.length();
    }

    public String getText() {
        return content.toString();
    }
}
