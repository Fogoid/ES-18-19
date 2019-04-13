package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

import static org.junit.Assert.*;

class BulkRoomBookingGetRoomBookingData4TypeMethodSpockTest extends SpockRollbackTestAbstractClass {
    BulkRoomBooking bulk
    def broker
    def result
    def hotelInterface
    def carInterface


    @Override
    def "populate4Test"() {

        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, null, null, carInterface, hotelInterface, null)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)

        new Reference(bulk, REF_ONE)
        new Reference(bulk, REF_TWO)

        //assert 1 == bulk.getReferences().size()
    }

    @Test
    def "successSINGLE"() {
        given:
        //def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        RestRoomBookingData roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(SINGLE)
        hotelInterface.getRoomBookingData(_) >> { roomBookingData} // JFF: why is this returned as a closure?

                //assert result != null
        when:
        bulk.getRoomBookingData4Type(SINGLE)

        then:
        1 == bulk.getReferences().size()
    }

    @Test
    def "successDOUBLE"() {
        given:
        //def bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, CLIENT_IBAN)
        RestRoomBookingData roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)
        hotelInterface.getRoomBookingData(_) >> { roomBookingData}

        //assert result != null
        when:
        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        1 == bulk.getReferences().size()
    }

    @Test
    def "hotelException"() {


        when:
        hotelInterface.getRoomBookingData(_) >> { throw new HotelException()}
        hotelInterface.getRoomBookingData(_) >> { throw new HotelException()}

        then:
        bulk.getRoomBookingData4Type(DOUBLE) == null
        2 == bulk.getReferences().size()
    }

    @Test
    def "remoteException"() {

        when:
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException()}
        hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException()}

        then:
        bulk.getRoomBookingData4Type(DOUBLE) == null
        2 == bulk.getReferences().size()

    }

    @Test
    def "maxRemoteException"() {

        when:
        for(int i=0; i<BulkRoomBooking.MAX_REMOTE_ERRORS; i++){
            hotelInterface.getRoomBookingData(_) >> { throw new RemoteAccessException()}
        }


        then:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2; i++) {
            bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        2 == this.bulk.getReferences().size()
        bulk.getCancelled()
    }

    @Test
    def "maxMinusOneRemoteException"() {

        given:
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)

        when:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assert bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        this.bulk.getRoomBookingData4Type(DOUBLE)

        then:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            2*hotelInterface.getRoomBookingData(_) >> {throw new RemoteAccessException()}
        }
        and:
        1*hotelInterface.getRoomBookingData(_) >> roomBookingData

        and:
        1 == bulk.getReferences().size()
    }

    // JFF: some tests are not as expected (see solutions)
    @Test
    def "remoteExceptionValueIsResetBySuccess"() {

        given:
        def roomBookingData = new RestRoomBookingData()
        roomBookingData.setRoomType(DOUBLE)

        when:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assertNull(this.bulk.getRoomBookingData4Type(DOUBLE))
        }

        bulk.getRoomBookingData4Type(DOUBLE)

        then:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            2*hotelInterface.getRoomBookingData(_) >> {throw new RemoteAccessException()}
        }

        and:
        1*hotelInterface.getRoomBookingData(_) >> roomBookingData

        and:
        1 == bulk.getReferences().size()

        when:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assert this.bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        then:
        !bulk.getCancelled()

    }

    @Test
    def "remoteExceptionValueIsResetByHotelException"() {

        given:
        hotelInterface.getRoomBookingData(_) >> {throw new HotelException()}

        when:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assert this.bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        then:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            2*hotelInterface.getRoomBookingData(_) >> {throw new RemoteAccessException()}
        }

        and:
        2 == bulk.getReferences().size()

        when:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS / 2 - 1; i++) {
            assert this.bulk.getRoomBookingData4Type(DOUBLE) == null
        }

        then:
        !bulk.getCancelled()


    }

}
