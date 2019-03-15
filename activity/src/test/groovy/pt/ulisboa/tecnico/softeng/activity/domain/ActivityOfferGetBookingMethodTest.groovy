package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
//import org.junit.runner.RunWith
//import mockit.integration.junit4.JMockit

//@RunWith(JMockit)
def class ActivityOfferGetBookingMethodTest extends SpockRollbackTestAbstractClass {
	private static final String IBAN = "IBAN"
	private static final String NIF = "123456789"
	private ActivityProvider provider
	private ActivityOffer offer

	@Override
	def "populate4Test"() {
		provider = new ActivityProvider("XtremX","ExtremeAdventure","NIF",IBAN)
		Activity activity = new Activity(provider,"Bush Walking",18,80,3)
		LocalDate begin = new LocalDate(2016,12,19)
		LocalDate end = new LocalDate(2016,12,21)
		offer = new ActivityOffer(activity,begin,end,30)
	}

	def "success"() {
		when:
		Booking booking = new Booking(provider,offer,NIF,IBAN)

		then:
		offer.getBooking(booking.getReference()) == booking
	}

	def "success cancelled"() {
		given:
		Booking booking = new Booking(provider,offer,NIF,IBAN)

		when:
		booking.cancel()

		then:
		offer.getBooking(booking.getCancel()) == booking
	}

	def "does not exist"() {
		when:
		new Booking(provider,offer,NIF,IBAN)

		then:
		offer.getBooking("XPTO") == null
	}

}
