package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import org.slf4j.LoggerFactory
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type

class HotelPersistenceTest extends SpockPersistenceTestAbstractClass {
    def logger = LoggerFactory.getLogger(HotelPersistenceTest.class)

    def HOTEL_NIF = "123456789"
    def HOTEL_IBAN = "IBAN"
    def HOTEL_NAME = "Berlin Plaza"
    def HOTEL_CODE = "H123456"
    def ROOM_NUMBER = "01"
    def CLIENT_NIF = "123458789"
    def CLIENT_IBAN = "IBANC"

    private LocalDate arrival = LocalDate.parse("2017-12-15")
    private LocalDate departure = LocalDate.parse("2017-12-19")

    @Override
    def whenCreateInDatabase() {

        for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
            hotel.delete()
        }

        def hotel = new Hotel(HOTEL_CODE, HOTEL_NAME, HOTEL_NIF, HOTEL_IBAN, Double.parseDouble("10"), Double.parseDouble("20"))

        new Room(hotel, ROOM_NUMBER, Type.DOUBLE)

        hotel.reserveRoom(Type.DOUBLE, arrival, departure, CLIENT_NIF, CLIENT_IBAN, "adventureId")
    }

    @Override
    def thenAssert(){
        assert FenixFramework.getDomainRoot().getHotelSet().size() == 1

        def hotels = new ArrayList<>(FenixFramework.getDomainRoot().getHotelSet())
        def hotel = hotels.get(0)

        assert hotel.getName() == HOTEL_NAME
        assert hotel.getCode() == HOTEL_CODE
        assert hotel.getIban() == HOTEL_IBAN
        assert hotel.getNif() == HOTEL_NIF
        assert hotel.getPriceSingle() == Double.parseDouble("10.0")
        assert hotel.getPriceDouble() == Double.parseDouble("20.0")
        assert hotel.getRoomSet().size() == 1
        def processor = hotel.getProcessor()
        assert processor != null
        assert processor.getBookingSet().size() == 1

        def rooms = new ArrayList<>(hotel.getRoomSet())
        def room = rooms.get(0)

        assert room.getNumber() == ROOM_NUMBER
        assert room.getType() == Type.DOUBLE
        assert room.getBookingSet().size() == 1

        def bookings = new ArrayList<>(room.getBookingSet())
        def booking = bookings.get(0)

        assert booking.getReference() != null
        assert booking.getArrival() == arrival
        assert booking.getDeparture() == departure
        assert booking.getBuyerIban() == CLIENT_IBAN
        assert booking.getBuyerNif() == CLIENT_NIF
        assert booking.getProviderNif() == HOTEL_NIF
        assert booking.getPrice() == Double.parseDouble("80.0")
        assert booking.getRoom() == room
        assert booking.getTime() != null
        assert booking.getProcessor() != null
    }

    @Override
    def deleteFromDatabase(){
        for (def hotel : FenixFramework.getDomainRoot().getHotelSet()) {
            hotel.delete()
        }
    }
}
