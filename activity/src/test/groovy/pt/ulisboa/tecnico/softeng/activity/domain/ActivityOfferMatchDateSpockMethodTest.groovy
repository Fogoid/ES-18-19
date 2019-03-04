package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityOfferMatchDateSpockMethodTest extends SpockRollbackTestAbstractClass{

    private final def begin = new LocalDate(2016, 12, 19);
    private final def end = new LocalDate(2016, 12, 19);

    private def offer;

    def "populate4Test"(){
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3);

        offer = new ActivityOffer(activity, begin, end, 30);
    }

    def "success"(){
        assert offer.matchDate(begin, end);
    }

    def "nullBeginDate"(){
        when:
        offer.matchDate(null, end);

        then:
        thrown(ActivityException)
    }

    def "nullEndDate"(){
        when:
        offer.matchDate(null, end);

        then:
        thrown(ActivityException);
    }

    def "beginPlusOne"() {
        assert !offer.matchDate(begin.plusDays(1), end);
    }

    def "beginMinusOne"(){
        assert !offer.matchDate(begin.minusDays(1), end);
    }

    def "endPlusOne"(){
        assert !offer.matchDate(begin, end.plusDays(1));

    }

    def "endMinusOne"(){
        assert !offer.matchDate(begin, end.minusDays(1));
    }

}
