package pt.ulisboa.tecnico.softeng.broker.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class AdventureConstructorMethodTest extends SpockRollbackTestAbstractClass {
    def BROKER_IBAN = "BROKER_IBAN"
    def NIF_AS_BUYER = "buyerNIF"
    def BROKER_NIF_AS_SELLER = "sellerNIF"
    def OTHER_NIF = "987654321"
    def CLIENT_NIF = "123456789"
    def DRIVING_LICENSE = "IMT1234"
    def AGE = 20
    def MARGIN = 0.3
    def CLIENT_IBAN = "BK011234567"

    LocalDate begin = new LocalDate(2016, 12, 19)
    LocalDate end = new LocalDate(2016, 12, 21)


    Client client
    Broker broker
    Adventure adventure
    
    def populate4Test() {
        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        client = new Client(broker, CLIENT_IBAN, CLIENT_NIF, DRIVING_LICENSE, AGE)
    }
    
    def 'sucess'(){
        when:
        adventure = new Adventure(broker, begin, end, client, MARGIN)

        then:
        broker == adventure.getBroker()
        begin == adventure.getBegin()
        end == adventure.getEnd()
        client == adventure.getClient()
        MARGIN == adventure.getMargin()
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null

    }

    def 'nullBroker'(){
        when:
        new Adventure(null, begin, end, client, MARGIN)

        then:
        thrown(BrokerException)
    }

    def 'nullBegin'(){
        when:
        new Adventure(broker, null, end, client, MARGIN)

        then:
        thrown(BrokerException)
}

    def 'nullEnd'(){
        when:
        new Adventure(broker, begin, null, client, MARGIN)

        then:
        thrown(BrokerException)
    }

    def 'sucessEqual18'(){
        when:
        adventure = new Adventure(broker, begin, end, new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 18), MARGIN)

        then:
        broker == adventure.getBroker()
        begin == adventure.getBegin()
        end == adventure.getEnd()
        adventure.getAge() == 18
        CLIENT_IBAN == adventure.getIban()
        MARGIN == adventure.getMargin()
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }

    def 'negativeAge' (){
        when:
        Client c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 17)
        new Adventure(broker, begin, end, c, MARGIN)

        then:
        thrown(BrokerException)
    }

    def 'sucessEqual100'(){
        given:
        Client c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE + "1", 100)

        when:
        adventure = new Adventure(broker, begin, end, c, MARGIN)

        then:
        broker == adventure.getBroker()
        begin == adventure.getBegin()
        end == adventure.getEnd()
        adventure.getAge() == 100
        CLIENT_IBAN == adventure.getIban()
        MARGIN == adventure.getMargin()
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }

    def 'over100'(){
        when:
        Client c = new Client(broker, CLIENT_IBAN, OTHER_NIF, DRIVING_LICENSE, 101)
        new Adventure(broker, begin, end, c, MARGIN)

        then:
        thrown(BrokerException)
    }

    def 'negativeAmount'(){
        when:
        new Adventure(broker, begin, end, client, -100)

        then:
        thrown(BrokerException)
    }

    def 'sucess1Amount'(){
        when:
        adventure = new Adventure(broker, begin, end, client, 1)

        then:
        broker == adventure.getBroker()
        begin == adventure.getBegin()
        end == adventure.getEnd()
        adventure.getAge() == 20
        CLIENT_IBAN == adventure.getIban()
        adventure.getMargin() == 1
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }
    
    def 'sucessEqualDates'(){
        when:
        adventure = new Adventure(broker, begin, begin, client, MARGIN)

        then:
        broker == adventure.getBroker()
        begin == adventure.getBegin()
        begin == adventure.getEnd()
        adventure.getAge() == 20
        CLIENT_IBAN == adventure.getIban()
        adventure.getMargin() == MARGIN
        broker.getAdventureSet().contains(adventure)

        adventure.getPaymentConfirmation() == null
        adventure.getActivityConfirmation() == null
        adventure.getRoomConfirmation() == null
    }
    
    def 'incosistentDates'(){
        when:
        new Adventure(broker, begin, begin.minusDays(1), client, MARGIN)

        then:
        thrown(BrokerException)
    }

}