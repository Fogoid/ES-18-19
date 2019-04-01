package pt.ulisboa.tecnico.softeng.activity.domain

import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityConstructorMethodTest extends SpockRollbackTestAbstractClass {
    def IBAN = "IBAN"
    def NIF = "NIF"
    def PROVIDER_NAME = "Bush Walking"
    def MIN_AGE = 25
    def MAX_AGE = 50
    def CAPACITY = 30

    ActivityProvider provider

    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", NIF, IBAN)
    }

    def 'sucess'() {
        when:
        Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, CAPACITY)

        then:
        activity.getCode().startsWith(this.provider.getCode())
        activity.getCode().length() > ActivityProvider.CODE_SIZE
        "Bush Walking" == activity.getName()
        MIN_AGE == activity.getMinAge()
        MAX_AGE == activity.getMaxAge()
        CAPACITY == activity.getCapacity()
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1

    }

    // JFF: duplication could be avoided with data table
    def 'nullProvider'() {
        when:
        new Activity(null, PROVIDER_NAME, MIN_AGE, MAX_AGE, CAPACITY)

        then:
        thrown(ActivityException)

    }

    def 'nullProviderName'() {
        when:
        new Activity(this.provider, null, MIN_AGE, MAX_AGE, CAPACITY)

        then:
        thrown(ActivityException)
    }

    def 'emptyProviderName'() {
        when:
        new Activity(this.provider, "    ", MIN_AGE, MAX_AGE, CAPACITY)

        then:
        thrown(ActivityException)
    }

    def 'successMinAgeEqual18'() {
        when:
        Activity activity = new Activity(this.provider, PROVIDER_NAME, 18, MAX_AGE, CAPACITY)

        then:
        activity.getCode().startsWith(this.provider.getCode())
        activity.getCode().length() > ActivityProvider.CODE_SIZE
        "Bush Walking" == activity.getName()
        activity.getMinAge() == 18
        MAX_AGE == activity.getMaxAge()
        CAPACITY == activity.getCapacity()
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1

    }

    def 'MinAgeLessThan18'() {
        when:
        new Activity(this.provider, PROVIDER_NAME, 17, MAX_AGE, CAPACITY)

        then:
        thrown(ActivityException)
    }

    def 'successMaxAge99'() {
        when:
        Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, 99, CAPACITY)

        then:
        activity.getCode().startsWith(this.provider.getCode())
        activity.getCode().length() > ActivityProvider.CODE_SIZE
        "Bush Walking" == activity.getName()
        MIN_AGE == activity.getMinAge()
        activity.getMaxAge() == 99
        CAPACITY == activity.getCapacity()
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }

    def 'MaxAgeGreaterThan99'() {
        when:
        new Activity(this.provider, PROVIDER_NAME, MIN_AGE, 100, CAPACITY)

        then:
        thrown(ActivityException)
    }

    def 'successMinAgeEqualMaxAge'(){
        when:
        Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MIN_AGE, CAPACITY)

        then:
        activity.getCode().startsWith(this.provider.getCode())
        activity.getCode().length() > ActivityProvider.CODE_SIZE
        "Bush Walking" == activity.getName()
        MIN_AGE == activity.getMinAge()
        MIN_AGE == activity.getMaxAge()
        CAPACITY == activity.getCapacity()
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }

    def 'MinAgeGreaterThanMaxAge'(){
        when:
        new Activity(this.provider, PROVIDER_NAME, MAX_AGE + 10, MAX_AGE, CAPACITY)

        then:
        thrown(ActivityException)
    }

    def 'MinAgeGreaterEqualMaxAgePlusOne'(){
        when:
        new Activity(this.provider, PROVIDER_NAME, MAX_AGE + 1, MAX_AGE, CAPACITY)

        then:
        thrown(ActivityException)
    }

    def 'successCapacityOne'(){
        when:
        Activity activity = new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, 1)

        then:
        activity.getCode().startsWith(this.provider.getCode())
        activity.getCode().length() > ActivityProvider.CODE_SIZE
        "Bush Walking" == activity.getName()
        MIN_AGE == activity.getMinAge()
        MAX_AGE == activity.getMaxAge()
        activity.getCapacity() == 1
        activity.getActivityOfferSet().size() == 0
        provider.getActivitySet().size() == 1
    }

    def 'zeroCapacity'(){
        when:
        new Activity(this.provider, PROVIDER_NAME, MIN_AGE, MAX_AGE, 0)

        then:
        thrown(ActivityException)
    }
}
