package pt.ulisboa.tecnico.softeng.activity.services.local

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.activity.domain.Activity
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityProvider
import pt.ulisboa.tecnico.softeng.activity.domain.Booking
import pt.ulisboa.tecnico.softeng.activity.domain.Processor
import pt.ulisboa.tecnico.softeng.activity.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.activity.exception.ActivityException
import pt.ulisboa.tecnico.softeng.activity.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.TaxInterface
import pt.ulisboa.tecnico.softeng.activity.services.remote.dataobjects.RestActivityBookingData

class ActivityInterfaceReserveActivityMethodSpockTest extends SpockRollbackTestAbstractClass{
    def IBAN = "IBAN"
    def NIF = "123456789"
    def INVOICE_REFERENCE = "invoiceReference"
    def PAYMENT_CONFIMATION = "paymentConfirmation"
    def MIN_AGE = 18
    def MAX_AGE = 50
    def CAPACITY = 30

    def bankInterface
    def taxInterface
    def activity

    ActivityProvider provider1
    ActivityProvider provider2
    RestActivityBookingData bookingData
    def activityBookingData

    @Override
    def populate4Test(){
        bankInterface=Mock(BankInterface)
        taxInterface=Mock(TaxInterface)

        def processor= new Processor(bankInterface,taxInterface)
        provider1 = new ActivityProvider("XtremX", "Adventure++", "NIF", IBAN, processor)
        provider2 = new ActivityProvider("Walker", "Sky", "NIF2", IBAN)


    }

    def 'reserveActivity'(){
        given:
        bankInterface.processPayment(_) >> PAYMENT_CONFIMATION

        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        activity = new Activity(provider1, "XtremX", MIN_AGE, MAX_AGE, CAPACITY)
        def offer = new ActivityOffer(activity, new LocalDate(2018, 02, 19), new LocalDate(2018, 12, 20), 30)
        activityBookingData = new RestActivityBookingData()
        activityBookingData.setAge(20)
        activityBookingData.setBegin(LocalDate.parse("2018-02-19"))
        activityBookingData.setEnd(LocalDate.parse("2018-12-20"))
        activityBookingData.setIban(IBAN)
        activityBookingData.setNif(NIF)


        when:

        bookingData = ActivityInterface.reserveActivity(activityBookingData)

        then:

        bookingData != null
        bookingData.getReference().startsWith("XtremX")

    }

    def 'reserveActivityNoOption'(){
        when:
        def activityBookingData = new RestActivityBookingData()
        activityBookingData.setAge(20)
        activityBookingData.setBegin(new LocalDate(2018, 02, 19))
        activityBookingData.setEnd(new LocalDate(2018, 12, 20))
        activityBookingData.setIban(IBAN)
        activityBookingData.setNif(NIF)

        then:
        ActivityInterface.reserveActivity(activityBookingData) >> { throw new ActivityException()}
    }

}

