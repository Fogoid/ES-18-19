package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.car.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.bank.domain.Operation.Type;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;


class OperationConstructorSpockMethodTest  extends SpockRollbackTestAbstractClass{

    Bank bank;
    Account account;

    @Override
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

    def "nullType"(){
        when:
        new Operation(null, account, 1000);

        then:
        thrown(BankException);
    }

    def "nullAccount"(){
        when:
        new Operation(Type.WITHDRAW, null, 1000);

        then:
        thrown(BankException);
    }

    def "zeroAmount"(){
        when:
        new Operation(Type.DEPOSIT, account, 0);

        then:
        thrown(BankException);
    }

    def "oneAmount"(){
        when:
        def operation = new Operation(Type.DEPOSIT, account, 1);

        then:
        operation == bank.getOperation(operation.getReference())
    }

    def "negativeAmount"(){
        when:
        new Operation(Type.WITHDRAW, account, -1000);

        then:
        thrown(BankException);
    }
}
