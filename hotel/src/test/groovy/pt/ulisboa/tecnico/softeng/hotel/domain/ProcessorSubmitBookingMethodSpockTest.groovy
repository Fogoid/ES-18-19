package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestInvoiceData
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.BankException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.RemoteAccessException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.exceptions.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class ProcessorSubmitBookingMethodSpockTest extends SpockRollbackTestAbstractClass{
    @Shared def arrival = new LocalDate(2016, 12, 19)
    @Shared def departure = new LocalDate(2016, 12, 24)
    @Shared def arrivalTwo = new LocalDate(2016, 12, 25)
    @Shared def departureTwo = new LocalDate(2016, 12, 28)
    @Shared def NIF_HOTEL = "123456700"
    @Shared def NIF_BUYER = "123456789"
    @Shared def IBAN_BUYER = "IBAN_BUYER"

    @Shared def CANCEL_PAYMENT_REFERENCE = 'CancelPaymentReference'
    @Shared def PAYMENT_REFERENCE="payment_reference"
    @Shared def INVOICE_REFERENCE = 'InvoiceReference'

    @Shared def hotel
    @Shared def room
    @Shared def booking
    def newBooking

    def taxInterface
    def bankInterface
    @Override
    def populate4Test (){
        taxInterface = Mock(TaxInterface)
        bankInterface = Mock(BankInterface)
        def processor= new Processor(bankInterface,taxInterface)

        hotel=new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0,processor)
        room = new Room(hotel, "01", Room.Type.SINGLE)
        booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)
        newBooking =new Booking(this.room, this.arrivalTwo, this.departureTwo,this.NIF_BUYER, this.IBAN_BUYER)

    }

    def 'success'(){
        given:
        bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when:
        hotel.getProcessor().submitBooking(booking)
        then:
        booking.paymentReference == PAYMENT_REFERENCE
        booking.invoiceReference == INVOICE_REFERENCE

    }


    @Unroll
    def 'one failure on submit invoice'(){
        when:
        this.hotel.getProcessor().submitBooking(this.booking)

        then:
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        and:
        1 * taxInterface.submitInvoice(_) >> {throw exception}
        and:
        this.booking.paymentReference == PAYMENT_REFERENCE
        this.booking.invoiceReference == null

        when:
        this.hotel.getProcessor().submitBooking(newBooking)

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
    def 'one failure on process payment'(){
        when:
        this.hotel.getProcessor().submitBooking(this.booking)

        then:
        1 * bankInterface.processPayment(_) >> {throw exception}
        and:
        0 * taxInterface.submitInvoice(_)
        and:
        this.booking.paymentReference == null
        this.booking.invoiceReference == null

        when:
        this.hotel.getProcessor().submitBooking( newBooking)

        then:
        2 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE

        where:
        exception                   | failure
        new BankException()          | 'bank exception'
        new RemoteAccessException() | 'remote access exception'
    }



    def 'successCancel' (){
        when:
        hotel.getProcessor().submitBooking(booking)

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

    @Unroll('the #failure occurred')
    def 'one failure on cancel payment'() {
        when: 'a successful renting'
        this.hotel.getProcessor().submitBooking(this.booking)

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
        this.hotel.getProcessor().submitBooking(newBooking)

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

    @Unroll('the #failure occurred')
    def 'one failure on cancel invoice'() {
        when: 'a successful renting'
        this.hotel.getProcessor().submitBooking(this.booking)

        then: 'the remote invocations succeed'
        1 * bankInterface.processPayment(_) >> PAYMENT_REFERENCE
        1 * taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when: 'cancelling the renting'
        this.booking.cancel()

        then: 'the payment is cancelled'
        1 * bankInterface.cancelPayment(PAYMENT_REFERENCE) >> CANCEL_PAYMENT_REFERENCE
        and: 'the cancel of the invoice throws a TaxException'
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE) >> { throw exception }

        when: 'a new renting is done'
        this.hotel.getProcessor().submitBooking(newBooking)

        then: 'renting one is completely cancelled'
        0 * bankInterface.cancelPayment(PAYMENT_REFERENCE)
        1 * taxInterface.cancelInvoice(INVOICE_REFERENCE)
        and: 'renting two is completed'
        1 * bankInterface.processPayment(_)
        1 * taxInterface.submitInvoice(_)

        where:
        exception                   | failure
        new TaxException()          | 'tax exception'
        new RemoteAccessException() | 'remote access exception'
    }




}
