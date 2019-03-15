package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
//import org.junit.runner.RunWith
//import mockit.Mocked
//import mockit.integration.junit4.JMockit
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
//import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
//import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface

//@RunWith(JMockit)
def class RoomReserveMethodTest extends SpockRollbackTestAbstractClass {
	private final LocalDate arrival = new LocalDate(2016,12,19)
	private final LocalDate departure = new LocalDate(2016,12,24)
	private Room room
	private final String NIF_HOTEL = "123456700"
	private final String NIF_BUYER = "123456789"
	private final String IBAN_BUYER = "IBAN_BUYER"
	//@Mocked private TaxInterface taxInterface
	//@Mocked private BankInterface bankInterface

	@Override
	def "populate4Test"() {
		Hotel hotel = new Hotel("XPTO123","Lisboa",NIF_HOTEL,"IBAN",20.0,30.0)
		room = new Room(hotel,"01",Type.SINGLE)
	}

	def "success"() {
		when:
		Booking booking=room.reserve(Type.SINGLE,arrival,departure,NIF_BUYER,IBAN_BUYER)

		then:
		room.getBookingSet().size() == 1
		booking.getReference().length() > 0
		booking.getArrival() == arrival
		booking.getDeparture() == departure
	}

	def "no double"() {
		when:
		room.reserve(Type.DOUBLE,arrival,departure,NIF_BUYER,IBAN_BUYER)

		then:
		thrown(HotelException)
	}

	def "null type"() {
		when:
		room.reserve(null,arrival,departure,NIF_BUYER,IBAN_BUYER)

		then:
		thrown(HotelException)
	}

	def "null arrival"() {
		when:
		room.reserve(Type.SINGLE,null,departure,NIF_BUYER,IBAN_BUYER)

		then:
		thrown(HotelException)
	}

	def "null departure"() {
		when:
		room.reserve(Type.SINGLE,arrival,null,NIF_BUYER,IBAN_BUYER)

		then:
		thrown(HotelException)
	}

	def "all conflict"() {
		expect:
		room.reserve(Type.SINGLE,arrival,departure,NIF_BUYER,IBAN_BUYER)

		try {
			room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
		} catch(HotelException he) {
			room.getBookingSet().size() == 1
		}
	}

}
