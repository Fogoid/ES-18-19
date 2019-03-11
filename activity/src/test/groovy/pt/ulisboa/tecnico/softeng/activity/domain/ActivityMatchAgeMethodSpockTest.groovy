package pt.ulisboa.tecnico.softeng.activity.domain;

import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass;


abstract class ActivityMatchAgeMethodSpockTest extends SpockRollbackTestAbstractClass {

	int MIN_AGE = 25
	int MAX_AGE = 80
	int CAPACITY = 30

	Activity activity

	def "populate4test"() {

		ActivityProvider provider = new ActivityProvider("XtremX", "ExtremeAdventure", "NIF", "IBAN")

		activity = new Activity(provider, "Bush Walking", MIN_AGE, MAX_AGE, CAPACITY)

	}

	def "success"() {

		when:
		activity.matchAge(MAX_AGE - MIN_AGE) / 2

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
		when:
		activity.matchAge(MIN_AGE - 1)

		then:
		false
	}

	def "successEqualMaxAge"() {
		when:
		activity.matchAge(MAX_AGE)

		then:
		false
	}

	def "greaterThanMaxAge"() {
		when:
		activity.matchAge(MAX_AGE + 1)

		then:
		false
	}

}