package pt.ulisboa.tecnico.softeng.tax.domain;

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException;

import java.util.Map;
import java.util.stream.Collectors;

public class TaxPayer extends TaxPayer_Base {

	private final static int PERCENTAGE = 5;
	protected TaxPayer() {
		// this is a FenixFramework artifact; if not present, compilation fails.
		// the empty constructor is used by the base class to materialize objects from
		// the database, and in this case the classes Seller_Base and Buyer_Base, which
		// extend this class, have the empty constructor, which need to be present in
		// their superclass
		super();
	}

	public TaxPayer(IRS irs, String NIF, String name, String address) {
		checkArguments(irs, NIF, name, address);

		setNif(NIF);
		setName(name);
		setAddress(address);

		irs.addTaxPayer(this);
	}


	public void delete() {
		for (Invoice invoice : getInvoice_buyerSet()) {
			invoice.delete();
		}
		for (Invoice invoice : getInvoice_sellerSet()) {
			invoice.delete();
		}
		setIrs(null);

		deleteDomainObject();
	}

	protected void checkArguments(IRS irs, String NIF, String name, String address) {
		if (NIF == null || NIF.length() != 9) {
			throw new TaxException();
		}

		if (name == null || name.length() == 0) {
			throw new TaxException();
		}

		if (address == null || address.length() == 0) {
			throw new TaxException();
		}

		if (irs.getTaxPayerByNIF(NIF) != null) {
			throw new TaxException();
		}

	}
	/**SELLER CODE*/
	public double toPay(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

		double result = 0;
		for (Invoice invoice : getInvoice_sellerSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				result = result + invoice.getIva();
			}
		}
		return result;
	}

	public Map<Integer, Double> getToPayPerYear() {
		return getInvoice_sellerSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> toPay(y)));
	}

	/**BUYER CODE*/

	public double taxReturn(int year) {
		if (year < 1970) {
			throw new TaxException();
		}

		double result = 0;
		for (Invoice invoice : getInvoice_buyerSet()) {
			if (!invoice.isCancelled() && invoice.getDate().getYear() == year) {
				result = result + invoice.getIva() * PERCENTAGE / 100;
			}
		}
		return result;
	}

	public Map<Integer, Double> getTaxReturnPerYear() {
		return getInvoice_buyerSet().stream().map(i -> i.getDate().getYear()).distinct()
				.collect(Collectors.toMap(y -> y, y -> taxReturn(y)));
	}

	/**BOTH CODE*/

	public Invoice getInvoiceByReference(String invoiceReference) {
		if (invoiceReference == null || invoiceReference.isEmpty()) {
			throw new TaxException();
		}

		for (Invoice invoice : getInvoice_buyerSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		for (Invoice invoice : getInvoice_sellerSet()) {
			if (invoice.getReference().equals(invoiceReference)) {
				return invoice;
			}
		}
		return null;
	}
}