package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

public class Withdraw extends Withdraw_Base {
    
    public Withdraw(Account account, long value) {
        super.checkArguments(Type.WITHDRAW, account, value);

        setReference(account.getBank().getCode() + Integer.toString(account.getBank().getCounter()));
        setValue(value);
        setTime(DateTime.now());

        setAccount(account);

        setBank(account.getBank());
    }

    @Override
    public String revert() {
        setCancellation(getReference() + "_CANCEL");

        return getAccount().deposit(getValue()).getReference();
    }

    @Override
    public String getName() { return "WITHDRAW"; }

}
