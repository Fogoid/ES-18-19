package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Shared;



class ActivityProviderConstructorMethodTest extends SpockRollbackTestAbstractClass{
    @Shared def PROVIDER_CODE = "XtremX";
    @Shared def PROVIDER_NAME = "Adventure++";
    @Shared def IBAN = "IBAN";
    @Shared def NIF = "NIF";

    @Override
    def populate4Test() { }

    def 'success'(){
        when:
        def provider =new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,NIF,IBAN);

        then:
        provider.getName()==PROVIDER_NAME;
        provider.getCode().length()==ActivityProvider.CODE_SIZE;
        FenixFramework.getDomainRoot().getActivityProviderSet().size()==1;
        provider.getActivitySet().size()==0;
    }

    // JFF: duplication / data tables
    def 'nullCode'(){
        when:
        new ActivityProvider(null,PROVIDER_NAME,NIF,IBAN)
        then:
        thrown(ActivityException)
    }
    def 'emptyCode'(){
        when:
        new ActivityProvider("    ",PROVIDER_NAME,NIF,IBAN)
        then:
        thrown(ActivityException)
    }

    def 'nullName'(){
        when:
        new ActivityProvider(PROVIDER_CODE,null,NIF,IBAN)
        then:
        thrown(ActivityException)
    }
    def 'emptyNAme'(){
        when:
        new ActivityProvider(PROVIDER_CODE,"    ",NIF,IBAN)
        then:
        thrown(ActivityException)
    }
    def 'fiveCharCode'(){
        when:
        new ActivityProvider("12345",PROVIDER_NAME,NIF,IBAN)
        then:
        thrown(ActivityException)
    }
    def 'sevenCharCode'() {
        when:
        new ActivityProvider("1234567", PROVIDER_NAME, NIF, IBAN)
        then:
        thrown(ActivityException)
    }

    def 'noteUniqueCode'(){
        given:
        new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,NIF,IBAN)
        when:
        new ActivityProvider("123456",PROVIDER_NAME,NIF+"2",IBAN)
        then:
        def error= thrown(ActivityException)
        and:
        FenixFramework.getDomainRoot().getActivityProviderSet().size()==1
    }
    def 'noteUniqueNIF'(){
        given:
        new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,NIF,IBAN);
        when:
        new ActivityProvider("123456","jdgsk",NIF,IBAN);
        then:
        def error= thrown(ActivityException)
        and:
        FenixFramework.getDomainRoot().getActivityProviderSet().size()==1;
    }

    def 'nullNIF'(){
        when:
        new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,null,IBAN);
        then:
        thrown(ActivityException);
    }
    def 'emptyNIF'(){
        when:
        new ActivityProvider(PROVIDER_CODE,PROVIDER_NAME,"   ",IBAN);
        then:
        thrown(ActivityException);
    }


}