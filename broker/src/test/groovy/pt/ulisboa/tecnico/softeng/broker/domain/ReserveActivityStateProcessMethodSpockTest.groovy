package pt.ulisboa.tecnico.softeng.broker.domain
import mockit.Delegate
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestActivityBookingData
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure.State
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.ActivityException
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;

class ReserveActivityStateMethodSpockTest extends SpockRollbackTestAbstractClass {
    private Broker broker
    private Client client
    private Adventure adventure
    def bookingData

    def taxInterface
    def activityInterface

    @Override
    def populate4Test(){
        taxInterface=Mock(TaxInterface)
        activityInterface=Mock(ActivityInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN,activityInterface,null,null,null,taxInterface)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
        adventure = new Adventure(broker, BEGIN, END, client, MARGIN)
        this.bookingData = new RestActivityBookingData()
        this.bookingData.setReference(ACTIVITY_CONFIRMATION)
        this.bookingData.setPrice(76.78)

        adventure.setState(State.RESERVE_ACTIVITY)

    }

    def 'successNoBookRoom'(){
        given:
        Adventure sameDayAdventure = new Adventure(broker,BEGIN, BEGIN, client, MARGIN)
        sameDayAdventure.setState(State.RESERVE_ACTIVITY)
        broker.getActivityInterface().reserveActivity(_) >> this.bookingData

        when:
        sameDayAdventure.process()

        then:
        assert sameDayAdventure.getState().getValue()==State.PROCESS_PAYMENT
    }

    def 'successToRentVehicle'(){
        given:
        Adventure adv = new Adventure(broker, BEGIN, BEGIN, client, MARGIN, true)
        adv.setState(State.RESERVE_ACTIVITY)
        broker.getActivityInterface().reserveActivity(_) >> this.bookingData

        when:
        adv.process()

        then:
        adv.getState().getValue()==State.RENT_VEHICLE
    }

    def 'successBookRoom' (){
        given:
        broker.getActivityInterface().reserveActivity(_) >> this.bookingData

        when:
        adventure.process()

        then:
        assert State.BOOK_ROOM==adventure.getState().getValue()
    }
    def 'activityException' (){
        given:
        broker.getActivityInterface().reserveActivity(_) >> { throw new ActivityException()}

        when:
        adventure.process()

        then:
        assert State.UNDO == adventure.getState().getValue()

    }

    def 'singleRemoteAccessException' () {
        given:
        broker.getActivityInterface().reserveActivity(_) >> { throw new RemoteAccessException()}

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        assert State.UNDO == adventure.getState().getValue()
    }

    def 'maxRemoteAccessException' () {
        given:
        broker.getActivityInterface().reserveActivity(_) >> {throw new RemoteAccessException()}

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        assert State.UNDO == adventure.getState().getValue()
    }

    def 'maxMinusOneRemoteAccessException' () {
        given:
        broker.getActivityInterface().reserveActivity(_) >> {throw new RemoteAccessException()}

        when:
        adventure.process()
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        assert State.RESERVE_ACTIVITY == adventure.getState().getValue()
    }

    def 'twoRemoteAccessExceptionOneSuccess' (){

        when:
        adventure.process()
        adventure.process()
        adventure.process()

        then:
        2*broker.getActivityInterface().reserveActivity(_) >> { throw new RemoteAccessException() }
        and:
        1*broker.getActivityInterface().reserveActivity(_) >> bookingData
        and:
        State.BOOK_ROOM == adventure.getState().getValue()

    }

    def 'oneRemoteAccessExceptionOneActivityException' (){

        when:
        adventure.process()
        adventure.process()

        then:
        1*broker.getActivityInterface().reserveActivity(_) >> { throw new RemoteAccessException() }
        and:
        1*broker.getActivityInterface().reserveActivity(_) >> { throw new ActivityException() }
        and:
        State.UNDO == adventure.getState().getValue()

    }

}
