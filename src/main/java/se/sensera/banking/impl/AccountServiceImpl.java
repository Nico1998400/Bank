package se.sensera.banking.impl;

import se.sensera.banking.*;
import se.sensera.banking.exceptions.Activity;
import se.sensera.banking.exceptions.UseException;
import se.sensera.banking.exceptions.UseExceptionType;
import se.sensera.banking.utils.ListUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
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
        User user = getUser(userId, Activity.CREATE_ACCOUNT);
        verifyDuplicateAccountName(accountName, user);
        AccountImpl account1 = new AccountImpl(user, accountName, true);

        return accountRepository.save(account1);
    }

    private User getUser(String userId, Activity createAccount) throws UseException {
        return usersRepository.getEntityById(userId).orElseThrow(() -> new UseException(createAccount, UseExceptionType.USER_NOT_FOUND));
    }

    private void verifyDuplicateAccountName(String accountName, User user) throws UseException {
        boolean notDuplicate = accountRepository.all().filter(x -> Objects.equals(x.getOwner().getName(), user.getName()))
                .anyMatch(x -> Objects.equals(x.getName(), accountName));

        if (notDuplicate) {
            throw new UseException(Activity.CREATE_ACCOUNT, UseExceptionType.ACCOUNT_NAME_NOT_UNIQUE);
        }
    }


    @Override
    public Account changeAccount(String userId, String accountId, Consumer<ChangeAccount> changeAccountConsumer) throws UseException {
        Account account = accountRepository.getEntityById(accountId).orElseThrow();
        User user = usersRepository.getEntityById(userId).orElseThrow();

        verifyOwner(account, user);
        AccountNameConsumer accountNameConsumer = new AccountNameConsumer(account, name -> accountRepository.all().anyMatch(account1 -> account1.getName().contains(name)));
        changeAccountConsumer.accept(accountNameConsumer);
        if (!accountNameConsumer.save)
            return account;
        return accountRepository.save(account);
    }

    private void verifyOwner(Account account, User user) throws UseException {
        if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }

        if (!account.isActive()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_ACTIVE);
        }
    }

    @Override
    public Account addUserToAccount(String userId, String accountId, String userIdToBeAssigned) throws UseException {
        User user = usersRepository.getEntityById(userId).orElseThrow();
        User userToAdd = usersRepository.getEntityById(userIdToBeAssigned).orElseThrow();

        if (accountRepository.getEntityById(accountId).isEmpty()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_FOUND);
        }

        Account account = accountRepository.getEntityById(accountId).get();

        if (!account.isActive()) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NOT_ACTIVE);
        }

        if (account.getOwner().equals(userToAdd)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.CANNOT_ADD_OWNER_AS_USER);
        } else if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }

        if (account.getUsers().anyMatch(user1 -> user1.equals(userToAdd))) {
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

        if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }

        if (account.getUsers().noneMatch(user1 -> user1.equals(userToAdd))) {
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
        User user = getUser(userId, Activity.INACTIVATE_ACCOUNT);

        if (!account.getOwner().equals(user)) {
            throw new UseException(Activity.INACTIVATE_ACCOUNT, UseExceptionType.NOT_OWNER);
        }
        if (!account.isActive()) {
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
        if (searchValue != null && !searchValue.isEmpty())
            accountStream = accountStream.filter(account -> account.getName().contains(searchValue.toLowerCase()));

        if (userId != null)
            accountStream = accountStream.filter(account -> account.getOwner().getId().equals(userId) || account.getUsers().anyMatch(user -> user.getId().equals(userId)));

        if (sortOrder.equals(SortOrder.AccountName))
            accountStream = accountStream.sorted(Comparator.comparing(Account::getName));

        return ListUtils.applyPage(accountStream, pageNumber, pageSize);
    }

    private static class AccountNameConsumer implements ChangeAccount {
        Account account;
        boolean save = false;
        Predicate<String> nameExist;

        public AccountNameConsumer(Account account, Predicate<String> nameExist) {
            this.account = account;
            this.nameExist = nameExist;
        }

        @Override
        public void setName(String name) throws UseException {
            if (Objects.equals(name, account.getName()))
                return;

            if (nameExist.test(name)) {
                throw new UseException(Activity.UPDATE_ACCOUNT, UseExceptionType.ACCOUNT_NAME_NOT_UNIQUE);
            }
            account.setName(name);
            save = true;
        }
    }
}

