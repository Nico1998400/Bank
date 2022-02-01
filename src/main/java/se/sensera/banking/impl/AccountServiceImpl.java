package se.sensera.banking.impl;
import se.sensera.banking.AccountsRepository;
import se.sensera.banking.UsersRepository;
import se.sensera.banking.*;
import se.sensera.banking.exceptions.Activity;
import se.sensera.banking.exceptions.UseException;
import se.sensera.banking.exceptions.UseExceptionType;

import javax.print.attribute.UnmodifiableSetException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountServiceImpl implements AccountService {
    private final AccountsRepository accountRepository;
    private final UsersRepository usersRepository;

    public AccountServiceImpl(UsersRepository usersRepository, AccountsRepository accountsRepository) {
        this.accountRepository = accountsRepository;
        this.usersRepository = usersRepository;

    }
    @Override
    public Account createAccount(String userId, String accountName) throws UseException {
        return null;
    }

    @Override
    public Account changeAccount(String userId, String accountId, Consumer<ChangeAccount> changeAccountConsumer) throws UseException {
        return null;
    }

    @Override
    public Account addUserToAccount(String userId, String accountId, String userIdToBeAssigned) throws UseException {
        User user = usersRepository.getEntityById(userId).orElseThrow();
        User userToAdd = usersRepository.getEntityById(userIdToBeAssigned).get();

        if (accountRepository.getEntityById(accountId).isEmpty()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_FOUND);
        }

        Account account = accountRepository.getEntityById(accountId).get();

        if (!account.isActive()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NOT_ACTIVE);
        }

        if (account.getOwner().equals(userToAdd)){
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.CANNOT_ADD_OWNER_AS_USER);
        }

        else if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }

        if (account.getUsers().anyMatch(user1 -> user1.equals(userToAdd))){
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.USER_ALREADY_ASSIGNED_TO_THIS_ACCOUNT);
        }

        account.addUser(userToAdd);
        return accountRepository.save(account); // Ger ett nytt account tillbaka

    }

    @Override
    public Account removeUserFromAccount(String userId, String accountId, String userIdToBeAssigned) throws UseException {
        return null;
    }

    @Override
    public Account inactivateAccount(String userId, String accountId) throws UseException {
        return null;
    }

    @Override
    public Stream<Account> findAccounts(String searchValue, String userId, Integer pageNumber, Integer pageSize, SortOrder sortOrder) throws UseException {
        return null;
    }
}
