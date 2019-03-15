package pt.ulisboa.tecnico.softeng.bank.services.local

import pt.ulisboa.tecnico.softeng.bank.domain.Account
import pt.ulisboa.tecnico.softeng.bank.domain.Bank
import pt.ulisboa.tecnico.softeng.bank.domain.Client
import pt.ulisboa.tecnico.softeng.bank.exception.BankException

import pt.ulisboa.tecnico.softeng.bank.domain.SpockRollbackTestAbstractClass



class BankInterfaceCancelPaymentTest extends SpockRollbackTestAbstractClass{
    Bank bank
    Account account
    String reference

    @Override
    def populate4Test(){
        bank=new Bank("Money","BK01")
        def client= new Client(bank,"António")
        account= new Account(bank,client)
        reference=account.deposit(100).getReference()
    }

    def 'success'(){
        when:
        def newReference= BankInterface.cancelPayment(reference)
        then:
        bank.getOperation(newReference)!=null
    }

    def 'nullReference'(){
        when:
        BankInterface.cancelPayment(null)
        then:
        thrown(BankException)
    }

    def 'emptyReference'(){
        when:
        BankInterface.cancelPayment("")
        then:
        thrown(BankException)
    }

    def 'NotExistsReference'(){
        when:
        BankInterface.cancelPayment("XPTO")
        then:
        thrown(BankException)
    }

}
