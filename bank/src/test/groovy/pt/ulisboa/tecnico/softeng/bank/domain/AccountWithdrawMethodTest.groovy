package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class AccountWithdrawMethodTest extends SpockRollbackTestAbstractClass {
	private Bank bank
	private Account account

	@Override
	def "populate4Test"() {
		bank = new Bank("Money","BK01")
		Client client = new Client(bank,"António")
		account = new Account(bank,client)
		account.deposit(100)
	}

	def "success"() {
		when:
		String reference = account.withdraw(40).getReference()

		then:
		account.getBalance() - 60 == 0
		Operation operation = bank.getOperation(reference)

		operation != null
		operation.getType() == Operation.Type.WITHDRAW
		operation.getAccount() == account
		operation.getValue() - 40 == 0
	}

	def "negative amount"() {
		when:
		account.withdraw(-20)

		then:
		thrown(BankException)
	}

	def "zero amount"() {
		when:
		account.withdraw(0)

		then:
		thrown(BankException)
	}

	def "one amount"() {
		when:
		account.withdraw(1)

		then:
		account.getBalance() - 99 == 0
	}

	def "equal to balance"() {
		when:
		account.withdraw(100)

		then:
		account.getBalance() - 0 == 0
	}

	def "equal to balance plus one"() {
		when:
		account.withdraw(101)

		then:
		thrown(BankException)
	}

	def "more than balance"() {
		when:
		account.withdraw(150)

		then:
		thrown(BankException)
	}

}
