package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure
import pt.ulisboa.tecnico.softeng.broker.domain.Broker
import pt.ulisboa.tecnico.softeng.broker.domain.Client
import pt.ulisboa.tecnico.softeng.broker.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.broker.services.remote.*
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestBankOperationData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.BankException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

class CancelledStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

    def broker
    def client
    def adventure
    def hotelInterface
    def bankInterface
    def activityInterface
    def carInterface
    def taxInterface
    def restBankOperationData
    def restACtivityOperationData
    def restRoomOperationData
    def restCarOperationData


    def populate4Test() {
        hotelInterface = Mock(HotelInterface)
        bankInterface = Mock(BankInterface)
        activityInterface = Mock(ActivityInterface)
        carInterface = Mock(CarInterface)
        taxInterface = Mock(TaxInterface)
        restBankOperationData = new RestBankOperationData()
        restACtivityOperationData = new RestActivityBookingData()
        restRoomOperationData = new RestRoomBookingData()
        restCarOperationData = new RestRentingData()

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface, taxInterface);
        client = new Client(broker, CLIENT_IBAN as String, CLIENT_NIF as String, DRIVING_LICENSE as String, AGE as int);
        adventure = new Adventure(broker, BEGIN as LocalDate, END as LocalDate, client, MARGIN as double);

        adventure.setState(Adventure.State.CANCELLED);
    }

   def 'didNotPayed'(){
       when:
       adventure.process()

       then:
       Adventure.State.CANCELLED == adventure.getState().getValue()

       and:
       0*bankInterface.getOperationData(_)
       0*activityInterface.getActivityReservationData(_)
       0*hotelInterface.getRoomBookingData(_);

   }

    // JFF: could have used data tables
    def 'cancelledPaymentFirstBankException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        bankInterface.getOperationData(*_) >> { throw new BankException()}

        when:
        adventure.process()

        then:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledPaymentFirstRemoteAccessException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        bankInterface.getOperationData(*_) >> { throw new RemoteAccessException()}

        when:
        adventure.process()

        then:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledPaymentSecondBankException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        bankInterface.getOperationData(*_) >> { RestBankOperationData;  throw new BankException()}

        when:
        adventure.process()

        then:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }
    def 'cancelledPaymentSecondRemoteAcessException'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        bankInterface.getOperationData(*_) >> { RestBankOperationData;  throw new RemoteAccessException()}

        when:
        adventure.process()

        then:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledPayment'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> restBankOperationData
        bankInterface.getOperationData(PAYMENT_CANCELLATION) >> restBankOperationData
        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledActivity'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)


        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> restBankOperationData
        bankInterface.getOperationData(PAYMENT_CANCELLATION) >> restBankOperationData
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> restACtivityOperationData
        and:

        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledRoom'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)



        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> restBankOperationData
        bankInterface.getOperationData(PAYMENT_CANCELLATION) >> restBankOperationData
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> restACtivityOperationData
        hotelInterface.getRoomBookingData(ROOM_CANCELLATION) >> restRoomOperationData

        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledRenting'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)


        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> restBankOperationData
        bankInterface.getOperationData(PAYMENT_CANCELLATION) >> restBankOperationData
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> restACtivityOperationData
        carInterface.getRentingData(RENTING_CANCELLATION) >>restCarOperationData
        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }

    def 'cancelledBookAndRenting'() {
        given:
        adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
        adventure.setPaymentCancellation(PAYMENT_CANCELLATION)
        adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
        adventure.setActivityCancellation(ACTIVITY_CANCELLATION)
        adventure.setRoomConfirmation(ROOM_CONFIRMATION)
        adventure.setRoomCancellation(ROOM_CANCELLATION)
        adventure.setRentingConfirmation(RENTING_CONFIRMATION)
        adventure.setRentingCancellation(RENTING_CANCELLATION)

        when:
        adventure.process()

        then:
        bankInterface.getOperationData(PAYMENT_CONFIRMATION) >> restBankOperationData
        bankInterface.getOperationData(PAYMENT_CANCELLATION) >> restBankOperationData
        activityInterface.getActivityReservationData(ACTIVITY_CANCELLATION) >> restACtivityOperationData
        hotelInterface.getRoomBookingData(ROOM_CANCELLATION) >> restRoomOperationData
        carInterface.getRentingData(RENTING_CANCELLATION) >>restCarOperationData
        and:
        Adventure.State.CANCELLED == adventure.getState().getValue()
    }


}