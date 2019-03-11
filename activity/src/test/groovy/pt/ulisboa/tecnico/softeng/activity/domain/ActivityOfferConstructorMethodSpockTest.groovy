package pt.ulisboa.tecnico.softeng.activity.domain
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException

class ActivityOfferConstructorMethodSpockTest extends SpockRollbackTestAbstractClass {
	private static final int CAPACITY=25
	private static final int MAX_AGE=50
	private static final int MIN_AGE=25
	private final LocalDate begin=new LocalDate(2016,12,19)
	private final LocalDate end=new LocalDate(2016,12,21)
	private Activity activity

	@Override
	def populate4Test() {
		ActivityProvider provider=new ActivityProvider('XtremX','ExtremeAdventure','NIF','IBAN')

		activity = new Activity(provider,'Bush Walking',MIN_AGE,MAX_AGE,CAPACITY)
	}

	def "success"() {
		when:
		ActivityOffer offer=new ActivityOffer(activity, begin, end,30)

		then:
		offer.getBegin() == begin
		offer.getEnd() == end
		activity.getActivityOfferSet().size() == 1
		offer.getNumberActiveOfBookings() == 0
		30 == offer.getPrice()
	}

	def "null activity"() {
		when:
		new ActivityOffer(null, begin, end,30)

		then:
		thrown(ActivityException)
	}

	def "null begin date"() {
		when:
		new ActivityOffer(activity,null, end,30)

		then:
		thrown(ActivityException)
	}

	def "null end date"() {
		when:
		new ActivityOffer(activity, begin,null,30)

		then:
		thrown(ActivityException)
	}

	def "success begin date equal end date"() {
		when:
		ActivityOffer offer=new ActivityOffer(activity, begin, begin,30)

		then:
		offer.getBegin() == begin
		offer.getEnd() == begin
		activity.getActivityOfferSet().size() == 1
		offer.getNumberActiveOfBookings() == 0
	}

	def "end date immediately before begin date"() {
		when:
		new ActivityOffer(activity, begin, begin.minusDays(1),30)

		then:
		thrown(ActivityException)
	}

	def "zero amount"() {
		when:
		new ActivityOffer(activity, begin, end,0)

		then:
		thrown(ActivityException)
	}

}