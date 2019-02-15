package pt.ulisboa.tecnico.softeng.hotel.domain

import static org.junit.Assert.*

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelGetPriceMethodSpockTest extends SpockRollbackTestAbstractClass {
	def PRICE_SINGLE = 20.0
	def PRICE_DOUBLE = 30.0
	def hotel

	@Override
	def populate4Test() {
	}

	def 'price single'() {
		given: 'a hotel'
		hotel = new Hotel('XPTO123', 'Lisboa', 'NIF', 'IBAN', PRICE_SINGLE, PRICE_DOUBLE)

		expect:
		hotel.getPrice(Room.Type.SINGLE) == PRICE_SINGLE
	}

	def 'price double'() {
		given: 'a hotel'
		hotel = new Hotel('XPTO123', 'Lisboa', 'NIF', 'IBAN', PRICE_SINGLE, PRICE_DOUBLE)

		expect:
		hotel.getPrice(Room.Type.DOUBLE) == PRICE_DOUBLE
	}

	def 'incorrect input'() {
		given: 'a hotel'
		hotel = new Hotel('XPTO123', 'Lisboa', 'NIF', 'IBAN', PRICE_SINGLE, PRICE_DOUBLE)

		when:
		hotel.getPrice(null)

		then: 'a HotelException'
		def error = thrown(HotelException)
	}
}
