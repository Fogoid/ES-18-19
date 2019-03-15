package pt.ulisboa.tecnico.softeng.bank.domain

class SpockOperationRevertMethodTest extends SpockRollbackTestAbstractClass{

    private Bank bank
    private Account account

    @Override
    def populate4Test() {
        bank = new Bank("Money","BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
    }

    def "reversing a deposit"() {
        given: "a new deposit on an account"
        def reference = account.deposit(100).getReference()
        def operation = bank.getOperation(reference)

        when: "the operation is reversed"
        def newReference = operation.revert()

        then: "the values must be those without the given deposit"
        account.getBalance() == 0
        bank.getOperation(newReference) != null
        bank.getOperation(reference) != null
    }

    def "reversing a withdraw"() {
        given: "a new deposit followed by a withdraw"
        this.account.deposit(1000)
        def reference = account.withdraw(100).getReference()
        def operation = bank.getOperation(reference)

        when: "the operation is reversed"
        def newReference = operation.revert()

        then: "the values must be those without the given withdraw"
        account.getBalance() == 1000
        bank.getOperation(newReference) != null
        bank.getOperation(reference) != null
    }
}
