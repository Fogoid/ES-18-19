package pt.ulisboa.tecnico.softeng.tax.domain


import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class SellerConstructorTest extends SpockRollbackTestAbstractClass {
	private static final String ADDRESS="Somewhere"
	private static final String NAME="José Vendido"
	private static final String NIF="123456789"
	IRS irs

	@Override
	def "populate4Test"() {
		irs=IRS.getIRSInstance()
	}

	def "success"() {
		when:
		Seller seller=new Seller(irs,NIF,NAME,ADDRESS)

		then:
		seller.getNif() == NIF
		seller.getName() == NAME
		seller.getAddress() == ADDRESS
		IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
	}

	// JFF: this is not running as a test
	def "unique nif"() {

		Seller seller=new Seller(irs,NIF,NAME,ADDRESS)

		try {
			when:
			new Seller(irs,NIF,NAME,ADDRESS)

			then:
			false
		} catch(TaxException ie) {
			then:
			IRS.getIRSInstance().getTaxPayerByNIF(NIF) == seller
		}

	}

	// JFF: could have used data tables
	def "null nif"() {
		when:
		new Seller(irs,null,NAME,ADDRESS)

		then:
		thrown(TaxException)
	}

	def "empty nif"() {
		when:
		new Seller(irs,"",NAME,ADDRESS)

		then:
		thrown(TaxException)
	}

	def "null name"() {
		when:
		new Seller(irs,NIF,null,ADDRESS)

		then:
		thrown(TaxException)
	}

	def "empty name"() {
		when:
		new Seller(irs,NIF,"",ADDRESS)

		then:
		thrown(TaxException)
	}

	def "null address"() {
		when:
		new Seller(irs,NIF,NAME,null)

		then:
		thrown(TaxException)
	}

	def "empty address"() {
		when:
		new Seller(irs,NIF,NAME,"")

		then:
		thrown(TaxException)
	}

}
