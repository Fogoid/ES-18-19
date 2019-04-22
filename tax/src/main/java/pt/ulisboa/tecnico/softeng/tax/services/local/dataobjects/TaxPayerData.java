package pt.ulisboa.tecnico.softeng.tax.services.local.dataobjects;

import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.softeng.tax.domain.Invoice;
import pt.ulisboa.tecnico.softeng.tax.domain.TaxPayer;
import pt.ulisboa.tecnico.softeng.tax.domain.IRS;

public class TaxPayerData {
	public enum Type {
		BUYER, SELLER
	}

	private String nif;
	private String name;
	private String address;
	private Type type;
	private Map<Integer, Double> taxes = new TreeMap<Integer, Double>();

	public TaxPayerData() {
	}

	public TaxPayerData(TaxPayer taxPayer) {
		TaxPayer seller;
		TaxPayer buyer;
		IRS irs=IRS.getIRSInstance();
		this.nif = taxPayer.getNif();
		this.name = taxPayer.getName();
		this.address = taxPayer.getAddress();
		for(Invoice invoice : irs.getTaxPayerByNIF(this.nif).getInvoice_sellerSet()) {
			if (invoice.getSeller().getNif().equals(taxPayer.getNif())) {
				this.type= Type.SELLER;
			}
		}
		for(Invoice invoice : irs.getTaxPayerByNIF(this.nif).getInvoice_buyerSet()) {
			if (invoice.getSeller().getNif().equals(taxPayer.getNif())) {
				this.type= Type.BUYER;
			}
		}
		if (this.type==Type.SELLER) {
			seller = (TaxPayer) taxPayer;
			this.taxes = seller.getToPayPerYear();
		} else {
			buyer = (TaxPayer) taxPayer;
			this.taxes = buyer.getTaxReturnPerYear();
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Map<Integer, Double> getTaxes() {
		return this.taxes;
	}

	public void setTaxes(Map<Integer, Double> taxes) {
		this.taxes = taxes;
	}

}
