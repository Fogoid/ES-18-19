package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass;

class OperationRevertSpockMethodTest extends SpockRollbackTestAbstractClass{
    def bank;
    def account;

    def "populate4Test"(){
        bank = new Bank("Money", "BK01");
        def client = new Client(bank, "António");
        account = new Account(bank, client);
    }

    def "revertDeposit"(){
        given: 'given a determined reference and operation'
        def reference = account.deposit(100).getReference();
        def operation = bank.getOperation(reference);

        when: 'tested when an operation is reverted'
        def newReference = operation.revert();

        then:
        account.getBalance() == 0;
        bank.getOperation(newReference) != null;
        bank.getOperation(reference) != null;

    }

    def "revertWithdraw"(){
        given:
        account.deposit(1000);
        def reference = account.withdraw(100).getReference();
        def operation = bank.getOperation(reference);

        when:
        def newReference = operation.revert();

        then:
        account.getBalance() == 1000;
        bank.getOperation(newReference) != null;
        bank.getOperation(reference) != null;


    }
}
