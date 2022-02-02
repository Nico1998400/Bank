package se.sensera.banking.impl;

import lombok.Data;
import se.sensera.banking.Account;
import se.sensera.banking.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class AccountImpl implements Account {

    private String id;
    private User owner;
    private String name;
    private boolean active;
    private List<User> userList = new ArrayList<>();

    public AccountImpl(User owner, String name, boolean active) {
        this.owner = owner;
        this.name = name;
        this.active = active;
    }

    @Override
    public Stream<User> getUsers() {
        return userList.stream();
    }

    @Override
    public void addUser(User user) {
        userList.add(user);
    }

    @Override
    public void removeUser(User user) {
        userList.remove(user);
    }

    @Override
    public String getId() {
        return this.id;
    }

}
