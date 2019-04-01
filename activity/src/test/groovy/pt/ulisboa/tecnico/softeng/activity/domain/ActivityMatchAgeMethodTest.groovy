package pt.ulisboa.tecnico.softeng.activity.domain;


class ActivityMatchAgeMethodTest extends SpockRollbackTestAbstractClass {

	int MIN_AGE = 25
	int MAX_AGE = 80
	int CAPACITY = 30

	Activity activity

	def populate4Test() {
		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")
		activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)

	}

	// JFF: duplication / data table
	def "success"() {
		given:

		int result = (MAX_AGE - MIN_AGE) / 2
		// JFF: it would be better to use .intdiv(2)

		expect:
		activity.matchAge(result) == true
	}

	def "successEqualMinAge"() {
		expect:
		activity.matchAge(MIN_AGE) == true
	}

	def "lessThanMinAge"() {
		expect:
		activity.matchAge(MIN_AGE - 1)  == false
	}

	def "successEqualMaxAge"() {
		expect:
		!activity.matchAge(MAX_AGE) == false
	}

	def "greaterThanMaxAge"() {
		expect:
		activity.matchAge(MAX_AGE + 1)  == false
	}

}