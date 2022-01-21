package se.sensera.banking.impl;

import se.sensera.banking.User;
import se.sensera.banking.UserService;
import se.sensera.banking.UsersRepository;
import se.sensera.banking.exceptions.UseException;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {
    private UsersRepository usersRepository;

    public UserServiceImpl(UsersRepository usersRepository) { // alt enter, create constructor
        this.usersRepository = usersRepository;
    }

    @Override
    public User createUser(String name, String personalIdentificationNumber) throws UseException {
        UserImpl user = new UserImpl(UUID.randomUUID().toString(), name, personalIdentificationNumber, true);
        return usersRepository.save(user); //Lägger till user save
    }

    @Override
    public User changeUser(String userId, Consumer<ChangeUser> changeUser) throws UseException {
        return null;
    }

    @Override
    public User inactivateUser(String userId) throws UseException {
        return null;
    }

    @Override
    public Optional<User> getUser(String userId) {
        return Optional.empty();
    }

    @Override
    public Stream<User> find(String searchString, Integer pageNumber, Integer pageSize, SortOrder sortOrder) {
        return null;
    }
}
