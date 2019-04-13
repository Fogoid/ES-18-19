package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

class TaxPaymentStateMethodTest extends  SpockRollbackTestAbstractClass {

    def bankInterface
    def taxInterface

    private Adventure adventure

    // JFF: some tests could use data tables
    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)

        def broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, null, bankInterface, null, null, taxInterface)
        def client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        this.adventure = new Adventure(broker, this.BEGIN, this.END, client, MARGIN)

        this.adventure.setState(State.PROCESS_PAYMENT)
    }

    def "reaching TaxPayment state"() {
        given:
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION

        when:
        this.adventure.process()

        then:
        State.TAX_PAYMENT == this.adventure.getState().getValue()

    }

    def "successful payment"() {
        given:
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when:
        this.adventure.process()
        this.adventure.process()

        then:
        State.CONFIRMED == this.adventure.getState().getValue()
    }

    def "unsuccessful payment receiving tax exception"() {
        given:
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> { throw new TaxException() }

        when:
        this.adventure.process()
        this.adventure.process()

        then:
        State.UNDO == this.adventure.getState().getValue()
    }

    def "unsuccessful payment having max number of remote access exception"() {
        this.adventure.setState(State.TAX_PAYMENT)

        when:
        for( def i = 0; i < TaxPaymentState.MAX_REMOTE_ERRORS; i++)
            this.adventure.process()

        then:
        3*taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }
        and:
        State.UNDO == this.adventure.getState().getValue()
    }

    def "payment having max-1 remote access exceptions"() {
        this.adventure.setState(State.TAX_PAYMENT)

        when:
        for( def i = 0; i < TaxPaymentState.MAX_REMOTE_ERRORS-1; i++)
            this.adventure.process()

        then:
        2*taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }
        and:
        State.TAX_PAYMENT == this.adventure.getState().getValue()
    }

    def "successful payment havind max-1 remote access exceptions"() {
        this.adventure.setState(State.TAX_PAYMENT)

        when:
        for( def i = 0; i < TaxPaymentState.MAX_REMOTE_ERRORS; i++)
            this.adventure.process()

        then:
        2*taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }
        and:
        1*taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and:
        State.CONFIRMED == this.adventure.getState().getValue()
    }

    def "one taxException and two remote access exceptions"() {
        this.adventure.setState(State.TAX_PAYMENT)

        when:
        for( def i = 0; i < TaxPaymentState.MAX_REMOTE_ERRORS; i++)
            this.adventure.process()

        then:
        2*taxInterface.submitInvoice(_) >> { throw new RemoteAccessException() }
        and:
        1*taxInterface.submitInvoice(_) >> { throw new TaxException() }
        and:
        State.UNDO == this.adventure.getState().getValue()

    }


}
