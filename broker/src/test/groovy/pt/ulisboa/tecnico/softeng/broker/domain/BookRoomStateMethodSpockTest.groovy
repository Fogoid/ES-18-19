package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class BookRoomStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    def bookingData
    def hotelInterface

    private Broker broker
    private Client client
    private Adventure adventure

    def populate4Test() {
        this.hotelInterface = Mock(HotelInterface)
        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, null, null, null, hotelInterface, null)
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        this.adventure = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN)

        this.bookingData = new RestRoomBookingData()
        this.bookingData.setReference(ROOM_CONFIRMATION)
        this.bookingData.setPrice(80.0)

        this.adventure.setState(State.BOOK_ROOM)
    }

    def "success book room"() {
        given:
        hotelInterface.reserveRoom(_) >> this.bookingData

        when:
        this.adventure.process()

        then:
        State.PROCESS_PAYMENT == this.adventure.getState().getValue()
    }

    def "success book room to rent"() {
        given:
        hotelInterface.reserveRoom(_) >> this.bookingData

        when:
        def adv = new Adventure(this.broker, this.BEGIN, this.END, this.client, MARGIN, true);
        adv.setState(State.BOOK_ROOM)

        and:
        adv.process()

        then:
        State.RENT_VEHICLE == adv.getState().getValue()
    }

    def "returned hotel exception"() {
        given:
        hotelInterface.reserveRoom(_) >> { throw new HotelException() }

        when:
        this.adventure.process()

        then:
        State.UNDO == this.adventure.getState().getValue()
    }

    def "have a single remote access exception"() {
        given:
        hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }

        when:
        this.adventure.process()

        then:
        State.BOOK_ROOM == this.adventure.getState().getValue()
    }

    def "make the max remote access exception"() {
        when:
        for(def i = 0; i < BookRoomState.MAX_REMOTE_ERRORS; i++)
            this.adventure.process()
        then:
        10*hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
        and:
        State.UNDO == this.adventure.getState().getValue()
    }

    def "make max-1 remote access exception"() {
        when:
        for (def i = 0; i < BookRoomState.MAX_REMOTE_ERRORS - 1; i++)
            this.adventure.process()
        then:
        9*hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
        and:
        State.BOOK_ROOM == this.adventure.getState().getValue()
    }

    def "five remote access exceptions and one success"() {
        when:
        this.adventure.process()
        this.adventure.process()
        this.adventure.process()
        this.adventure.process()
        this.adventure.process()
        this.adventure.process()

        then:
        5*hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
        and:
        1*hotelInterface.reserveRoom(_) >> this.bookingData
        and:
        State.PROCESS_PAYMENT == this.adventure.getState().getValue()
    }

    def "one remove access exception and one hotel exception"() {
        when:
        this.adventure.process()
        this.adventure.process()

        then:
        1*hotelInterface.reserveRoom(_) >> { throw new RemoteAccessException() }
        and:
        1*hotelInterface.reserveRoom(_) >> { throw new HotelException() }
        and:
        State.UNDO == this.adventure.getState().getValue()
    }

}
