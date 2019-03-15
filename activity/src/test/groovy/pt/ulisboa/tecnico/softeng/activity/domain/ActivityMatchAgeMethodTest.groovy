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

	def "success"() {
		given:

		int result = (MAX_AGE - MIN_AGE) / 2

		when:
		activity.matchAge(result)

		then:
		true
	}

	def "successEqualMinAge"() {
		when:
		activity.matchAge(MIN_AGE)

		then:
		true
	}

	def "lessThanMinAge"() {
		given:
		activity.matchAge(MIN_AGE - 1)
		false
	}

	def "successEqualMaxAge"() {
		given:
		activity.matchAge(MAX_AGE)
		false
	}

	def "greaterThanMaxAge"() {
		given:
		activity.matchAge(MAX_AGE + 1)
		false
	}

}