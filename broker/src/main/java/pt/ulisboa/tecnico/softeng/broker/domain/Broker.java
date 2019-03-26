package pt.ulisboa.tecnico.softeng.broker.domain;


import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface;
import pt.ulisboa.tecnico.softeng.broker.services.remote.TaxInterface;


public class Broker extends Broker_Base {
	private static Logger logger = LoggerFactory.getLogger(Broker.class);
	private CarInterface carInterface;
	private HotelInterface hotelInterface;

	public Broker(String code, String name, String nifAsSeller, String nifAsBuyer, String iban) {
		checkArguments(code, name, nifAsSeller, nifAsBuyer, iban);

		setCode(code);
		setName(name);
		setNifAsSeller(nifAsSeller);
		setNifAsBuyer(nifAsBuyer);
		setIban(iban);

		FenixFramework.getDomainRoot().addBroker(this);
	}


	public Broker(String code, String name, String nifAsSeller, String nifAsBuyer, String iban, CarInterface carInterface, HotelInterface hotelInterface) {
		checkArguments(code, name, nifAsSeller, nifAsBuyer, iban);

		setCode(code);
		setName(name);
		setNifAsSeller(nifAsSeller);
		setNifAsBuyer(nifAsBuyer);
		setIban(iban);



		setCarInterface(carInterface);	
    setHotelInterface(hotelInterface);

		FenixFramework.getDomainRoot().addBroker(this);

	}


	public void delete() {
		setRoot(null);

		for (Adventure adventure : getAdventureSet()) {
			adventure.delete();
		}

		for (BulkRoomBooking bulkRoomBooking : getRoomBulkBookingSet()) {
			bulkRoomBooking.delete();
		}

		for (Client client : getClientSet()) {
			client.delete();
		}

		deleteDomainObject();
	}

	private void checkArguments(String code, String name, String nifAsSeller, String nifAsBuyer, String iban) {
		if (code == null || code.trim().length() == 0 || name == null || name.trim().length() == 0
				|| nifAsSeller == null || nifAsSeller.trim().length() == 0 || nifAsBuyer == null
				|| nifAsBuyer.trim().length() == 0 || iban == null || iban.trim().length() == 0) {
			throw new BrokerException();
		}

		if (nifAsSeller.equals(nifAsBuyer)) {
			throw new BrokerException();
		}

		for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
			if (broker.getCode().equals(code)) {
				throw new BrokerException();
			}
		}

		for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
			if (broker.getNifAsSeller().equals(nifAsSeller) || broker.getNifAsSeller().equals(nifAsBuyer)
					|| broker.getNifAsBuyer().equals(nifAsSeller) || broker.getNifAsBuyer().equals(nifAsBuyer)) {
				throw new BrokerException();
			}
		}

	}

	public Client getClientByNIF(String NIF) {
		for (Client client : getClientSet()) {
			if (client.getNif().equals(NIF)) {
				return client;
			}
		}
		return null;
	}

	public boolean drivingLicenseIsRegistered(String drivingLicense) {
		return getClientSet().stream().anyMatch(client -> client.getDrivingLicense().equals(drivingLicense));
	}

	public void bulkBooking(int number, LocalDate arrival, LocalDate departure) {
		BulkRoomBooking bulkBooking = new BulkRoomBooking(this, number, arrival, departure, getNifAsBuyer(), getIban());
		bulkBooking.processBooking();
	}

	@Override
	public int getCounter() {
		int counter = super.getCounter() + 1;
		setCounter(counter);
		return counter;
	}

	/**
	 * @return the carInterface
	 */
	public CarInterface getCarInterface() {
		return carInterface;
	}

	/**
	 * @param carInterface the carInterface to set
	 */
	public void setCarInterface(CarInterface carInterface) {
		this.carInterface = carInterface;
	}

	/**
	 * @return the hotelInterface
	 */
	public HotelInterface getHotelInterface() {
		return this.hotelInterface;
	}

	/**
	 * @param hotelInterface the hotelInterface to set
	 */
	public void setHotelInterface(HotelInterface hotelInterface) {
		this.hotelInterface = hotelInterface;
	}

}

