package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework

class TaxPersistenceTest extends SpockPersistenceTestAbstractClass{
    def SELLER_NIF = "123456789"
    def BUYER_NIF ="987654321"
    def FOOD ="FOOD"
    def VALUE =16
    def date = LocalDate.parse('2018-02-13')

    @Override
    def whenCreateInDatabase() {

        def irs =IRS.getIRSInstance()
        def seller = new Seller(irs,SELLER_NIF,"José Vendido","Somewhere")
        def buyer= new Buyer(irs,BUYER_NIF,"Manuel Comprado","Anywhere")
        def it =new ItemType(irs,FOOD,VALUE)

        new Invoice(VALUE,date,it,seller,buyer)

    }

    @Override
    def thenAssert() {
        def irs = IRS.getIRSInstance()

        assert 2 == irs.getTaxPayerSet().size()

        List<TaxPayer> taxPayers1 =new ArrayList<>(irs.getTaxPayerSet())
        def taxPayer1=taxPayers1.get(0)
        if (taxPayer1 instanceof Seller){
            assert taxPayer1.getNif()==SELLER_NIF
        }
        else{
            assert taxPayer1.getNif()==BUYER_NIF
        }

        List<TaxPayer> taxPayers2 =new ArrayList<>(irs.getTaxPayerSet())
        def taxPayer2=taxPayers2.get(1)
        if(taxPayer2 instanceof Seller){
            assert taxPayer2.getNif()==SELLER_NIF
        }
        else{
            assert taxPayer2.getNif()==BUYER_NIF
        }

        assert irs.getItemTypeSet().size()==1
        List<ItemType> itemTypes =new ArrayList<>(irs.getItemTypeSet())
        def itemType =itemTypes.get(0)
        assert itemType.getTax()==VALUE
        assert itemType.getName()==FOOD

        assert irs.getInvoiceSet().size()==1
        List<Invoice> invoices =new ArrayList<>(irs.getInvoiceSet())
        def invoice =invoices.get(0)
        assert (invoice.getValue()-VALUE)==0
        assert invoice.getReference()!=null
        assert invoice.getDate()==date
        assert invoice.getBuyer().getNif()==BUYER_NIF
        assert invoice.getSeller().getNif()==SELLER_NIF
        assert invoice.getItemType() == itemType
        assert invoice.getTime() != null
        assert !invoice.getCancelled()

    }

    @Override
    def deleteFromDatabase(){
        FenixFramework.getDomainRoot().getIrs().delete()
    }

}
