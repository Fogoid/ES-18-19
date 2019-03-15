package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
//import org.junit.runner.RunWith
//import mockit.Mocked
//import mockit.integration.junit4.JMockit
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
//import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
//import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

//@RunWith(JMockit)
def class RoomGetBookingMethodTest extends SpockRollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016,12,19)
	private final LocalDate departure = new LocalDate(2016,12,24)
	private Hotel hotel
	private Room room
	private Booking booking
	//@Mocked private TaxInterface taxInterface
	//@Mocked private BankInterface bankInterface
	private final String NIF_BUYER = "123456789"
	private final String IBAN_BUYER = "IBAN_BUYER"

	@Override
	def "populate4Test"() {
		hotel = new Hotel("XPTO123","Lisboa","NIF","IBAN",20.0,30.0)
		room = new Room(hotel,"01",Type.SINGLE)
		booking = room.reserve(Type.SINGLE,arrival,departure,NIF_BUYER,IBAN_BUYER)
	}

	def "success"() {
		expect:
		room.getBooking(booking.getReference()) == booking
	}

	def "success cancelled"() {
		when:
		booking.cancel()

		then:
		room.getBooking(booking.getCancellation()) == booking
	}

	def "does not exist"() {
		expect:
		room.getBooking("XPTO") == null
	}

}
