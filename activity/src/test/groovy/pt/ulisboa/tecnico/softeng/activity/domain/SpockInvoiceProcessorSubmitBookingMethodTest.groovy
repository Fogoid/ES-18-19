package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate


import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.activity.services.remote.exceptions.TaxException
import spock.lang.Unroll

class SpockInvoiceProcessorSubmitBookingMethodTest extends SpockRollbackTestAbstractClass {
    def CANCEL_PAYMENT_REFERENCE = "CancelPaymentReference"
    def INVOICE_REFERENCE = "InvoiceReference"
    def PAYMENT_REFERENCE = "PaymentReference"
    def AMOUNT = 30
    def IBAN = "IBAN"
    def NIF = "123456789"
    def provider
    def offer
    def booking
    def newBooking
    def taxInterface
    def bankInterface
    def begin = LocalDate.parse("2016-12-19")
    def end = LocalDate.parse("2016-12-21")

    @Override
    def populate4Test() {

        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        def processor = new Processor(bankInterface, taxInterface)

        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN, processor);
        Activity activity = new Activity(provider, "Bush Walking", 18, 80, 10);

        offer = new ActivityOffer(activity, begin, end, AMOUNT)
        booking = new Booking(provider, offer, NIF, IBAN)
        newBooking = new Booking(this.provider, this.offer, this.NIF, this.IBAN)
    }


    def 'success'() {

        given:
        bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when:
        provider.getProcessor().submitBooking(booking)

        then:
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE
    }


    @Unroll
    def 'one failure on submit invoice'() {

        when:
        this.provider.getProcessor().submitBooking(this.booking)

        then:
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and:
        1 * taxInterface.submitInvoice(_) >> {throw exception}
        and:
        this.booking.paymentReference == PAYMENT_REFERENCE
        this.booking.invoiceReference == null

        when:
        this.provider.getProcessor().submitBooking(newBooking)

        then:
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and:
        2 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        and:
        this.booking.paymentReference == PAYMENT_REFERENCE
        this.booking.invoiceReference == INVOICE_REFERENCE
        this.booking.paymentReference == PAYMENT_REFERENCE
        this.booking.invoiceReference == INVOICE_REFERENCE

        where:
        exception                   | failure
        new TaxException()          | 'tax exception'
        new RemoteAccessException() | 'remote access exception'
    }


@Unroll
def 'oneFailureOnProcessPayment'() {

    when:
    this.provider.getProcessor().submitBooking(booking)

    then:
    1 * bankInterface.processPayment(_) >> {throw exception}

    and:
    0 * taxInterface.submitInvoice(_)

    and:
    this.booking.paymentReference == null
    this.booking.invoiceReference == null

    when:
    this.provider.getProcessor().submitBooking(newBooking)

    then:
    2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE

    where:
    exception                   | failure
    new BankException()         | 'bank exception'
    new RemoteAccessException() | 'remote access exception'
}


    @Unroll
    def 'successCancel'() {

        when:
        provider.getProcessor().submitBooking(booking)

        then:
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE

        when:
        booking.cancel()

        then:
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE

        and:
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        and:
        booking.cancelledPaymentReference == CANCEL_PAYMENT_REFERENCE

        and:
        booking.cancelledInvoice

        and:
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE

    }


    @Unroll
    def 'oneFailureOnCancelPayment'() {

        when: 'a successful renting'
        this.provider.getProcessor().submitBooking(this.booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the renting'
        this.booking.cancel()

        then: 'a BankException is thrown'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> { throw exception }
        and: 'the cancel of the invoice is not done'
        0 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when: 'a new renting is done'
        this.provider.getProcessor().submitBooking(newBooking)

        then: 'renting one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        and: 'renting two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)

        where:
        exception                   | failure
        new BankException()         | 'bank exception'
        new RemoteAccessException() | 'remote access exception'
    }


    @Unroll
    def 'oneFailureOnCancelInvoice'() {

        when: 'a successful renting'
        this.provider.getProcessor().submitBooking(this.booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when:
        this.booking.cancel()

        then:
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> { throw exception }
        and: 'the cancel of the invoice is not done'
        0 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        when: 'a new renting is done'
        this.provider.getProcessor().submitBooking(newBooking)

        then: 'renting one is completely cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)

        and: 'renting two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)

        where:
        exception                   | failure
        new BankException()         | 'bank exception'
        new RemoteAccessException() | 'remote access exception'

    }



}