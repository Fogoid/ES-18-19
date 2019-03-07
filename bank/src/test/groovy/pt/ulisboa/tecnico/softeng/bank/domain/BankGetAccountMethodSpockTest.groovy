package pt.ulisboa.tecnico.softeng.bank.domain

import pt.ulisboa.tecnico.softeng.bank.exception.BankException


class BankGetAccountMethodSpockTest extends SpockRollbackTestAbstractClass{
    Bank bank
    Client client

    @Override
    def populate4Test(){
        bank= new Bank("Money","BK01")
        client =new Client(bank,"António")
    }

    def 'success'(){
        when:
        def account= new Account(bank,client)
        def result =bank.getAccount(account.getIBAN())
        then:
        account==result
    }

    def ' nullIBAN'(){
        when:
        bank.getAccount(null)
        then:
        thrown(BankException)
    }

    def 'emptyIBAN'(){
        when:
        bank.getAccount("")
        then:
        thrown(BankException)
    }
    def 'blankIBAN'(){
        when:
        bank.getAccount("    ")
        then:
        thrown(BankException)
    }

    def 'emptySetOfAccountsDoNomatch'(){
        when:
        new Account(bank,client)
        new Account(bank,client)
        then:
        bank.getAccount("XPTO")==null

    }
}
