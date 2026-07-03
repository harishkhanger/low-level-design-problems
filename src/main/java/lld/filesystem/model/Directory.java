package lld.filesystem.model;

import java.util.ArrayList;
import java.util.List;

public class Directory extends FileSystemNode {
    private final List<FileSystemNode> children = new ArrayList<>();

    public Directory(String name) {
        super(name);
    }

    public Directory add(FileSystemNode node) {
        children.add(node);
        return this;
    }

    public void remove(FileSystemNode node) {
        children.remove(node);
    }

    public List<FileSystemNode> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public int getSize() {
        int total = 0;
        for (FileSystemNode child : children) {
            total += child.getSize();
        }
        return total;
    }

    @Override
    public List<FileSystemNode> find(String target) {
        List<FileSystemNode> matches = new ArrayList<>();
        if (getName().equals(target)) {
            matches.add(this);
        }
        for (FileSystemNode child : children) {
            matches.addAll(child.find(target));
        }
        return matches;
    }
}
