package pt.ulisboa.tecnico.softeng.tax.domain.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.domain.Buyer
import pt.ulisboa.tecnico.softeng.tax.domain.IRS
import pt.ulisboa.tecnico.softeng.tax.domain.Invoice
import pt.ulisboa.tecnico.softeng.tax.domain.ItemType
import pt.ulisboa.tecnico.softeng.tax.domain.Seller
import pt.ulisboa.tecnico.softeng.tax.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface

class SpockIRSCancelInvoiceMethodTest extends SpockRollbackTestAbstractClass{

    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def VALUE = 16
    LocalDate date = new LocalDate(2018, 02, 13)

    private IRS irs
    private String reference
    Invoice invoice

    def populate4Test() {
        irs = IRS.getIRSInstance()
        Seller seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        Buyer buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        ItemType itemType = new ItemType(irs, FOOD, VALUE)
        invoice = new Invoice(30.0, date, itemType, seller, buyer)
        reference = invoice.getReference()
    }

    def 'success'() {

        when:
        TaxInterface.cancelInvoice(reference)

        then:
        invoice.isCancelled()
    }


    def 'nullReference'() {

        when:
        TaxInterface.cancelInvoice(null)

        then:
        thrown(TaxException)
    }


    def 'emptyReference'() {

        when:
        TaxInterface.cancelInvoice("   ")

        then:
        thrown(TaxException)
    }


    def 'referenceDoesNotExist'() {

        when:
        TaxInterface.cancelInvoice("XXXXXXXX")

        then:
        thrown(TaxException)
    }

}