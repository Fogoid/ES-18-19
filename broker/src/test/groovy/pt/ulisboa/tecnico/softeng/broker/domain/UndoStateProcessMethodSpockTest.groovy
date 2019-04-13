package pt.ulisboa.tecnico.softeng.broker.domain


import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.*

class UndoStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def broker
    def client
    def adventure
    def hotelInterface
    def bankInterface
    def activityInterface
    def carInterface
    def taxInterface



    // JFF: could have used data tables
    def populate4Test() {
         hotelInterface = Mock(HotelInterface)
        bankInterface = Mock(BankInterface)
        activityInterface = Mock(ActivityInterface)
        carInterface = Mock(CarInterface)
        taxInterface = Mock(TaxInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface, taxInterface);
        client = new Client(broker, CLIENT_IBAN as String, CLIENT_NIF as String, DRIVING_LICENSE as String, AGE as int);
        adventure = new Adventure(broker, BEGIN as LocalDate, END as LocalDate, client, MARGIN as double);

        adventure.setState(Adventure.State.UNDO);
    }

    def 'sucessRevertPayment'() {

        when:
        adventure.process()

        then:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION)  >> { PAYMENT_CANCELLATION}
        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()

    }

    def 'failRevertPaymentBankException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION)  >> { throw new BankException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    def 'failRevertPaymentRemoteAccessException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        bankInterface.cancelPayment(PAYMENT_CONFIRMATION)  >> { throw new RemoteAccessException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }


    def 'successRevertActivity'(){

        when:
        adventure.process()

        then:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> {ACTIVITY_CANCELLATION}

        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()

    }
    def 'failRevertActivityException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> { throw new ActivityException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }
    def 'failRevertActivityRemoteException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        activityInterface.cancelReservation(ACTIVITY_CONFIRMATION) >> {throw new RemoteAccessException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }
    def 'sucessRevertRoomBooking'(){

        when:
        adventure.process()

        then:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> {ROOM_CANCELLATION}

        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()


    }

    def 'sucessRevertRoomBookingHotelException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> {throw new HotelException()}

        when:
        adventure.process()

        then:

        Adventure.State.UNDO == adventure.getState().getValue()

    }

    def 'sucessRevertRoomBookingRemoteException'(){
    given:
    adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
    adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
    adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
    adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
    adventure.setRoomConfirmation(ROOM_CONFIRMATION);
    hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> {throw new RemoteAccessException()}

    when:
    adventure.process()

    then:
    Adventure.State.UNDO == adventure.getState().getValue()

}

    def 'sucessRevertRentCar'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> {RENTING_CANCELLATION}

        when:
        adventure.process()

        then:
        Adventure.State.CANCELLED == adventure.getState().getValue()

    }

    def 'failRevertRentCarCarException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> {throw new CarException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()

    }

    def 'failRevertRentCarRemoteException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        carInterface.cancelRenting(RENTING_CONFIRMATION) >> {throw new RemoteAccessException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()

    }

    def 'sucessCancelInvoice'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CONFIRMATION)
        adventure.setInvoiceReference(INVOICE_REFERENCE)
        taxInterface.cancelInvoice(INVOICE_REFERENCE)


        when:
        adventure.process()

        then:
        Adventure.State.CANCELLED == adventure.getState().getValue()


    }

    def 'failCancelInvoiceTaxException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION);
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION);
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION);
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION);
        adventure.setRoomConfirmation(ROOM_CONFIRMATION);
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        adventure.setInvoiceReference(INVOICE_REFERENCE)
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> {throw new TaxException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()

    }

    def 'failCancelInvoiceRemoteException'(){
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)
        adventure.setInvoiceReference(INVOICE_REFERENCE)
        taxInterface.cancelInvoice(INVOICE_REFERENCE) >> {throw new RemoteAccessException()}

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()

    }


}