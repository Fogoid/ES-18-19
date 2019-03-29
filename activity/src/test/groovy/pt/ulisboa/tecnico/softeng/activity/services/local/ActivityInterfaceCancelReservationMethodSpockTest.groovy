package pt.ulisboa.tecnico.softeng.activity.services.local
import org.joda.time.LocalDate

import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.domain.RollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData
import spock.lang.Unroll


class ActivityInterfaceCancelReservationMethodSpockTest extends SpockRollbackTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "123456789"
    def provider
    def offer
    def bankInterface
    def taxInterface

    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
        def activity = new Activity(provider,'Bush Walking',18,80,3)
        LocalDate begin = new LocalDate(2016, 12, 19)
        LocalDate end = new LocalDate(2016, 12, 21)

        offer = new ActivityOffer(activity,begin,end,30)
    }

    def 'success'() {
        given:
                _*bankInterface.processPayment(_ as RestBankOperationData)
                _*taxInterface.submitInvoice(_ as RestInvoiceData)


        Booking booking = new Booking(provider, offer, NIF, IBAN)
        provider.getProcessor().submitBooking(booking)

        when:
        String cancel = ActivityInterface.cancelReservation(booking.getReference())

        then:
        booking.isCancelled()
        cancel == booking.getCancel()
    }

    def 'doesNotExist'(){
        given:
        _*bankInterface.processPayment(_ as RestBankOperationData)
        _*taxInterface.submitInvoice(_ as RestInvoiceData)

        when:
        provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
        ActivityInterface.cancelReservation("XPTO")

        then:
        thrown(ActivityException)
    }
}

