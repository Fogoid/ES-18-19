package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

class BankInterfaceGetOperationDataMethodTest extends SpockRollbackTestAbstractClass{

    def AMOUNT = 100
    private Bank bank
    private Account account
    private String reference

    def populate4Test() {
        bank = new Bank("Money", "BK01")
        Client client = new Client(bank, "António")
        account = new Account(bank, client)
        reference = account.deposit(AMOUNT).getReference()
    }

    def 'success'() {

        when:
        BankOperationData data = BankInterface.getOperationData(reference)

        then:
        reference == data.getReference()
        account.getIBAN() == data.getIban()
        Type.DEPOSIT.name() == data.getType()
        AMOUNT == data.getValue()
        data.getTime() != null
    }


    // JFF: could have used data tables
    def 'nullReference'() {
        when:
        BankInterface.getOperationData(null)

        then:
        thrown(BankException)
    }


    def 'emptyReference'() {

        when:
        BankInterface.getOperationData("")

        then:
        thrown(BankException)
    }


    def 'referenceNotExists'() {

        when:
        BankInterface.getOperationData("XPTO")

        then:
        thrown(BankException)
    }

}
