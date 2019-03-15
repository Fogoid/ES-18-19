package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData
import spock.lang.Shared
import spock.lang.Unroll

class BankInterfaceProcessPaymentMethodTest extends SpockRollbackTestAbstractClass{

    @Shared def TRANSACTION_SOURCE = "ADVENTURE"
    @Shared def TRANSACTION_REFERENCE = "REFERENCE"

    private Bank bank
    private Account account
    private String iban

    @Override
    def populate4Test() {
        bank = new Bank("Money", "BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
        iban = account.getIBAN()
        account.deposit(500)
    }

    def "success"() {
        when: "check if this.account has an IBAN and make a new reference"
        account.getIBAN()
        def newReference = BankInterface
                .processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: "the reference of the new payment has to have the following properties"
        newReference != null
        newReference.startsWith("BK01")

        bank.getOperation(newReference) != null
        bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
    }

    def "make transfer between to accounts on different banks"() {
        given: "a new account"
        def otherBank = new Bank("Money", "BK02")
        def otherClient = new Client(otherBank, "Manuel")
        def otherAccount = new Account(otherBank, otherClient)
        def otherIban = otherAccount.getIBAN()
        otherAccount.deposit(1000)

        when: "make a transfer between accounts"
        BankInterface
                .processPayment(new BankOperationData(otherIban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))
        BankInterface
                .processPayment(new BankOperationData(iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE + "PLUS"))

        then: "check if balance is correct"
        otherAccount.getBalance() == 900
        account.getBalance() == 400
    }

    def 'redo an already payed'() {
        given: 'the account iban'
        this.account.getIBAN()

        when: 'the same payment processing is made'
        String firstReference = BankInterface
                .processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));
        String secondReference = BankInterface
                .processPayment(new BankOperationData(this.iban, 100, TRANSACTION_SOURCE, TRANSACTION_REFERENCE));

        then: 'the following is expected'
        firstReference == secondReference
        this.account.getBalance() == 400
    }

    def 'one ammount'(){
        when: 'a payment is processed with the following attributes'
        BankInterface.processPayment(new BankOperationData(this.iban, 1, TRANSACTION_SOURCE, TRANSACTION_REFERENCE))

        then: 'the following balance is expected'
        this.account.getBalance() == 499
    }

    @Unroll("BankInterface.processPayment: #ib #value #tsource #tref")
    def 'check invalid payments'(){
        when:
        BankInterface.processPayment(new BankOperationData(ib, value, tsource, tref))

        then:
        thrown(BankException)

        where:
        ib | value | tsource | tref
        null | 100 | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
        "  " | 100 | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
        this.iban | 0 | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
        "other" | 0 | TRANSACTION_SOURCE | TRANSACTION_REFERENCE
    }
}
