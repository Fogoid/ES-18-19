package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class AccountConstructorMethodTest extends SpockRollbackTestAbstractClass {
	private Bank bank
	private Client client

	@Override
	def populate4Test() {
		bank = new Bank("Money","BK01")

		client = new Client(bank,"António")
	}

	def "success"() {
		when:
		Account account=new Account(bank, client)

		then:
		account.getBank() == bank
		account.getIBAN().startsWith(bank.getCode())
		account.getClient() == client
		0.0d == account.getBalance()
		bank.getAccountSet().size() == 1
		bank.getClientSet().contains(client)
	}

	// JFF: could have uped a datatable
	def "not empty bank argument"() {
		when:
		new Account(null, client)

		then:
		thrown(BankException)
	}

	def "not empty client argument"() {
		when:
		new Account(bank,null)

		then:
		thrown(BankException)
	}

	def "client does not belong to bank"() {
		when:
		Client allien = new Client(new Bank("MoneyPlus","BK02"),"António")

		new Account(bank,allien)

		then:
		thrown(BankException)
	}

}
