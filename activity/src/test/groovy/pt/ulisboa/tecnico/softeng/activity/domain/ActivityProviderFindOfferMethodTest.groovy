package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityProviderFindOfferMethodTest extends SpockRollbackTestAbstractClass {
    def MIN_AGE = 25
    def MAX_AGE = 80
    def CAPACITY = 25
    def AGE = 40
    LocalDate begin = new LocalDate(2016, 12, 19)
    LocalDate end = new LocalDate(2016, 12, 21)

    ActivityProvider provider
    Activity activity
    ActivityOffer offer

    def populate4Test() {
        provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
        activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)
        offer = new ActivityOffer(activity, begin, end, 30)
    }

    def 'sucess'() {
        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 1
        offers.contains(offer)

    }

    def 'nullBeginDate'() {
        when:
        provider.findOffer(null, end, AGE)

        then:
        thrown(ActivityException)
    }

    def 'nullEndDate'() {
        when:
        provider.findOffer(begin, null, AGE)

        then:
        thrown(ActivityException)
    }

    def 'sucessAgeEqualMin'() {
        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, MIN_AGE)

        then:
        offers.size() == 1
        offers.contains(offer)
    }

    def 'sucessMinusOneThanMinimal'() {
        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, MIN_AGE - 1)

        then:
        offers.isEmpty()
    }

    def 'sucessAgeEqualMax'(){
        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, MAX_AGE)

        then:
        offers.size() == 1
        offers.contains(offer)
    }

    def 'AgePlusOneThanMinimal'(){
        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, MAX_AGE + 1)

        then:
        offers.isEmpty()
    }

    def 'emptyActivitySet'(){
        given:
        ActivityProvider otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN")

        when:
        List<ActivityOffer> offers = otherProvider.findOffer(begin, end, AGE)

        then:
        offers.isEmpty()
    }

    def 'emptyActivityOfferSet'(){
        given:
        ActivityProvider otherProvider = new ActivityProvider("Xtrems", "Adventure", "NIF2", "IBAN")
        new Activity(otherProvider, "Bush Walking", 18, 80, 25)

        when:
        List<ActivityOffer> offers = otherProvider.findOffer(begin, end, AGE)

        then:
        offers.isEmpty()
    }
    
    def 'twoMatchActivityOffers'(){
        given:
        new ActivityOffer(activity, begin, end, 30)
        
        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 2
    }
    
    def 'oneMatchActivityOfferAndOneNotMatch'(){
        given:
        new ActivityOffer(activity, begin, end.plusDays(1), 30)

        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 1
    }
    
    def 'oneMatchActivityOfferAndOtherNoCapacity'(){
       given:
       Activity otherActivity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, 1)
       ActivityOffer otherActivityOffer = new ActivityOffer(otherActivity, begin, end, 30)
       new Booking(provider, otherActivityOffer, "123456789", "IBAN")

        when:
        List<ActivityOffer> offers = provider.findOffer(begin, end, AGE)

        then:
        offers.size() == 1
    }

}

