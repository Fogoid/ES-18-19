package pt.ulisboa.tecnico.softeng.hotel.domain


import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

import spock.lang.Shared
import spock.lang.Unroll


class GBookingConstructorTest extends SpockRollbackTestAbstractClass {
    @Shared def ARRIVAL =new LocalDate(2016, 12, 19)
    @Shared def DEPARTURE =new LocalDate(2016, 12, 21)
    @Shared def ROOM_PRICE=20.0
    @Shared def NIF_BUYER="123456789"
    @Shared def IBAN_BUYER="IBAN_BUYER"
    @Shared Room room

    //VER MOCKS E SE E PRECISO FAZER

    @Override
    def populate4Test(){
        def hotel =new Hotel("XPTO123", "Londres", "NIF", "IBAN", 20.0, 30.0)
        room =new Room(hotel,"01",Room.Type.SINGLE)
    }

    def 'success'(){
        when:
        def booking =new Booking(room,ARRIVAL,DEPARTURE,NIF_BUYER,IBAN_BUYER)
        then:
        booking.getReference().startsWith(room.getHotel().getCode())
        booking.getReference().length() >Hotel.CODE_SIZE
        booking.getArrival()==ARRIVAL
        booking.getDeparture()==DEPARTURE
        booking.getPrice()==ROOM_PRICE * 2

    }

    @Unroll("Booking: #_room,#arrival,#departure,#nif_buyer,#iban_buyer")
    def 'expections_test'(){
        when:
        new Booking(_room,arrival,departure,nif_buyer,iban_buyer)

        then:
        thrown(HotelException)

        where:
        _room  | arrival  | departure  | nif_buyer  | iban_buyer
        null   | ARRIVAL  |  DEPARTURE | NIF_BUYER  | IBAN_BUYER
        room   | null     |  DEPARTURE | NIF_BUYER  | IBAN_BUYER
        room   | ARRIVAL  |  null      | NIF_BUYER  | IBAN_BUYER
    }

    def ' departureBeforeArrival'() {
        when:
        new Booking(room,ARRIVAL,ARRIVAL.minusDays(1),NIF_BUYER,IBAN_BUYER)
        then:
        thrown(HotelException)
    }
    def 'arrivalEqualDeparture '(){
        given:
        new Booking (room,ARRIVAL,ARRIVAL,NIF_BUYER,IBAN_BUYER)

    }
}