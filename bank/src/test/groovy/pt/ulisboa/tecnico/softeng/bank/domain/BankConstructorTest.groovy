package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll

class BankConstructorTest extends SpockRollbackTestAbstractClass {
    @Shared def BANK_CODE ="BK01";
    @Shared def BANK_NAME = "Money";

    def "populate4Test"(){}

    // JFF: semi-colons not required
    def "success"(){
        when:
        def bank = new Bank(BANK_NAME, BANK_CODE);

        then:
        bank.getName() == BANK_NAME;
        bank.getCode() == BANK_CODE;
        FenixFramework.getDomainRoot().getBankSet().size() == 1;
        bank.getAccountSet().size() == 0;
        bank.getClientSet().size() == 0;

    }


    @Unroll("conflict and non-conflict test: #_beg, #_end")
    def 'conflict'() {
        when: 'when renting for a given days'
        new Bank(name, code);

        then: 'check it does not conflict'
        thrown(BankException)

        where:
        name     | code
        null     | BANK_CODE
        "    "   | BANK_CODE
        BANK_NAME| null
        BANK_NAME| "    "
        BANK_NAME| "BK0"
        BANK_NAME| "BK011"
    }

    def "notUniqueCode"(){
        new Bank(BANK_NAME, BANK_CODE);

        try{
            when:
            new Bank(BANK_NAME, BANK_CODE);

            then:
            assert false
        }
        catch(BankException be){
            then:
            FenixFramework.getDomainRoot().getBankSet().size() == 1;
        }

    }
}
