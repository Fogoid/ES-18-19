package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class BankConstructorSpockTest extends SpockRollbackTestAbstractClass {
    def BANK_CODE ="BK01";
    def BANK_NAME = "Money";

    def "populate4Test"(){}

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

    def "nullName"(){
        when:
        new Bank(null, BANK_CODE);

        then:
        thrown(BankException);
    }

    def "emptyName"(){
        when:
        new Bank("    ", BANK_CODE);

        then:
        thrown(BankException);
    }

    def "nullCode"(){
        when:
        new Bank(BANK_NAME, null);

        then:
        thrown(BankException);

    }

    def "emptyCode"(){
        when:
        new Bank(BANK_NAME, "    ");

        then:
        thrown(BankException);
    }

    def "inconsistentCodeSmaller"(){
        when:
        new Bank(BANK_NAME, "BK0")

        then:
        thrown(BankException);
    }

    def "inconsistentCodeBigger"(){
        when:
        new Bank(BANK_NAME, "BK011");

        then:
        thrown(BankException);
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
