package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class ClientConstructorMethodTest extends SpockRollbackTestAbstractClass {
    def BROKER_IBAN = "BROKER_IBAN"
    def NIF_AS_BUYER = "buyerNIF"
    def BROKER_NIF_AS_SELLER = "sellerNIF"
    def CLIENT_NIF = "123456789"
    def DRIVING_LICENSE = "IMT1234"
    def AGE = 20
    def CLIENT_IBAN = "BK011234567"

    Client client
    Broker broker

    def populate4Test(){
        broker = new Broker("BR01","eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
    }

    def 'sucess'(){
        when:
        Client client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        then:
        CLIENT_IBAN == client.getIban()
        CLIENT_NIF == client.getNif()
        AGE == client.getAge()
    }

    //JFF: could have used data tables
    def 'nullBroker'(){
        when:
        new Client(null, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        then:
        thrown(BrokerException)
    }

    def 'nullIBAN'(){
        when:
        new Client(broker, null, CLIENT_NIF, DRIVING_LICENSE, AGE)

        then:
        thrown(BrokerException)
    }

    def 'emptyOrBlankIBAN'(){
        when:
        new Client(broker, "   ", CLIENT_NIF, DRIVING_LICENSE, AGE)

        then:
        thrown(BrokerException)
    }

    def 'nullNIF'(){
        when:
        new Client(broker, CLIENT_IBAN, null, DRIVING_LICENSE, AGE)

        then:
        thrown(BrokerException)

    }

    def 'emptyOrBlankNIF'(){
        when:
        new Client(broker, CLIENT_IBAN, "    ", DRIVING_LICENSE, AGE)

        then:
        thrown(BrokerException)
    }

    def 'negativeAge'(){
        when:
        new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, -1)

        then:
        thrown(BrokerException)
    }

    def 'clientExistsWithSameNIF'(){
        given:
        Client client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)

        when:
        new Client(broker, "OTHER_IBAN", CLIENT_NIF, DRIVING_LICENSE + "1", AGE)

        then:
        thrown(BrokerException)
        client ==broker.getClientByNIF(CLIENT_NIF)
    }

    def 'nullDrivingLicense'(){
        when:
        Client client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, null, AGE)

        then:
        CLIENT_IBAN == client.getIban()
        CLIENT_NIF == client.getNif()
        AGE == client.getAge()
        client.getDrivingLicense() == null
    }

    def 'emptyOrBlankDrivingLicense'(){
        when:
        new Client(broker, CLIENT_IBAN, CLIENT_NIF, "      ", AGE)

        then:
        thrown(BrokerException)
    }
}


