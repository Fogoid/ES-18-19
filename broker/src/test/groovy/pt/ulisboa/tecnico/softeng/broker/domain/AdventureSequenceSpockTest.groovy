package pt.ulisboa.tecnico.softeng.broker.domain;

import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.TaxException

class AdventureSequenceSpockTest extends SpockRollbackTestAbstractClass {
    def bookingActivityData
    def bookingRoomData
    def rentingData
    def activityInterface
    def bankInterface
    def carInterface
    def roomInterface
    def taxInterface

    private Broker broker
    private Client client

    def populate4Test() {
        activityInterface = Mock(ActivityInterface)
        bankInterface = Mock(BankInterface)
        carInterface = Mock(CarInterface)
        roomInterface = Mock(HotelInterface)
        taxInterface = Mock(TaxInterface)

        this.broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, BROKER_NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, roomInterface, taxInterface)
        this.client = new Client(this.broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    
        this.bookingActivityData = new RestActivityBookingData()
		this.bookingActivityData.setReference(ACTIVITY_CONFIRMATION)
		this.bookingActivityData.setPrice(70.0)
		this.bookingActivityData.setPaymentReference(PAYMENT_CONFIRMATION)
		this.bookingActivityData.setInvoiceReference(INVOICE_REFERENCE)

		this.bookingRoomData = new RestRoomBookingData()
		this.bookingRoomData.setReference(ROOM_CONFIRMATION)
		this.bookingRoomData.setPrice(80.0)
		this.bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
		this.bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

		this.rentingData = new RestRentingData()
		this.rentingData.setReference(RENTING_CONFIRMATION)
		this.rentingData.setPrice(60.0)
		this.rentingData.setPaymentReference(PAYMENT_CONFIRMATION)
		this.rentingData.setInvoiceReference(INVOICE_REFERENCE)
    }

    def "success sequence"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        roomInterface.reserveRoom(_) >> this.bookingRoomData
        carInterface.rentCar(*_) >> this.rentingData
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> this.bookingActivityData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> this.rentingData
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> this.bookingRoomData

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN, true)

        and:
        adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def "successful sequence without car"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        roomInterface.reserveRoom(_) >> this.bookingRoomData
        bankInterface.processPayment(_) >>  PAYMENT_CONFIRMATION
        taxInterface.submitInvoice() >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> this.bookingActivityData
        roomInterface.getRoomBookingData(ROOM_CONFIRMATION) >> this.bookingRoomData

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN)

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def "successful sequence without hotel"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        carInterface.rentCar(*_) >> this.rentingData
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> this.bookingActivityData
        carInterface.getRentingData(RENTING_CONFIRMATION) >> this.rentingData

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, ARRIVAL, this.client, MARGIN, true);

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def "successful sequence without car and hotel"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        bankInterface.getOperationData(PAYMENT_CONFIRMATION)
        activityInterface.getActivityReservationData(ACTIVITY_CONFIRMATION) >> this.bookingActivityData;

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, ARRIVAL, this.client, MARGIN);

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CONFIRMED == adventure.getState().getValue()
    }

    def "canceled sequence fail activity"() {
        given:
        activityInterface.reserveActivity(_) >> { throw new ActivityException() }

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN)

        and:
        adventure.process()
        adventure.process()

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def "canceled sequence fail hotel"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        roomInterface.reserveRoom(_) >> { throw new HotelException() }
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN)

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def "canceled sequence fail car"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        carInterface.rentCar(*_) >> { throw new CarException() }
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, ARRIVAL, this.client, MARGIN, true)

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def "canceled sequence fail payment"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        roomInterface.reserveRoom(_) >> this.bookingRoomData
        carInterface.rentCar(*_) >> this.rentingData
        bankInterface.processPayment(_) >> { throw new BankException() }
        taxInterface.submitInvoice(_) >> INVOICE_DATA
        activityInterface.cancelReservation(_) >> ACTIVITY_CANCELLATION
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN, true)

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CANCELLED == adventure.getState().getValue()
    }

    def "canceled sequence fail tax"() {
        given:
        activityInterface.reserveActivity(_) >> this.bookingActivityData
        roomInterface.reserveRoom(_) >> this.bookingRoomData
        carInterface.rentCar(*_) >> this.rentingData
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> { throw new TaxException() }
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> ACTIVITY_CANCELLATION
        roomInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> RENTING_CANCELLATION
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION) >> PAYMENT_CANCELLATION

        when:
        def adventure = new Adventure(this.broker, ARRIVAL, DEPARTURE, this.client, MARGIN, true)

        and:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        State.CANCELLED == adventure.getState().getValue()
    }



}