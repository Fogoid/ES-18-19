package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import spock.lang.Shared
import spock.lang.Unroll;


class OperationConstructorMethodTest extends SpockRollbackTestAbstractClass{

    Bank bank;
    @Shared Account account;

    def "populate4Test"(){
        bank = new Bank("Money", "BK01");
        def client = new Client(bank, "António");
        account = new Account(bank, client);
    }

    def "success"(){
        when:'when a certain operation is created'
        def operation = new Operation(Type.DEPOSIT, account, 1000 );

        then: "criteria evaluated in the construction of an operation"
        operation.getReference().startsWith(bank.getCode());
        operation.getReference().length() > Bank.CODE_SIZE;
        operation.getType() == Type.DEPOSIT;
        operation.getAccount() == account;
        operation.getValue() == 1000;
        operation.getTime() != null;
        operation == bank.getOperation(operation.getReference());
    }

    @Unroll("conflict and non-conflict test: #type, #acc, #amount")
    def 'conflict'() {
        when: 'when creating an operation'
        new Operation(type, acc, amount );

        then: 'check it does not conflict'
        thrown(BankException)

        where:
        type          | acc      |   amount
        null          | account  |   1000
        Type.WITHDRAW | null     |   1000
        Type.DEPOSIT  | account  |   0
        Type.WITHDRAW | account  |  -1000
    }


    def "oneAmount"(){
        when:
        def operation = new Operation(Type.DEPOSIT, account, 1);

        then:
        operation == bank.getOperation(operation.getReference())
    }
}
