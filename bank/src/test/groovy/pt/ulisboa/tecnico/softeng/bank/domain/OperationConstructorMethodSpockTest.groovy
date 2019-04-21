package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class OperationConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def bank
	@Shared def account
	@Shared def providerAccount

	@Override
	def populate4Test() {
		bank = new Bank('Money', 'BK01')
		def client = new Client(bank, 'António')
		account = new Account(bank, client)
		def providerClient = new Client(bank, 'José')
		providerAccount = new Account(bank, providerClient)
	}

	def 'success'() {
		when: 'when creating an operation'
		def operation = new Deposit(account, 1000)

		then: 'the object should hold the proper values'
		with(operation) {
			getReference().startsWith(bank.getCode())
			getReference().length() > Bank.CODE_SIZE
			getAccount() == account
			getValue() == 1000
			getTime() != null
			bank.getOperation(getReference()) == operation
		}
		and:
		operation instanceof Deposit
	}


	@Unroll('withdraw operation:, #acc, #value')
	def 'withdraw exception'() {
		when: 'when creating an invalid operation'
		new Withdraw(acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc     | value
		null    | 1000
		account | 0
		account | -1000
	}

	@Unroll('deposit operation: #acc, #value')
	def 'deposit exception'() {
		when: 'when creating an invalid operation'
		new Deposit(acc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc     | value
		null    | 1000
		account | 0
		account | -1000
	}

	def 'one amount'() {
		when:
		def operation = new Deposit(account, 1)

		then:
		bank.getOperation(operation.getReference()) == operation
	}

	@Unroll('transfer operation: #acc, #provAcc, #value')
	def 'transfer exception'() {
		when: 'creating an invalid operation'
		new Transfer(acc, provAcc, value)

		then: 'throw an exception'
		thrown(BankException)

		where:
		acc 	|  	provAcc 	  | value
		null	| providerAccount | 1000
		account | null			  | 1000
		account | providerAccount | 0
		account | providerAccount | -1000
	}
}
