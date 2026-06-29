package lld.texteditor.command;

import lld.texteditor.model.Document;

public class InsertCommand implements Command {
    private final Document document;
    private final int position;
    private final String text;

    public InsertCommand(Document document, int position, String text) {
        this.document = document;
        this.position = position;
        this.text = text;
    }

    @Override
    public void execute() {
        document.addText(position, text);
    }

    @Override
    public void undo() {
        document.deleteText(position, text.length());
    }
}
