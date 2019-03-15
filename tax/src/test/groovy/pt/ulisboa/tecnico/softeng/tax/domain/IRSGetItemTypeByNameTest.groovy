package pt.ulisboa.tecnico.softeng.tax.domain

import spock.lang.Unroll


def class IRSGetItemTypeByNameTest extends SpockRollbackTestAbstractClass {
	def FOOD = "FOOD"
	def VALUE = 16

	private IRS irs

	@Override
	def populate4Test() {
		irs = IRS.getIRSInstance()
		new ItemType(irs, FOOD, VALUE)
	}

	def "getting an item type by name success"() {
		when: 'an item type is created successfully'
		ItemType itemType = this.irs.getItemTypeByName(FOOD)

		then: 'the following properties must be met'
		itemType.getName() != null
		itemType.getName() == FOOD
	}

	@Unroll('irs.getItemTypeByName: #name')
	def 'check invalid names'() {
		when: 'creating an item type with invalid name'
		def itemType = irs.getItemTypeByName(name)

		then:
		itemType == null

		where:
		name << [null, '', 'CAR']
	}
}
