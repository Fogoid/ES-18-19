package pt.ulisboa.tecnico.softeng.hotel.domain

//import mockit.Mocked
//import mockit.integration.junit4.JMockit
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
//import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
//import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

import org.joda.time.LocalDate
import spock.lang.Shared
import spock.lang.Unroll

//@RunWith(JMockit.class)
def class BookingConflictMethodSpockTest extends SpockRollbackTestAbstractClass {
	@Shared def arrival =  LocalDate.parse('2016-12-19')
	@Shared def departure =  LocalDate.parse('2016-12-24')
	def NIF_HOTEL = "123456700"
	def NIF_BUYER = "123456789"
	def IBAN_BUYER = "IBAN_BUYER"

	private Booking booking

	/* Not important for the current sprint
	@Mocked
	private TaxInterface taxInterface
	@Mocked
	private BankInterface bankInterface
	*/

	@Override
	def populate4Test() {
		def hotel = new Hotel("XPTO123", "Londres", this.NIF_HOTEL, "IBAN", 20.0, 30.0)
		def room = new Room(hotel, "01", Room.Type.SINGLE)

		booking = new Booking(room, arrival, departure, NIF_BUYER, IBAN_BUYER)
	}

	@Unroll('this.booking.conflict: #arrivalDate, #departureDate')
	def 'conflicts not expected on bookings'() {
		expect:
		!this.booking.conflict(arrivalDate, departureDate)

		where:
		arrivalDate << [new LocalDate(2016, 12, 9), arrival.minusDays(10), arrival.minusDays(10),
						departure.plusDays(4), departure]
		departureDate << [new LocalDate(2016, 12, 15), arrival.minusDays(4), arrival,
						  departure.plusDays(10), departure.plusDays(10)]
	}

	def 'check if no conflict when cancelled'() {
		when: 'the booking is cancelled'
		this.booking.cancel()

		then: 'it is expected to no conflicts exist'
		!this.booking.conflict(this.booking.getArrival(), this.booking.getDeparture())
	}

	def 'inconsistent arguments'() {
		when: 'there is a conflict with the booking'
		this.booking.conflict(new LocalDate(2016, 12, 15), new LocalDate(2016, 12, 9))
		then: 'an HotelException is thrown'
		thrown(HotelException)
	}

	@Unroll('this.booking.conflict: #arrivalDate, #departureDate')
	def 'expected conflicts on booking'() {
		expect: 'no conflicts'
		this.booking.conflict(arrivalDate, departureDate)

		where:
		arrivalDate << [new LocalDate(2016, 12, 9), arrival.minusDays(4), arrival,
					arrival.minusDays(4), arrival.minusDays(4), arrival.plusDays(3)]
		departureDate << [new LocalDate(2016, 12, 9), departure.plusDays(4), departure.plusDays(4),
					departure, departure.minusDays(3), departure.plusDays(6)]
	}

}
