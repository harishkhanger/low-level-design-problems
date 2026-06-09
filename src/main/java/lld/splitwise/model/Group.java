package lld.splitwise.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final String name;
    private final List<User> users;

    public Group(String name, List<User> users) {
        this.name = name;
        this.users = new ArrayList<>(users);
    }

    public void addMember(User user){
        if (users.contains(user))throw new IllegalArgumentException("User already exists");
        users.add(user);
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return List.copyOf(users);
    }
}
