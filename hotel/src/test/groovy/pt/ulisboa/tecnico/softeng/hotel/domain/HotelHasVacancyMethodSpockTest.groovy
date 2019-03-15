package pt.ulisboa.tecnico.softeng.hotel.domain

import org.joda.time.LocalDate
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

class HotelHasVacancyMethodSpockTest extends SpockRollbackTestAbstractClass {
    LocalDate arrival = new LocalDate(2016, 12, 19)
    LocalDate departure = new LocalDate(2016, 12, 21)
    Hotel hotel
    Room room
    def NIF_HOTEL = "123456700"
    def NIF_BUYER = "123456789"
    def IBAN_BUYER = "IBAN_BUYER"
    
    def populate4Test(){
        hotel = new Hotel("XPTO123", "Paris", NIF_HOTEL, "IBAN", 20.0, 30.0)
        room = new Room(hotel, "01", Room.Type.DOUBLE)
    }
    
    def 'hasVancacy'(){
        when:
        room = hotel.hasVacancy(Room.Type.DOUBLE, arrival, departure)
        
        then:
        room != null
        room.getNumber() == "01"
    }
    
    def 'noVancacy'(){
        when:
        room.reserve(Room.Type.DOUBLE, arrival, departure, NIF_BUYER, IBAN_BUYER)
        
        then:
        hotel.hasVacancy(Room.Type.DOUBLE, arrival, departure) == null
        
    }
    
    def 'noVacancyEmptyRoomSet'(){
        when:
        Hotel otherHotel = new Hotel("XPTO124", "Paris Germain", "NIF2", "IBAN", 25.0, 35.0)
        
        then:
        otherHotel.hasVacancy(Room.Type.DOUBLE, arrival, departure) == null
    }
    
    def 'nullType'(){
        when:
        hotel.hasVacancy(null, arrival, departure)
        
        then:
        thrown(HotelException)
    }
    
    def 'nullArrival'(){
        when:
        hotel.hasVacancy(Room.Type.DOUBLE, null, departure)
        
        then:
        thrown(HotelException)
    }
    
    def 'nullDeparture'(){
        when:
        hotel.hasVacancy(Room.Type.DOUBLE, arrival, null)

        then:
        thrown(HotelException)
    }

}


