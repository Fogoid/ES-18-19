package pt.ulisboa.tecnico.softeng.tax.domain

class IRSGetTaxPayerByNIFSpockTest extends SpockRollbackTestAbstractClass {
    def SELLER_NIF = "123456789"
    def BUYER_NIF = "987654321"

    IRS irs

    def populate4Test(){
        irs = IRS.getIRSInstance()
        new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
        new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
    }

    def 'sucessBuyer'(){
        TaxPayer taxPayer = irs.getTaxPayerByNIF(BUYER_NIF)
        taxPayer != null
        BUYER_NIF == taxPayer.getNif()

    }

    def 'sucessSeller'(){
        TaxPayer taxPayer = irs.getTaxPayerByNIF(SELLER_NIF)
        taxPayer != null
        SELLER_NIF == taxPayer.getNif()
    }

    def 'nullNIF'(){
        TaxPayer taxPayer = irs.getTaxPayerByNIF(null)
        taxPayer == null
    }

    def 'emptyNIF'(){
        TaxPayer taxPayer = irs.getTaxPayerByNIF("")
        taxPayer == null
    }

    def 'doesNotExist'(){
        TaxPayer taxPayer = irs.getTaxPayerByNIF("122456789")
        taxPayer == null
    }
}
