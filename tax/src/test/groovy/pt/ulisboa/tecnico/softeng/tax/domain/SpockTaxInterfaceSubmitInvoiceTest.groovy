package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.tax.exception.TaxException
import pt.ulisboa.tecnico.softeng.tax.services.local.TaxInterface
import pt.ulisboa.tecnico.softeng.tax.services.remote.dataobjects.RestInvoiceData
import spock.lang.Shared
import spock.lang.Unroll

class SpockTaxInterfaceSubmitInvoiceTest extends SpockRollbackTestAbstractClass {

    @Shared def REFERENCE = '123456789'
    @Shared def SELLER_NIF = '123456789'
    @Shared def BUYER_NIF = '987654321'
    @Shared def FOOD = 'FOOD'
    @Shared def VALUE = 160
    def TAX = 16
    @Shared def date = LocalDate.parse('2018-10-12')
    @Shared def time = DateTimeFormat.forPattern('yyyy-MM-DD HH:mm:ss').parseDateTime('2018-02-13 10:10:00')

    private IRS irs

    @Override
    def populate4Test() {
        irs = IRS.getIRSInstance()
        new Seller(irs, SELLER_NIF, 'José Vendido', 'Somewhere')
        new Buyer(irs, BUYER_NIF, 'Manuel Comprado', 'Anywhere')
        new ItemType(irs, FOOD, TAX)

    }

    def 'success creating new invoice'() {
        given: 'a invoice data with the following properties'
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date, time)
        def invoiceReference = TaxInterface.submitInvoice(invoiceData)

        when: 'an invoice is get from the irs given the invoice reference'
        def invoice = irs.getTaxPayerByNIF(SELLER_NIF).getInvoiceByReference(invoiceReference)

        then: "the following properties must be met"
        invoice.getReference() == invoiceReference
        invoice.getSeller().getNif() == SELLER_NIF
        invoice.getBuyer().getNif() == BUYER_NIF
        invoice.getItemType().getName() == FOOD
        invoice.getValue() == VALUE
        invoice.getDate() == date
    }

    def 'Submit de same invoice twice'() {
        given: 'a new invoice is submitted'
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF, FOOD, VALUE, date, time)
        def invoiceReference = TaxInterface.submitInvoice(invoiceData)

        when: 'a second invoice with the same data is submitted'
        def secondInvoiceReference = TaxInterface.submitInvoice(invoiceData)

        then: 'the invoice references must be equal'
        invoiceReference == secondInvoiceReference
    }

    def 'invoice with data from 1970'() {
        when: 'the invoice data from further than 1970'
        def invoiceData = new RestInvoiceData(REFERENCE, SELLER_NIF, BUYER_NIF,FOOD, VALUE,
                new LocalDate(1970, 01, 01), new DateTime(1970, 01, 01, 10,10))
        then: 'the new invoice is submitted successfully'
        TaxInterface.submitInvoice(invoiceData)
    }

    @Unroll('RestInvoiceData: #ref | #snif | #bnif | #food | #val | #ds | #ts ')
    def 'Invalid values for creating an invoice'() {
        when: "an invoice data without a seller NIF"
        def invoiceData = new RestInvoiceData(ref, snif, bnif, food, val, ds, ts)
        TaxInterface.submitInvoice(invoiceData)

        then: "a TaxException is expected"
        thrown(TaxException)

        where: "the following values on the different variables"
        ref       | snif       | bnif      | food | val    | ds   | ts
        REFERENCE | null       | BUYER_NIF | FOOD | VALUE  | date | time
        REFERENCE | ""         | BUYER_NIF | FOOD | VALUE  | date | time
        REFERENCE | SELLER_NIF | null      | FOOD | VALUE  | date | time
        REFERENCE | SELLER_NIF | ""        | FOOD | VALUE  | date | time
        REFERENCE | SELLER_NIF | BUYER_NIF | null | VALUE  | date | time
        REFERENCE | SELLER_NIF | BUYER_NIF | ""   | VALUE  | date | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | 0.0d   | date | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | -23.7d | date | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | null | time
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | date | null
        REFERENCE | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | new LocalDate(1069,12,31) | new DateTime(1969, 12, 31, 10, 10)
        null      | SELLER_NIF | BUYER_NIF | FOOD | VALUE  | new LocalDate(1970, 01, 01) | new DateTime(1970, 01, 01, 10, 10)
    }
}
