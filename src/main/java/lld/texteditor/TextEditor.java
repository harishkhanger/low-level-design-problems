package lld.texteditor;

import lld.texteditor.command.Command;
import lld.texteditor.command.DeleteCommand;
import lld.texteditor.command.InsertCommand;
import lld.texteditor.model.Document;

import java.util.ArrayDeque;
import java.util.Deque;

public class TextEditor {
    private final Document document = new Document();
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public void insert(int position, String text) {
        executeCommand(new InsertCommand(document, position, text));
    }

    public void delete(int position, int length) {
        executeCommand(new DeleteCommand(document, position, length));
    }

    private void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }

    public String getText() {
        return document.getText();
    }
}
