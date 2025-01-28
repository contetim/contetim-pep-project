package Service;

import DAO.AccountDAO;
import Model.Account;
import java.sql.SQLException;

public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account createAccount(Account account) throws SQLException {
        if (account.getUsername().isBlank() || account.getPassword().length() < 4 || accountDAO.accountExists(account.getUsername())) {
            return null;
        }
        return accountDAO.createAccount(account);
    }

    public Account login(String username, String password) throws SQLException {
        return accountDAO.getAccountByUsernameAndPassword(username, password);
    }

    public boolean doesAccountExist (int accountId){
        return accountDAO.getAccountById(accountId) != null;
    }
}
