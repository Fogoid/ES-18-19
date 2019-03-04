package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import spock.lang.Shared
import spock.lang.Unroll

class BuyerConstructorSpockTest extends SpockRollbackTestAbstractClass {
    @Shared private static final def ADDRESS = "Somewhere";
    @Shared private static final def NAME = "José Vendido";
    @Shared private static final def NIF = "123456789";

    @Shared IRS irs;

    def "populate4Test"(){
        irs = IRS.getIRSInstance();

    }

    def "uniqueNIF"(){
        def seller = new Buyer(this.irs, NIF, NAME, ADDRESS);

        try {
            when:
            new Buyer(this.irs, NIF, NAME, ADDRESS);

            then:
            assert false
        }

        catch(TaxException){
            then:
            seller == IRS.getIRSInstance().getTaxPayerByNIF(NIF);
        }
    }

    @Unroll("conflict and non-conflict test: #dt1, #dt2, #dt3, #dt4 || #res")
    def 'conflict'() {
        when: 'when renting for a given days'
        new Buyer(_irs, nif, name, addr);

        then: 'check it does not conflict'
        thrown(TaxException);

        where:
        _irs   | nif         | name  | addr
        irs    | null        | NAME  | ADDRESS
        irs    |  ""         | NAME  | ADDRESS
        irs    | "12345678"  | NAME  | ADDRESS
        irs    | NIF         | null  | ADDRESS
        irs    | NIF         | ""    | ADDRESS
        irs    | NIF         | NAME  | null
        irs    | NIF         | NAME  | ""
    }

}
