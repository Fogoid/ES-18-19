package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

def class SpockHotelSetPriceMethodTest extends SpockRollbackTestAbstractClass{

    private Hotel hotel
    private final double price = 25.0

    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", price + 5.0, price + 10.0)
    }

    def 'successSingle'() {

        when:
        hotel.setPrice(Room.Type.SINGLE, price)

        then:
        price == hotel.getPrice(Room.Type.SINGLE)
    }

    def 'successDouble'() {

        when:
        hotel.setPrice(Room.Type.DOUBLE, price)

        then:
        price == hotel.getPrice(Room.Type.DOUBLE)
    }

    def 'negativePriceSingle'() {

        when:
        hotel.setPrice(Room.Type.SINGLE, -1.0)

        then:
        thrown(HotelException)
    }

    def 'negativePriceDouble'() {

        when:
        hotel.setPrice(Room.Type.DOUBLE, -1.0)

        then:
        thrown(HotelException)
    }
}
