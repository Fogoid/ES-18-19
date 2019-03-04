package pt.ulisboa.tecnico.softeng.tax.domain

import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ist.fenixframework.FenixFramework



class ItemTypeConstructorSpockTest extends SpockRollbackTestAbstractClass {
    def CAR = "CAR"
    def TAX = 23

    IRS irs

    def populate4Test() {
        irs = IRS.getIRSInstance()
    }

    def 'sucess'() {
        when:
        def itemType = new ItemType(irs, CAR, TAX)

        then:
        itemType.getName() == CAR
        itemType.getTax() == TAX

        irs.getItemTypeByName(CAR) != null
        irs.getItemTypeByName(CAR) == itemType

    }

    def 'uniqueName'(){
        given:
        def itemType = new ItemType(irs, CAR, TAX)

        when:
        new ItemType(this.irs, CAR, TAX)

        then:
        thrown(TaxException)
        itemType == IRS.getIRSInstance().getItemTypeByName(CAR)
    }

}