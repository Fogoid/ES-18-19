package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.bank.exception.BankException
import pt.ulisboa.tecnico.softeng.car.domain.SpockRollbackTestAbstractClass

class ClientConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
    def CLIENT_NAME = "António"
    Bank bank

    def populate4Test(){
        bank = new Bank("Money", "BK01")
    }

    def "sucess"(){
        when:
        def client = new Client(bank, CLIENT_NAME)

        then:
        client.getName() == CLIENT_NAME
        client.getID().length() >= 1
        bank.getClientSet().contains(client)
    }

    def "nullBank"(){
        when:
        new Client(null, CLIENT_NAME)

        then:
        thrown(BankException)
    }

    def "nullClientName"(){
        when:
        new Client(bank, null)

        then:
        thrown(BankException)
    }

    def "blankClientName"(){
        when:
        new Client(bank, "  ")

        then:
        thrown(BankException)
    }

    def "emptyClientName"(){
        when:
        new Client(bank,"")

        then:
        thrown(BankException)
    }
}
