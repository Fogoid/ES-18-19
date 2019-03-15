package pt.ulisboa.tecnico.softeng.activity.services.local

import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.services.local.ActivityInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException;

class ActivityInterfaceGetActivityReservationDataMethodTest extends SpockRollbackTestAbstractClass {

    def NAME = "ExtremeAdventure"
    def CODE = "XtremX"
    LocalDate begin = new LocalDate(2016, 12, 19)
    LocalDate end = new LocalDate(2016, 12, 21)
    private ActivityProvider provider
    private ActivityOffer offer
    private Booking booking

    def populate4Test() {
        provider = new ActivityProvider(CODE, NAME, "NIF", "IBAN")
        Activity activity = new Activity(this.provider, "Bush Walking", 18, 80, 3)
        offer = new ActivityOffer(activity, begin, end, 30)
    }

    def 'success'() {

        given:
        booking = new Booking(provider, offer, "123456789", "IBAN")

        when:
        RestActivityBookingData data = ActivityInterface.getActivityReservationData(this.booking.getReference());

        then:
        booking.getReference() == data.getReference()
        data.getCancellation() == null
        NAME == data.getName()
        CODE == data.getCode()
        begin == data.getBegin()
        end == data.getEnd()
        data.getCancellationDate() == null
    }


    def 'successCancelled'() {

        given:
        booking = new Booking(provider, offer, "123456789", "IBAN")

        when:
        provider.getProcessor().submitBooking(booking)
        booking.cancel()
        RestActivityBookingData data = ActivityInterface.getActivityReservationData(booking.getCancel())

        then:
        booking.getReference() == data.getReference()
        booking.getCancel() == data.getCancellation()
        NAME == data.getName()
        CODE == data.getCode()
        begin == data.getBegin()
        end == data.getEnd()
        data.getCancellationDate() != null
    }


    def 'nullReference'() {

        when:
        ActivityInterface.getActivityReservationData(null)

        then:
        thrown(ActivityException)
    }


    def 'emptyReference'() {

        when:
        ActivityInterface.getActivityReservationData("")

        then:
        thrown(ActivityException)
    }


    def 'notExistsReference'() {

        when:
        ActivityInterface.getActivityReservationData("XPTO")

        then:
        thrown(ActivityException)
    }

}
