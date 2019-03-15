package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

def class SpockTaxPayerGetInvoiceByReferenceTest extends SpockRollbackTestAbstractClass {

    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def VALUE = 16
    def TAX = 23
    private final LocalDate date = new LocalDate(2018, 02, 13)

    private Seller seller
    private Buyer buyer
    private ItemType itemType
    private Invoice invoice

    def populate4Test() {
        IRS irs = IRS.getIRSInstance()
        seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        itemType = new ItemType(irs, FOOD, TAX)
        invoice = new Invoice(VALUE, date, itemType, seller, buyer)
    }

    def 'success'() {


        expect:
        invoice == seller.getInvoiceByReference(invoice.getReference())
    }

    def 'nullReference'() {

        when:
        seller.getInvoiceByReference(null)

        then:
        thrown(TaxException)
    }

    def 'emptyReference'() {

        when:
        seller.getInvoiceByReference("")

        then:
        thrown(TaxException)
    }

    def 'desNotExist'() {


        expect:
        null == seller.getInvoiceByReference(BUYER_NIF)
    }
}
