package se.sensera.banking.impl;

import se.sensera.banking.*;
import se.sensera.banking.exceptions.Activity;
import se.sensera.banking.exceptions.UseException;
import se.sensera.banking.exceptions.UseExceptionType;
import se.sensera.banking.utils.ListUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountServiceImpl implements AccountService {
    AccountsRepository accountRepository;
    UsersRepository usersRepository;

    public AccountServiceImpl(UsersRepository usersRepository, AccountsRepository accountsRepository) {
        this.accountRepository = accountsRepository;
        this.usersRepository = usersRepository;

    }

    @Override
    public Account createAccount(String userId, String accountName) throws UseException {
        User user = usersRepository.getEntityById(userId).orElseThrow(()-> new UseException(Activity.CREATE_ACCOUNT, UseExceptionType.USER_NOT_FOUND));
        AccountImpl account1 = new AccountImpl(user,accountName,true);

        boolean notDuplicate = accountRepository.all().filter(x -> Objects.equals(x.getOwner().getName(),user.getName()))
                .anyMatch(x->Objects.equals(x.getName(),accountName));

        if (notDuplicate) {
            throw new UseException(Activity.CREATE_ACCOUNT,UseExceptionType.ACCOUNT_NAME_NOT_UNIQUE);
        }

        return accountRepository.save(account1);
    }

    @Override
    public Account changeAccount(String userId, String accountId, Consumer<ChangeAccount> changeAccountConsumer) throws UseException {
        Account account = accountRepository.getEntityById(accountId).orElseThrow();
        User user = usersRepository.getEntityById(userId).orElseThrow();

        if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }
        if (!account.isActive()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_ACTIVE);
        }

        changeAccountConsumer.accept(name -> {
            if (Objects.equals(name,account.getName())){
                System.out.println("Name success");
            } else {
                if (accountRepository.all().anyMatch(account1 -> account1.getName().contains(name))) {
                    throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NAME_NOT_UNIQUE);
                }
                account.setName(name);
                accountRepository.save(account);
            }



        });

        return account;
    }

    @Override
    public Account addUserToAccount(String userId, String accountId, String userIdToBeAssigned) throws UseException {
        User user = usersRepository.getEntityById(userId).orElseThrow();
        User userToAdd = usersRepository.getEntityById(userIdToBeAssigned).orElseThrow();

        if (accountRepository.getEntityById(accountId).isEmpty()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_FOUND);
        }

        Account account = accountRepository.getEntityById(accountId).orElseThrow();

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
        Account account = accountRepository.getEntityById(accountId).orElseThrow();
        User user = usersRepository.getEntityById(userId).orElseThrow();
        User userToAdd = usersRepository.getEntityById(userIdToBeAssigned).orElseThrow();

        if (!account.getOwner().equals(user)){
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }

        if (account.getUsers().noneMatch(user1 -> user1.equals(userToAdd) )) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.USER_NOT_ASSIGNED_TO_THIS_ACCOUNT);
        }


        account.removeUser(userToAdd);
        return accountRepository.save(account);

    }


    @Override
    public Account inactivateAccount(String userId, String accountId) throws UseException {
        if (accountRepository.getEntityById(accountId).isEmpty())
            throw new UseException(Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_FOUND);

        Account account = accountRepository.getEntityById(accountId).orElseThrow();
        User user = usersRepository.getEntityById(userId).orElseThrow(()-> new UseException(Activity.INACTIVATE_ACCOUNT, UseExceptionType.USER_NOT_FOUND));

        if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }
        if (!account.isActive()){
            throw new UseException(Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_ACTIVE);
        }

        if (!account.getOwner().isActive()) {
            throw new UseException(Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_ACTIVE);
        }


        account.setActive(false);

        return accountRepository.save(account);
    }


    @Override
    public Stream<Account> findAccounts(String searchValue, String userId, Integer pageNumber, Integer pageSize, SortOrder sortOrder) throws UseException {

        Stream<Account> accountStream = accountRepository.all();

        accountStream = accountStream.filter(account -> account.getName().contains(searchValue.toLowerCase()));
        accountStream = accountStream.sorted(Comparator.comparing(Account::getName));

        return ListUtils.applyPage(accountStream, pageNumber, pageSize);
    }
}
