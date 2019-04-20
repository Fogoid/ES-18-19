package pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects;

import org.joda.time.DateTime;

import pt.ulisboa.tecnico.softeng.bank.domain.Operation;

public class BankOperationData {
	private String reference;
	private String type;
	private String sourceIban;
	private String targetIban;
	private Double value;
	private DateTime time;
	private String transactionSource;
	private String transactionReference;

	public BankOperationData() {
	}

	public BankOperationData(Operation operation) {
		this.reference = operation.getReference();
		this.type = operation.getName();
		this.sourceIban = operation.getAccount().getIBAN();
		this.value = convert_long_to_double(operation.getValue());
		this.time = operation.getTime();
		this.transactionSource = operation.getTransactionSource();
		this.transactionReference = operation.getTransactionReference();
	}

	public BankOperationData(String sourceIban, double value, String transactionSource, String transactionReference) {
		this.sourceIban = sourceIban;
		this.value = value;
		this.transactionSource = transactionSource;
		this.transactionReference = transactionReference;
	}

	public String getReference() {
		return this.reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public void setSourceIban(String sourceIban) {
		this.sourceIban = sourceIban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	public void setTargetIban(String targetIban) {
		this.targetIban = targetIban;
	}

	public Double getValue() {
		return this.value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public DateTime getTime() {
		return this.time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	public String getTransactionSource() {
		return this.transactionSource;
	}

	public void setTransactionSource(String transactionSource) {
		this.transactionSource = transactionSource;
	}

	public String getTransactionReference() {
		return this.transactionReference;
	}

	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
	}

	public double convert_long_to_double(long money){
		double currency = money/1000.0;
		return currency;
	}
}
