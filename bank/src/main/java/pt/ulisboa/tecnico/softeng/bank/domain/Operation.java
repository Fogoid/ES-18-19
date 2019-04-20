package pt.ulisboa.tecnico.softeng.bank.domain;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public abstract class Operation extends Operation_Base {
	public static enum Type {
		DEPOSIT, WITHDRAW, TRANSFER
	}

	public Operation() {
	}

	public void delete() {
		setBank(null);
		setAccount(null);

		deleteDomainObject();
	}

	protected void checkArguments(Type type, Account account, long value) {
		if (type == null || account == null || value <= 0) {
			throw new BankException();
		}
	}

	public abstract String revert();
	public abstract String getName();

}
