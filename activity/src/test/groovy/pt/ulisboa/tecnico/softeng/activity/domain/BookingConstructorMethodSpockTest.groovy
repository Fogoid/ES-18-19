package pt.ulisboa.tecnico.softeng.activity.domain

import mockit.FullVerifications
import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Shared
import spock.lang.Unroll;


class BookingConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    ActivityProvider provider
    ActivityOffer offer
    @Shared
    def AMOUNT = 30
    @Shared
    def IBAN = "IBAN"
    @Shared
    def NIF = "123456789"

    @Override
    def 'populate4Test'() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN);
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3);
        def begin = LocalDate.parse('2016-12-19')
        def end = LocalDate.parse('2016-12-21')

        offer = new ActivityOffer(activity, begin, end, AMOUNT)
    }

    def 'success'() {
        when:
        def booking = new Booking(provider, offer, NIF, IBAN)

        then:
        booking.getReference().startsWith(provider.getCode())
        booking.getReference().length() > ActivityProvider.CODE_SIZE
        offer.getNumberActiveOfBookings() == 1
        booking.getBuyerNif() == NIF
        booking.getIban() == IBAN
        booking.getAmount() == AMOUNT == 0

    }

    @Unroll("Booking: #provider ,#offer,#NIF,#IBAN")
    def 'exceptions'(){
        when:
        new Booking(_provider,_offer,_nif,_iban)

        then:
        thrown(ActivityException)

        where:
        _provider | _offer | _nif | _iban
        null      | offer  | NIF  | IBAN
        provider  | null   | NIF  | IBAN
        null      | offer  | null | IBAN
        provider  | null   | NIF  | ""
        provider  | null   | NIF  | null
        null      | offer  | ""   | IBAN
    }


    def 'bookingEqualCapacity'() {
        given:
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        when:
        new Booking(provider, offer, NIF, IBAN)
        then:
        def error = thrown(ActivityException)
        and:
        offer.getNumberActiveOfBookings() == 3
    }

    def 'bookingEqualCapacityButHasCancelled'() {
        when:
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        def booking = new Booking(provider, offer, NIF, IBAN)
        booking.cancel()
        new Booking(provider, offer, NIF, IBAN)

        then:
        offer.getNumberActiveOfBookings() == 3
    }

}
