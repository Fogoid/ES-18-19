package pt.ulisboa.tecnico.softeng.activity.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import spock.lang.Shared
import spock.lang.Unroll

class ActivityOfferMatchDateSpockMethodTest extends SpockRollbackTestAbstractClass{

    @Shared def begin = new LocalDate(2016, 12, 19);
    @Shared def end = new LocalDate(2016, 12, 19);

    private def offer;

    def "populate4Test"(){
        def provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN");
        def activity = new Activity(provider, "Bush Walking", 18, 80, 3);

        offer = new ActivityOffer(activity, begin, end, 30);
    }

    def "success"(){
        assert offer.matchDate(begin, end);
    }

    @Unroll("conflict and non-conflict test: #_beg, #_end")
    def 'conflict'() {
        when: 'when renting for a given days'
        offer.match(_beg, _end)

        then: 'check it does not conflict'
        thrown(ActivityException)

        where:
        _beg     | _end
        begin    | null
        null     |  end
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
