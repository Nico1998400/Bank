package se.sensera.banking.impl;

import se.sensera.banking.Account;
import se.sensera.banking.User;

import java.util.stream.Stream;

public class AccountImpl implements Account {
    @Override
    public String getId() {
        return null;
    }

    @Override
    public User getOwner() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setActive(boolean active) {

    }

    @Override
    public Stream<User> getUsers() {
        return null;
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public void removeUser(User user) {

    }
}
