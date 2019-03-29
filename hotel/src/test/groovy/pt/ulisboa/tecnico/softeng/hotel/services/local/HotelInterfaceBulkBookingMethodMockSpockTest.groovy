package pt.ulisboa.tecnico.softeng.hotel.services.local

import org.joda.time.LocalDate
import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel
import pt.ulisboa.tecnico.softeng.hotel.domain.Processor
import pt.ulisboa.tecnico.softeng.hotel.domain.Room
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type
import pt.ulisboa.tecnico.softeng.hotel.domain.SpockRollbackTestAbstractClass
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException
import pt.ulisboa.tecnico.softeng.hotel.services.remote.BankInterface
import pt.ulisboa.tecnico.softeng.hotel.services.remote.TaxInterface
import spock.lang.Shared
import spock.lang.Unroll

import java.util.stream.Collectors

class HotelInterfaceBulkBookingMethodMockSpockTest extends SpockRollbackTestAbstractClass {
    @Shared def arrival = LocalDate.parse("2016-12-19")
    @Shared def departure = LocalDate.parse("2016-12-21")
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"
    def BULK_ID = "BULK_ID"
    def PAYMENT_CONFIRMATION = "paymentConfirmation"
    def INVOICE_REFERENCE = "invoiceReference"

    def hotelInterface
    def bankInterface
    def taxInterface

    private Hotel hotel

    def populate4Test() {
        bankInterface = Mock(BankInterface)
        taxInterface = Mock(TaxInterface)

        def processor = new Processor(bankInterface, taxInterface)
        this.hotel = new Hotel("XPTO123", "Paris", "NIF", "IBAN", 20.0, 30.0, processor)
        hotelInterface = new HotelInterface()

        new Room(this.hotel, "01", Type.DOUBLE)
        new Room(this.hotel, "02", Type.SINGLE)
        new Room(this.hotel, "03", Type.DOUBLE)
        new Room(this.hotel, "04", Type.SINGLE)

        this.hotel = new Hotel("XPTO124", "Paris", "NIF2", "IBAN2", 25.0, 35.0)
        new Room(this.hotel, "01", Type.DOUBLE)
        new Room(this.hotel, "02", Type.SINGLE)
        new Room(this.hotel, "03", Type.DOUBLE)
        new Room(this.hotel, "04", Type.SINGLE)
    }

    def "successful bulk bulking"() {
        given:
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION
        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when:
        def references = hotelInterface.bulkBooking(2, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        2 == references.size()
    }

    def "no rooms available"() {
        for (def hotel: FenixFramework.getDomainRoot().getHotelSet())
            hotel.delete()

        given:
        this.hotel = new Hotel("XPTO124", "Paris", "NIF", "IBAN", 27.0, 37.0)

        when:
        hotelInterface.bulkBooking(3, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        thrown(HotelException)
    }

    def "one number"() {
        when:
        def references = hotelInterface.bulkBooking(1, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        1 == references.size()
    }

    @Unroll("bulkBooking: #ardate, #depdate")
    def "null date"() {
        when:
        hotelInterface.bulkBooking(2, ardate, depdate, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        thrown(HotelException)

        where:
        ardate       | depdate
        null         | this.departure
        this.arrival | null
    }

    def "reserve all rooms"() {
        when:
        def references = hotelInterface.bulkBooking(8, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        8 == references.size()
    }

    def "reserve all rooms plus one"() {
        when:
        hotelInterface.bulkBooking(9, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        thrown(HotelException)

        and:
        8 == hotelInterface.getAvailableRooms(8, this.arrival, this.departure).size()
    }

    def "idempotent bulk booking"() {
        given:
        bankInterface.processPayment(_) >> PAYMENT_CONFIRMATION

        taxInterface.submitInvoice(_) >> INVOICE_REFERENCE

        when:
        def references = hotelInterface.bulkBooking(4, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        4 == references.size()

        when:
        def equalReferences = hotelInterface.bulkBooking(4, this.arrival, this.departure, this.NIF_BUYER, this.IBAN_BUYER, this.BULK_ID)

        then:
        4 == hotelInterface.getAvailableRooms(4, this.arrival, this.departure).size()
        and:
        references.stream().sorted().collect(Collectors.toList()) == equalReferences.stream().sorted().collect(Collectors.toList())
    }
}
