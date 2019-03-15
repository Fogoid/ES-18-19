package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Booking
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.services.remote.dataobjects.RestRoomBookingData

import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

def class HotelInterfaceGetRoomBookingDataMethodTest extends SpockRollbackTestAbstractClass{

    def arrival = new LocalDate(2016, 12, 19)
    def departure = new LocalDate(2016, 12, 24)
    private Hotel hotel
    private Room room
    private Booking booking
    def NIF_HOTEL = "123456700"
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"



    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", NIF_HOTEL, "IBAN", 20.0, 30.0)
        room = new Room(hotel, "01", Type.SINGLE)
        booking = room.reserve(Type.SINGLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
    }

    def 'success'() {

        when:
        RestRoomBookingData data = HotelInterface.getRoomBookingData(booking.getReference())

        then:
        booking.getReference() == data.getReference()
        null == (data.getCancellation())
        null == (data.getCancellationDate())
        hotel.getName() == data.getHotelName()
        hotel.getCode() == data.getHotelCode()
        room.getNumber() == data.getRoomNumber()
        room.getType().name() == data.getRoomType()
        booking.getArrival() == data.getArrival()
        booking.getDeparture() == data.getDeparture()
        booking.getPrice() == data.getPrice()

    }

    def 'successCancellation'() {

        when:
        booking.cancel()
        RestRoomBookingData data = HotelInterface.getRoomBookingData(booking.getCancellation())

        then:
        booking.getReference() == data.getReference()
        booking.getCancellation() == data.getCancellation()
        booking.getCancellationDate() == data.getCancellationDate()
        hotel.getName() == data.getHotelName()
        hotel.getCode() == data.getHotelCode()
        room.getNumber() == data.getRoomNumber()
        room.getType().name() == data.getRoomType()
        booking.getArrival() == data.getArrival()
        booking.getDeparture() == data.getDeparture()
        booking.getPrice() == data.getPrice()
    }

    def 'nullReference'() {

        when:
        HotelInterface.getRoomBookingData(null)

        then:
        thrown(HotelException)
    }

    def 'emptyReference'() {

        when:
        HotelInterface.getRoomBookingData("")

        then:
        thrown(HotelException)
    }

    def 'referenceDoesNotExist'() {

        when:
        HotelInterface.getRoomBookingData("XPTO")

        then:
        thrown(HotelException)
    }



}
