package lld.filesystem;

import lld.filesystem.model.Directory;
import lld.filesystem.model.File;
import lld.filesystem.model.FileSystemNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSystemTest {

    @Test
    void fileSizeIsItsOwn() {
        assertEquals(42, new File("a.txt", 42).getSize());
    }

    @Test
    void emptyDirectoryHasZeroSize() {
        assertEquals(0, new Directory("empty").getSize());
    }

    @Test
    void directorySizeSumsItsChildren() {
        Directory dir = new Directory("docs")
            .add(new File("a.txt", 10))
            .add(new File("b.txt", 20));
        assertEquals(30, dir.getSize());
    }

    @Test
    void sizeRecursesThroughNestedSubdirectories() {
        Directory deeper = new Directory("deeper").add(new File("file3", 7));
        Directory sub = new Directory("sub").add(new File("file2", 30)).add(deeper);
        Directory root = new Directory("root").add(new File("file1", 100)).add(sub);

        assertEquals(137, root.getSize());
    }

    @Test
    void findLocatesMatchesAnywhereInTheTree() {
        Directory sub = new Directory("sub").add(new File("target.txt", 1));
        Directory root = new Directory("root")
            .add(new File("target.txt", 1))
            .add(sub);

        List<FileSystemNode> hits = root.find("target.txt");
        assertEquals(2, hits.size());
        assertTrue(hits.stream().allMatch(n -> n.getName().equals("target.txt")));
    }

    @Test
    void findMatchesDirectoriesToo() {
        Directory root = new Directory("root").add(new Directory("logs"));
        assertEquals(1, root.find("logs").size());
    }

    @Test
    void removeDropsChildFromSizeAndSearch() {
        File temp = new File("temp.bin", 500);
        Directory dir = new Directory("cache").add(new File("keep.txt", 5)).add(temp);
        assertEquals(505, dir.getSize());

        dir.remove(temp);
        assertEquals(5, dir.getSize());
        assertEquals(0, dir.find("temp.bin").size());
    }

    @Test
    void blankNameAndNegativeSizeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new File("", 1));
        assertThrows(IllegalArgumentException.class, () -> new File("a", -1));
        assertThrows(IllegalArgumentException.class, () -> new Directory("  "));
    }
}
