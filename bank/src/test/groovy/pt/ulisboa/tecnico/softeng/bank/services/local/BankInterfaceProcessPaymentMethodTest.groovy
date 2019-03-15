package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.domain.Operation
import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.services.local.dataobjects.BankOperationData

class BankInterfaceProcessPaymentMethodTest extends SpockRollbackTestAbstractClass{

    def TRANSACTION_SOURCE = "ADVENTURE"
    def TRANSACTION_REFERENCE = "REFERENCE"

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
        assert newReference != null
        assert newReference.startsWith("BK01")

        assert bank.getOperation(newReference) != null
        assert bank.getOperation(newReference).getType() == Operation.Type.WITHDRAW
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
}
