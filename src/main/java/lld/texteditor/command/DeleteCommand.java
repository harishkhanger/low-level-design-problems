package lld.texteditor.command;

import lld.texteditor.model.Document;

public class DeleteCommand implements Command {
    private final Document document;
    private final int position;
    private final int length;
    private String deletedText;

    public DeleteCommand(Document document, int position, int length) {
        this.document = document;
        this.position = position;
        this.length = length;
    }

    @Override
    public void execute() {
        deletedText = document.deleteText(position, length);
    }

    @Override
    public void undo() {
        document.addText(position, deletedText);
    }
}
