package pt.ulisboa.tecnico.softeng.hotel.domain;


import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class RoomConstructorMethodTest extends SpockRollbackTestAbstractClass  {
    Hotel hotel

    def populate4Test() {
        hotel = new Hotel("XPTO123", "Lisboa", "NIF", "IBAN", 20.0, 30.0)
    }

    def "success"() {
        when:
        def room = new Room(hotel, "01", Room.Type.DOUBLE)

        then:
        room.getHotel() == hotel
        room.getNumber() == "01"
        room.getType() == Room.Type.DOUBLE
        hotel.getRoomSet().size() == 1
    }

    def "nullHotel"() {
        when:
        new Room(null, "01", Room.Type.DOUBLE)

        then:
        thrown(HotelException)
    }

    def "nullRoomNumber"() {
        when:
        new Room(hotel, null, Room.Type.DOUBLE)

        then:
        thrown(HotelException)
    }

    def "emptyRoomNumber"() {
        when:
        new Room(hotel, "", Room.Type.DOUBLE)

        then:
        thrown(HotelException)
    }

    def "blankRoomNumber"() {
        when:
        new Room(hotel, "     ", Room.Type.DOUBLE)

        then:
        thrown(HotelException)
    }

    def "nonAlphanumericRoomNumber"() {
        when:
        new Room(hotel, "JOSE", Room.Type.DOUBLE)

        then:
        thrown(HotelException)
    }

    def "nullType"() {
        when:
        new Room(hotel, "01", null)

        then:
        thrown(HotelException)
    }

    def "nonUniqueRoomNumber"() {

        new Room(hotel, "01", Room.Type.SINGLE);

        try {
            when:
            new Room(hotel, "01", Room.Type.DOUBLE)

            then:
            false
        }
        catch (HotelException he) {
            then:
            hotel.getRoomSet().size() == 1
        }
    }

}

