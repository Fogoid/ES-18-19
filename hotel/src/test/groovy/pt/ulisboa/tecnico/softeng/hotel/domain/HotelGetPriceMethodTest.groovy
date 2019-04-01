package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelGetPriceMethodTest extends SpockRollbackTestAbstractClass {
    Hotel hotel
    def priceSingle = 20.0d
    def priceDOuble = 30.0d

    def populate4Test(){
        hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", priceSingle, priceDOuble)
    }

    def "sucessSingle"(){
        when:
       true    // JFF: in this case, it would be better to use a single expect

        then:
        priceSingle == hotel.getPrice(Room.Type.SINGLE)

    }

    def "sucessDouble"(){
        when:
        true // JFF: in this case, it would be better to use a single expect

        then:
        priceDOuble == hotel.getPrice(Room.Type.DOUBLE)
    }

    def "nullType"(){
        when:
        hotel.getPrice(null)

        then:
        thrown(HotelException)
    }
}