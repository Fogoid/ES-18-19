package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Transfer extends Transfer_Base {
    
    public Transfer(Account sourceAccount, Account targetAccount, long value) {
        checkArguments(sourceAccount, targetAccount, value);

        setReference(sourceAccount.getBank().getCode() + Integer.toString(sourceAccount.getBank().getCounter()));
        setValue(value);
        setTime(DateTime.now());

        setAccount(sourceAccount);
        setTargetAccount(targetAccount);

        setBank(sourceAccount.getBank());
    }

    private void checkArguments(Account sourceAccount, Account targetAccount, double value) {
        if (sourceAccount == null || targetAccount == null || value <= 0) {
            throw new BankException();
        }
    }

    @Override
    public String revert() {
        setCancellation(getReference() + "_CANCEL");

        return getTargetAccount().transfer(getAccount(), getValue()).getReference();
    }

    @Override
    public String getName() { return "TRANSFER"; }


}
