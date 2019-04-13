package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException


def class ProcessPaymentStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

	def taxInterface
	def bankInterface

	private Broker broker
	private Client client
	private Adventure adventure

	def PAYMENT_CONFIRMATION


	// JFF: could have used a data table
	@Override
	def 'populate4Test'() {

		taxInterface = Mock(TaxInterface)
		bankInterface = Mock(BankInterface)

		broker=new Broker("BR01","eXtremeADVENTURE",BROKER_NIF_AS_SELLER,NIF_AS_BUYER,BROKER_IBAN, null, bankInterface, null, null, taxInterface)
		client=new Client(broker,CLIENT_IBAN,CLIENT_NIF,DRIVING_LICENSE,AGE)
		adventure=new Adventure(broker,BEGIN,END,client,MARGIN)
		adventure.setState(State.PROCESS_PAYMENT)
	}

	def 'success'() {

		given:

		broker.getBankInterface().processPayment(_) >> PAYMENT_CONFIRMATION

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.TAX_PAYMENT
	}

	def 'bank exception'() {
		given:
		broker.getBankInterface().processPayment(_) >> {throw new BankException()}

		when:
		adventure.process()
		adventure.process()

		then:
		adventure.getState().getValue() == State.CANCELLED
	}

	def 'single remote access exception'() {
		given:
		broker.getBankInterface().processPayment(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.PROCESS_PAYMENT
	}

	def 'max remote access exception'() {
		given:
		broker.getBankInterface().processPayment(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()

		then:
		adventure.getState().getValue() == State.CANCELLED
	}

	def 'max minus one remote access exception'() {
		given:
		broker.getBankInterface().processPayment(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()
		adventure.process()

		then:
		adventure.getState().getValue() == State.PROCESS_PAYMENT
	}

	def 'two remote access exception one success'() {
		when:
		adventure.process()
		adventure.process()
		adventure.process()



		then:
		2*broker.getBankInterface().processPayment(_) >> {throw new RemoteAccessException()}
		and:
		1*broker.getBankInterface().processPayment(_) >> PAYMENT_CONFIRMATION
		and:
		adventure.getState().getValue() == State.TAX_PAYMENT
	}

	def 'one remote access exception one bank exception'() {
		when:
		adventure.process()
		adventure.process()
		adventure.process()

		then:
		1*broker.getBankInterface().processPayment(_) >> {throw new RemoteAccessException()}
		and:
		1*broker.getBankInterface().processPayment(_) >> {throw new BankException()}
		and:
		adventure.getState().getValue() == State.CANCELLED
	}

}
