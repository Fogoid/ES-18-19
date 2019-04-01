package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

def class SellerToPayTest extends SpockRollbackTestAbstractClass {

    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"
    def FOOD = "FOOD"
    def TAX = 10
    LocalDate date = new LocalDate(2018, 02, 13)

    private Seller seller
    private Buyer buyer
    private ItemType itemType


    def populate4Test() {
        IRS irs = IRS.getIRSInstance()
        seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
        itemType = new ItemType(irs, FOOD, TAX)
    }


    def 'success'() {

        given:
        new Invoice(100, date, itemType, seller, buyer)
        new Invoice(100, date, itemType, seller, buyer)
        new Invoice(50, date, itemType, seller, buyer)

        when:
        def value = seller.toPay(2018)

        then:
        25.0f == value
    }

    // JFF: this could be merged with the above
    def 'yearWithoutInvoices'() {

        when:
        new Invoice(100, date, itemType, seller, buyer)
        new Invoice(100, date, itemType, seller, buyer)
        new Invoice(50, date, itemType, seller, buyer)

        then:
        0.0f == seller.toPay(2015)


    }

    def 'noInvoices'() {

        when:
        def value = seller.toPay(2018)

        then:
        value == 0
    }

    def 'before1970'() {

        when:
        new Invoice(100, new LocalDate(1969, 02, 13), itemType, seller, buyer)
        new Invoice(50, new LocalDate(1969, 02, 13), itemType, seller, buyer)


        def value = seller.toPay(1969)
        value == 0


        then:
        thrown(TaxException);

    }

    def 'equal1970'() {

        given:
        new Invoice(100, new LocalDate(1970, 02, 13), itemType, seller, buyer)
        new Invoice(50, new LocalDate(1970, 02, 13), itemType, seller, buyer)

        when:
        def value = seller.toPay(1970)

        then:
        value == 15.0f
    }

    def 'ignoreCancelled'() {

        given:
        new Invoice(100, date, itemType, seller, buyer)
        Invoice invoice = new Invoice(100, date, itemType, seller, buyer)
        new Invoice(50, date, itemType, seller, buyer)

        when:
        invoice.cancel();
        def value = seller.toPay(2018)

        then:
        15 == value
    }


}
