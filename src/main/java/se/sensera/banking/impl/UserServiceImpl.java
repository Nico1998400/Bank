package se.sensera.banking.impl;

import se.sensera.banking.User;
import se.sensera.banking.UserService;
import se.sensera.banking.UsersRepository;
import se.sensera.banking.exceptions.Activity;
import se.sensera.banking.exceptions.UseException;
import se.sensera.banking.exceptions.UseExceptionType;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {
    private UsersRepository usersRepository;

    public UserServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;

    }

    @Override
    public User createUser(String name, String personalIdentificationNumber) throws UseException {
        if (usersRepository.all()
                .anyMatch(user1 -> user1.getPersonalIdentificationNumber().equals(personalIdentificationNumber)))
            throw new UseException(Activity.CREATE_USER, UseExceptionType.USER_PERSONAL_ID_NOT_UNIQUE);

        UserImpl user = new UserImpl(UUID.randomUUID().toString(), name, personalIdentificationNumber, true);
        return usersRepository.save(user); //LÃ¤gger till user save
    }

    @Override

    public User changeUser(String userId, Consumer<ChangeUser> changeUser) throws UseException {
     /*   if (usersRepository.all()
                .filter(user -> user.getPersonalIdentificationNumber().equals(userId))
                {
            throw new UseException(Activity.UPDATE_USER, UseExceptionType.USER_PERSONAL_ID_NOT_UNIQUE);
        }*/

        User user = usersRepository.getEntityById(userId)
                .orElseThrow(() -> new UseException(Activity.UPDATE_USER, UseExceptionType.NOT_FOUND));

        AtomicBoolean save = new AtomicBoolean( false);
        changeUser.accept(new ChangeUser() {

            @Override
            public void setName(String name) {
                user.setName(name);
                save.set(true);
            }

            @Override
            public void setPersonalIdentificationNumber(String personalIdentificationNumber) throws UseException {
                if (usersRepository.all()
                        .anyMatch(userToCheck -> userToCheck.getPersonalIdentificationNumber().equals(personalIdentificationNumber)))
                    throw new UseException(Activity.UPDATE_USER, UseExceptionType.USER_PERSONAL_ID_NOT_UNIQUE);

                user.setPersonalIdentificationNumber(personalIdentificationNumber);
                save.set(true);

            }
        });
        if(!save.get())
            return user;

        return usersRepository.save(user);
    }

    @Override
    public User inactivateUser(String userId) throws UseException {
        if (usersRepository.getEntityById(userId).isEmpty())
            throw new UseException(Activity.UPDATE_USER, UseExceptionType.NOT_FOUND);
        User user = usersRepository.getEntityById(userId).get();
        user.setActive(false);
        return usersRepository.save(user);
    }

    @Override
    public Optional<User> getUser(String userId) {

        return usersRepository.getEntityById(userId); // return Optional.of(user) ...alt 2
    }

    @Override
    public Stream<User> find(String searchString, Integer pageNumber, Integer pageSize, SortOrder sortOrder) {

        return usersRepository.all()
                .filter(user -> user.getName().toLowerCase().contains(searchString));
    }
}