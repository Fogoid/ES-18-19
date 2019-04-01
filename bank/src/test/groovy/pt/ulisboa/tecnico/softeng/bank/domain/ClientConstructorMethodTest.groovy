package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ulisboa.tecnico.softeng.bank.exception.BankException

class ClientConstructorMethodTest extends SpockRollbackTestAbstractClass {
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

    // JFF: could have used data tables
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
