package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class BuyerToReturnTest extends SpockRollbackTestAbstractClass {
    private static final def SELLER_NIF = "123456789";
    private static final def BUYER_NIF = "987654321";
    private static final def FOOD = "FOOD";
    private static final def TAX = 10;
    private final def date = new LocalDate(2018, 02, 13);

    private Seller seller;
    private Buyer buyer;
    private ItemType itemType;

    def "populate4Test"(){
        IRS irs = IRS.getIRSInstance();
        seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere");
        buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere");
        itemType = new ItemType(irs, FOOD, TAX);
    }

    def "yearsWithoutInvoice"(){
        given:
        new Invoice(100, date, itemType, seller, buyer);
        new Invoice(100, date, itemType, seller, buyer);
        new Invoice(50, date, itemType, seller, buyer);

        when:
        def value = buyer.taxReturn(2017);

        then:
        value == 0;

    }

    def "noInvoices"(){
        when:
        def value = this.buyer.taxReturn(2018);

        then:
        value == 0;
    }

    def "before1970"(){
        when:
        new Invoice(100, new LocalDate(1969, 02, 13), itemType, seller, buyer);
        buyer.taxReturn(1969) == 0;

        then:
        thrown(TaxException);
    }

    def "equal1970"(){
        given: 'given a determined Invoice'
        new Invoice(100, new LocalDate(1970, 02, 13), itemType, seller, buyer);

        when:
        def value = this.buyer.taxReturn(1970);

        then:
        value == 0.5;
    }

    def "ignoreCancelled"(){
        given:
        new Invoice(100, date, itemType, seller, buyer);
        Invoice invoice = new Invoice(100, date, itemType, seller, buyer);
        new Invoice(50, date, itemType, seller, buyer);

        when:
        invoice.cancel()
        def value = buyer.taxReturn(2018);

        then:
        value == 0.75;
    }

}
