package pt.ulisboa.tecnico.softeng.bank.domain

class SpockOperationRevertMethodTest extends SpockRollbackTestAbstractClass{

    private Bank bank
    private Account account

    def "reversing a deposit"() {
        given:
        def reference = account.deposit(100).getReference()
        def operation = bank.getOperation(reference)

        when:
        def newReference = operation.revert()

        then:
        assert account.getBalance() == 0
        assert bank.getOperation(newReference)
        assert bank.getOperation(reference)
    }

    def "reversing a withdraw"() {
        given:
        this.account.deposit(1000)
        def reference = account.withdraw(100).getReference()
        def operation = bank.getOperation(reference)

        when:
        def newReference = operation.revert()

        then:
        assert account.getBalance() == 1000
        assert bank.getOperation(newReference)
        assert bank.getOperation(reference)
    }

    @Override
    def "populate for test"() {
        bank = new Bank("Money","BK01")
        def client = new Client(bank, "António")
        account = new Account(bank, client)
    }
}
