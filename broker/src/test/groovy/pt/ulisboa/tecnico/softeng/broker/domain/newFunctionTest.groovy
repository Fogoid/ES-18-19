package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Expectations;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface.Type

import static org.junit.Assert.*;

class newFunctionTest extends SpockRollbackTestAbstractClass {
    BulkRoomBooking bulk
    def broker
    def client
    def adventure
    def hotelInterface
    def arrival = new LocalDate("2018-03-20")
    def departure= new LocalDate("2018-03-27")
    def nifASBuyer ="nif-buyer"
    def IBAN="iban"
    def bookingRoomData
    def newBookingRoomData

    @Override
    def populate4Test() {

        hotelInterface = new HotelInterface()

        this.newBookingRoomData = new RestRoomBookingData()
        this.newBookingRoomData.setReference(ROOM_CONFIRMATION)
        this.newBookingRoomData.setPrice(80.0)
        this.newBookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
        this.newBookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, null, null, null, hotelInterface, null)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
        //assert 1 == bulk.getReferences().size()

        this.bookingRoomData = new RestRoomBookingData(Type.SINGLE, arrival, departure, nifASBuyer, IBAN, "adventureId")
        this.bookingRoomData.setReference(ROOM_CONFIRMATION)
        this.bookingRoomData.setPrice(80.0)
        this.bookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
        this.bookingRoomData.setInvoiceReference(INVOICE_REFERENCE)

        this.newBookingRoomData = new RestRoomBookingData(Type.SINGLE, arrival, departure, nifASBuyer, IBAN, "adventureId")
        this.newBookingRoomData.setReference(ROOM_CONFIRMATION)
        this.newBookingRoomData.setPrice(80.0)
        this.newBookingRoomData.setPaymentReference(PAYMENT_CONFIRMATION)
        this.newBookingRoomData.setInvoiceReference(INVOICE_REFERENCE)
    }
    /*
    def 'test' (){
        given:
        def result = hotelInterface.reserveRoom(this.bookingRoomData)
        //hotelInterface.getRoomBookingData(ROOM_CONFIRMATION) >> this.bookingRoomData

        hotelInterface.cancelBooking(ROOM_CONFIRMATION) >> ROOM_CANCELLATION
        when:
        def compare_result = hotelInterface.reserveRoom(this.bookingRoomData)

        then:
        result == compare_result

        //hotelInterface.reserveRoom(new RestRoomBookingData(Type.SINGLE, arrival, departure, nifASBuyer, IBAN, "1")

        //hotelInterface.reserveRoom(new RestRoomBookingData(Type.SINGLE, arrival, departure, nifASBuyer, IBAN, "1")

    }*/

}
