package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class AccountDepositMethodTest extends SpockRollbackTestAbstractClass {
	private Bank bank
	private Account account

	@Override
	def "populate4Test"() {
		bank = new Bank("Money","BK01")
		Client client = new Client(bank,"António")
		account = new Account(bank,client)
	}

	def "success"() {
		when:
		String reference = account.deposit(50).getReference()

		then:
		account.getBalance() - 50 == 0
		Operation operation=bank.getOperation(reference)

		operation != null
		operation.getType() == Operation.Type.DEPOSIT
		account == operation.getAccount()
		operation.getValue() - 50 == 0
	}

	def "zero amount"() {
		when:
		account.deposit(0)

		then:
		thrown(BankException)
	}

	def "one amount"() {
		given:
		account.deposit(1)

	}

	def "negative amount"() {
		when:
		account.deposit(-100)

		then:
		thrown(BankException)
	}

}
