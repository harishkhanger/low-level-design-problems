package lld.filesystem.model;

import java.util.List;

public class File extends FileSystemNode {
    private final int size;

    public File(String name, int size) {
        super(name);
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative");
        }
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public List<FileSystemNode> find(String target) {
        return getName().equals(target) ? List.of(this) : List.of();
    }
}
