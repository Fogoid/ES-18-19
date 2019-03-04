package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException

class InvoiceConstructorSpockTest extends SpockRollbackTestAbstractClass{
    private static final def SELLER_NIF = "123456789";
    private static final def BUYER_NIF = "987654321";
    private static final def FOOD = "FOOD";
    private static final def VALUE = 16;
    private static final def TAX = 23;
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

    def "success"(){

        when:
        Invoice invoice = new Invoice(VALUE, date, itemType, seller, buyer);

        then:
        invoice.getReference() != null;
        invoice.getValue() == VALUE;
        invoice.getDate() == date;
        invoice.getItemType() == itemType;
        invoice.getSeller() == seller;
        invoice.getBuyer() == buyer;
        Math.abs(invoice.getIva()-VALUE*TAX/100.0)<0.00001f; //Important stuff
        !invoice.isCancelled();

        invoice == seller.getInvoiceByReference(invoice.getReference());
        invoice == buyer.getInvoiceByReference(invoice.getReference());
    }

    def "nullSeller"(){
        when:
        new Invoice(VALUE, date, itemType, null, buyer);

        then:
        thrown(TaxException);
    }

    def "nullBuyer"(){
        when:
        new Invoice(VALUE, date, itemType, seller, null);

        then:
        thrown(TaxException);
    }

    def "nullItemType"(){
        when:
        new Invoice(VALUE, date, null, seller, buyer);

        then:
        thrown(TaxException);
    }

    def "zeroValuer"(){
        when:
        new Invoice(0, date, itemType, seller, buyer);

        then:
        thrown(TaxException);
    }

    def "negativeValue"(){
        when:
        new Invoice(-23.6f, date, itemType, seller, buyer);

        then:
        thrown(TaxException);
    }

    def "nullDate"(){
        when:
        new Invoice(VALUE, null, this.itemType, this.seller, this.buyer);

        then:
        thrown(TaxException);
    }

    def "before1970"(){
        when:
        new Invoice(VALUE, new LocalDate(1969, 12, 31), itemType, seller, buyer);

        then:
        thrown(TaxException);
    }

    def "equal1970"(){
        when:
        assert true

        then:
        new Invoice(VALUE, new LocalDate(1970, 01, 01), this.itemType, this.seller, this.buyer);
    }
}
