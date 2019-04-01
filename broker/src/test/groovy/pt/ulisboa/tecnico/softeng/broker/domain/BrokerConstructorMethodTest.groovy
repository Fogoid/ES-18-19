package pt.ulisboa.tecnico.softeng.broker.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException

class BrokerConstructorMethodTest extends SpockRollbackTestAbstractClass {
    def BROKER_CODE = "BR01"
    def BROKER_NAME = "WeExplore"
    def BROKER_IBAN = "BROKER_IBAN"
    def BROKER_NIF_AS_SELLER = "sellerNIF"
    def NIF_AS_BUYER = "buyerNIF"
    @Override
    def populate4Test(){}

    def 'success'(){
        when:
        def broker= new Broker(BROKER_CODE,BROKER_NAME,BROKER_NIF_AS_SELLER,NIF_AS_BUYER,BROKER_IBAN)
        then:
        broker.getCode()==BROKER_CODE
        broker.getName()==BROKER_NAME
        broker.getAdventureSet().size()==0
        FenixFramework.getDomainRoot().getBrokerSet().contains(broker)
    }

    // JFF: could have used data tables
    def 'nullCode'() {
        when:
        new Broker(null, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'emptyCode'() {
        when:
        new Broker("", BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'blankCode'() {
        when:
        new Broker("  ", BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'uniqueCode'() {
        given:
        def broker = new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        when:
        new Broker(BROKER_CODE, "WeExploreX", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==1
        FenixFramework.getDomainRoot().getBrokerSet().contains(broker)
    }
    def 'nullName'() {
        when:
        new Broker(BROKER_CODE, null, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'emptyName'() {
        when:
        new Broker(BROKER_CODE, "", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'blankName'() {
        when:
        new Broker(BROKER_CODE, "   ", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'nullSellerNIF'() {
        when:
        new Broker(BROKER_CODE, BROKER_NAME, null, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'uniqueSellerNIF'() {
        given:
        new Broker(BROKER_CODE,BROKER_NAME, BROKER_NIF_AS_SELLER, "123456789", BROKER_IBAN)
        when:
        new Broker(BROKER_CODE, BROKER_NAME,  BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==1
    }
    def 'nullBuyerNIF'() {
        when:
        new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, null, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'emptyBuyerNIF'() {
        when:
        new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, "   ", BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'uniqueBuyerNIFOne'() {
        given:
        new Broker(BROKER_CODE,BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        when:
        new Broker(BROKER_CODE,BROKER_NAME, "123456789", NIF_AS_BUYER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==1
    }

    def 'uniqueBuyerSellerNIFTwo'(){
        given:
        new Broker(BROKER_CODE,BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN)
        when:
        new Broker(BROKER_CODE,BROKER_NAME, NIF_AS_BUYER, "123456789", BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==1
    }

    def 'nullIBAN'(){
        when:
        new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, null)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'emptyIBAN'(){
        when:
        new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, NIF_AS_BUYER, "  ")
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }
    def 'sellerNIFDifferentBuyerNIF'(){
        when:
        new Broker(BROKER_CODE, BROKER_NAME, BROKER_NIF_AS_SELLER, BROKER_NIF_AS_SELLER, BROKER_IBAN)
        then:
        def error= thrown(BrokerException)
        and:
        FenixFramework.getDomainRoot().getBrokerSet().size()==0
    }

}