package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Deposit extends Deposit_Base {
    
    public Deposit(Account account, long value) {
        super.checkArguments(Type.DEPOSIT, account, value);

        setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
        setValue(value);
        setTime(DateTime.now());

        setAccount(account);

        setBank(account.getBank());
    }

    @Override
    public String revert() {
        setCancellation(getReference() + "_CANCEL");

        return getAccount().withdraw(getValue()).getReference();
    }

    @Override
    public String getName() { return "DEPOSIT"; }
    
}
