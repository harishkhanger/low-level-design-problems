package lld.filesystem.model;

import java.util.List;

public abstract class FileSystemNode {
    private final String name;

    protected FileSystemNode(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract int getSize();

    public abstract List<FileSystemNode> find(String target);
}
