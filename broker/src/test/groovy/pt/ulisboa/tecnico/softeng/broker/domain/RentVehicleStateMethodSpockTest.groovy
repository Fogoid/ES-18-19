package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate;
import org.junit.Assert
import org.junit.Ignore;
import org.junit.Test;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRentingData;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.CarException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;


class RentVehicleStateMethodSpockTest extends SpockRollbackTestAbstractClass {

    RestRentingData rentingData
    def broker
    def client
    def adventure
    def carInterface
    def hotelInterface


    @Override
    def "populate4Test"() {

        carInterface = Mock(CarInterface)
        hotelInterface = Mock(HotelInterface)
        //adventure = Mock(Adventure)


        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, null, null, carInterface, hotelInterface, null)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)

        rentingData = new RestRentingData();
        rentingData.setReference(RENTING_CONFIRMATION);
        rentingData.setPrice(76.78);

        adventure.setState(State.RENT_VEHICLE)
    }

    @Test
    def "successRentVehicle"() {
        given:
        carInterface.rentCar(*_) >> { rentingData }

        when:
        adventure.process()

        then:
        Adventure.State.PROCESS_PAYMENT == adventure.getState().getValue()
    }

    // JFF: could have used data tables
    @Test
    def "carException"() {

        given:
        carInterface.rentCar(*_) >> { throw new CarException() }

        when:
        adventure.process()

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    @Test
    def "singleRemoteAccessException"() {

        given:
        carInterface.rentCar(*_) >> { throw new RemoteAccessException() }

        when:
        adventure.process()

        then:
        Adventure.State.RENT_VEHICLE == adventure.getState().getValue()
    }

    @Test
    def "maxRemoteAccessException"() {

        given:
        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS; i++) {
            carInterface.rentCar(*_) >> { throw new RemoteAccessException() }
        }

        when:
        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS; i++) {
            adventure.process()
        }

        then:
        Adventure.State.UNDO == adventure.getState().getValue()
    }

    @Test
    def "maxMinusOneRemoteAccessException"() {

        given:
        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS - 1; i++) {
            carInterface.rentCar(*_) >> { throw new RemoteAccessException() }
        }

        when:
        for (int i = 0; i < RentVehicleState.MAX_REMOTE_ERRORS - 1; i++) {
            adventure.process()
        }

        then:
        Adventure.State.RENT_VEHICLE == adventure.getState().getValue()
    }

    @Test
    def "twoRemoteAccessExceptionOneSuccess"() {

        when:
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        2 * carInterface.rentCar(*_) >> { throw new RemoteAccessException() }

        and:
        1 * carInterface.rentCar(*_) >> { rentingData }
        and:
        Adventure.State.PROCESS_PAYMENT == adventure.getState().getValue()

    }

    @Test
    def "oneRemoteAccessExceptionOneCarException"() {

        when:
        adventure.process()
        adventure.process()

        then:
        1 * carInterface.rentCar(*_) >> { throw new RemoteAccessException() }

        and:
        1 * carInterface.rentCar(*_) >> { throw new CarException() }
        and:
        Adventure.State.UNDO == adventure.getState().getValue()

    }
}