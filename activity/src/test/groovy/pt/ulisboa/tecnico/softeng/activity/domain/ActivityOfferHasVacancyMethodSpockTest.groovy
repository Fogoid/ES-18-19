package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import org.junit.Assert
import org.junit.Test
import mockit.Expectations
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestInvoiceData

class ActivityOfferHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {

    private static final String IBAN = "IBAN"
    private static final String NIF = "123456789"
    private ActivityProvider provider
    private ActivityOffer offer

	def bankInterface

	def taxInterface

    @Override
    def "populate4Test"() {

		bankInterface = Mock(BankInterface)
		taxInterface = Mock(TaxInterface)

        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", IBAN)
        def activity = new Activity(this.provider, "Bush Walking", 18, 80, 3)

        LocalDate begin = new LocalDate(2016, 12, 19)
        LocalDate end = new LocalDate(2016, 12, 21)

        offer = new ActivityOffer(activity, begin, end, 30)
    }

    @Test
    def success() {
        when:
        new Booking(this.provider, this.offer, NIF, IBAN)

        then:
        offer.hasVacancy()
    }

    @Test
    def "bookingIsFull"() {

        when:
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)

        then:
        !offer.hasVacancy()
    }

    @Test
    def "bookingIsFullMinusOne"() {

        when:
        new Booking(provider, offer, NIF, IBAN)
        new Booking(provider, offer, NIF, IBAN)

        then:
        offer.hasVacancy()
    }

    @Test

	def "hasCancelledBookings"() {

		given:
		_*bankInterface.processPayment(_ as RestBankOperationData)

		_*taxInterface.submitInvoice(_ as RestInvoiceData)


		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		Booking booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		when:
		booking.cancel()

		then:
		offer.hasVacancy()
	}

	def "hasCancelledBookingsButFull"() {
		given:
		_*bankInterface.processPayment(_ as RestBankOperationData)

		_*taxInterface.submitInvoice(_ as RestInvoiceData)


		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		provider.getProcessor().submitBooking(new Booking(provider, offer, NIF, IBAN))
		Booking booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		when:
		booking.cancel()
		booking = new Booking(provider, offer, NIF, IBAN)
		provider.getProcessor().submitBooking(booking)

		then:
		!offer.hasVacancy()
	}

}
