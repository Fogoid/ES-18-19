package pt.ulisboa.tecnico.softeng.broker.domain

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
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException

def class ConfirmedStateProcessMethodSpockTest extends SpockRollbackTestAbstractClass {

	ActivityInterface activityInterface
	BankInterface bankInterface
	CarInterface carInterface
	HotelInterface hotelInterface
	TaxInterface taxInterface

	private Broker broker
	private Client client
	private Adventure adventure


	def roomActivityData
	def bookingActivityData
	RestRoomBookingData roomBookingData
	RestRentingData rentingData
	RestActivityBookingData activityReservationData




	@Override
	def 'populate4Test'() {


		activityInterface= Mock(ActivityInterface)
		bankInterface= Mock(BankInterface)
		carInterface= Mock(CarInterface)
		hotelInterface = Mock(HotelInterface)
		taxInterface= Mock(TaxInterface)
		activityReservationData = Mock(RestActivityBookingData)
		roomBookingData = Mock(RestRoomBookingData)
		rentingData = Mock(RestRentingData)

		bookingActivityData = new RestActivityBookingData()
		bookingActivityData.setReference(ACTIVITY_CONFIRMATION)


		broker=new Broker("BR01","eXtremeADVENTURE",BROKER_NIF_AS_SELLER,NIF_AS_BUYER,BROKER_IBAN, activityInterface, bankInterface, carInterface, hotelInterface ,taxInterface)
		client=new Client(broker,CLIENT_IBAN,CLIENT_NIF,DRIVING_LICENSE,AGE)
		adventure=new Adventure(broker, BEGIN, END,client,MARGIN)
		adventure.setState(State.CONFIRMED)
	}

	// JFF: results of interactions not being used
	def 'successAll'() {

		given:
		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getHotelInterface().getRoomBookingData(_)>> roomBookingData
		roomBookingData.getPaymentReference() >> REFERENCE
		roomBookingData.getInvoiceReference() >> REFERENCE

		broker.getCarInterface().getRentingData() >> rentingData
		rentingData.getPaymentReference() >> REFERENCE
		rentingData.getInvoiceReference() >> REFERENCE

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED

	}


	def 'success activity and hotel'() {

		given:
		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getHotelInterface().getRoomBookingData(_)>> roomBookingData
		roomBookingData.getPaymentReference() >> REFERENCE
		roomBookingData.getInvoiceReference() >> REFERENCE

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'success activity and car'() {
		given:
		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getCarInterface().getRentingData() >> rentingData
		rentingData.getPaymentReference() >> REFERENCE
		rentingData.getInvoiceReference() >> REFERENCE

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'success activity'() {
		given:
		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'one bank exception'() {
		given:
		broker.getBankInterface().getOperationData(_) >> {throw new BankException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'max bank exception'() {

		given:
		broker.getBankInterface().getOperationData(_) >> {throw new BankException()}

		when:
		adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'max minus one bank exception'() {
		given:
		broker.getBankInterface().getOperationData(_) >> {throw new BankException()}

		when:
		adventure.process()
		adventure.process()
		adventure.process()
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'one remote access exception in payment'() {
		given:
		broker.getBankInterface().getOperationData(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'activity exception'() {
		given:
		broker.getActivityInterface().getActivityReservationData(_) >> {throw new ActivityException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'one remote access exception in activity'() {
		given:
		broker.getActivityInterface().getActivityReservationData(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()
		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'activity no payment confirmation'() {
		given:
		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> null

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'activity no invoice reference'() {
		given:
		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> null

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	// JFF: why are the three sets outside the given block?
	def 'car exception'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getCarInterface().getRentingData(_) >> {throw new CarException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'one remote exception in car'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getCarInterface().getRentingData(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'car no payment confirmation'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getCarInterface().getRentingData(RENTING_CONFIRMATION) >> rentingData
		rentingData.getPaymentReference() >> null

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'car no invoice reference'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRentingConfirmation(RENTING_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getCarInterface().getRentingData(RENTING_CONFIRMATION) >> rentingData
		rentingData.getPaymentReference() >> REFERENCE
		rentingData.getInvoiceReference() >> null

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'hotel exception'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getHotelInterface().getRoomBookingData(_) >> {throw new HotelException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
		adventure.getRoomConfirmation()
	}

	def 'one remote access exception in hotel'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getHotelInterface().getRoomBookingData(_) >> {throw new RemoteAccessException()}

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.CONFIRMED
	}

	def 'hotel no payment confirmation'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)
		broker.getActivityInterface().getActivityReservationData(ACTIVITY_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getHotelInterface().getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData
		roomBookingData.getPaymentReference() >> null

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

	def 'hotel no invoice reference'() {
		adventure.setPaymentConfirmation(PAYMENT_CONFIRMATION)
		adventure.setActivityConfirmation(ACTIVITY_CONFIRMATION)
		adventure.setRoomConfirmation(ROOM_CONFIRMATION)

		given:
		broker.getBankInterface().getOperationData(PAYMENT_CONFIRMATION)
		broker.getActivityInterface().getActivityReservationData(ACTIVITY_CONFIRMATION)

		broker.getActivityInterface().getActivityReservationData(_) >> activityReservationData
		activityReservationData.getPaymentReference() >> REFERENCE
		activityReservationData.getInvoiceReference() >> REFERENCE

		broker.getHotelInterface().getRoomBookingData(ROOM_CONFIRMATION) >> roomBookingData
		roomBookingData.getPaymentReference() >> REFERENCE
		roomBookingData.getInvoiceReference() >> null

		when:
		adventure.process()

		then:
		adventure.getState().getValue() == State.UNDO
	}

}
