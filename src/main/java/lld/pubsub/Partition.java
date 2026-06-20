package lld.pubsub;

import java.util.ArrayList;
import java.util.List;

public class Partition {

    private final int id;
    private final List<Message> log = new ArrayList<>();

    public Partition(int id) {
        this.id = id;
    }

    public synchronized long append(Message message) {
        long offset = log.size();
        log.add(message);
        return offset;
    }

    public synchronized List<Message> read (long fromOffset) {
        if (fromOffset >= log.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(log.subList((int)fromOffset, log.size()));
    }

    public synchronized long size() {
        return log.size();
    }

    public int getId() {
        return id;
    }
}
