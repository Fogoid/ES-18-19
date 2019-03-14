package pt.ulisboa.tecnico.softeng.hotel.domain

import pt.ist.fenixframework.FenixFramework
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException

import spock.lang.Shared
import spock.lang.Unroll


class GHotelConstructorTest extends SpockRollbackTestAbstractClass {
    @Shared def IBAN= "IBAN"
    @Shared def NIF="NIF"

    @Shared def HOTEL_NAME="Londres"
    @Shared def HOTEL_CODE= "XPTO123"

    @Shared def PRICE_SINGLE=20.0
    @Shared def PRICE_DOUBLE=30.0

    @Override
    def populate4Test(){

    }

    def 'success'(){
        when:
        def hotel= new Hotel(HOTEL_CODE,HOTEL_NAME,NIF,IBAN,PRICE_SINGLE,PRICE_DOUBLE)

        then:
        hotel.getName()==HOTEL_NAME
        hotel.getCode().length()==Hotel.CODE_SIZE
        hotel.getRoomSet().size()==0
        FenixFramework.getDomainRoot().getHotelSet().size()==1
        PRICE_SINGLE==hotel.getPrice(Room.Type.SINGLE)
        PRICE_DOUBLE==hotel.getPrice(Room.Type.DOUBLE)

    }

    @Unroll("Hotel:#code,#hotel_name,#nif,#iban,#prive_single,#price_double")
    def 'expections_tests'(){
        when:
        new Hotel(code,hotel_name,nif,iban,prive_single,price_double)
        then:
        thrown(HotelException)

        where:
        code  |  hotel_name  |  nif  |  iban  |  prive_single  |  price_double
        null  |  HOTEL_NAME  |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        "  "  |  HOTEL_NAME  |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        ""    |  HOTEL_NAME  |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        HOTEL_CODE| null     |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        HOTEL_CODE| "   "    |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        HOTEL_CODE|    ""    |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        "123456"  |  HOTEL_NAME  |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
        "12345678"|  HOTEL_NAME  |  NIF  |  IBAN  | PRICE_SINGLE   | PRICE_DOUBLE
    }

    def 'nifNotUnique'(){
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)
        new Hotel(HOTEL_CODE + "_new", HOTEL_NAME + "_New", NIF, IBAN, PRICE_SINGLE, PRICE_DOUBLE)
        then:
        thrown(HotelException)
    }
    def 'negativePriceSingle'(){
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, -1.0, PRICE_DOUBLE)
        then:
        thrown(HotelException)
    }
    def 'negativePriceDouble'(){
        when:
        new Hotel(HOTEL_CODE, HOTEL_NAME, NIF, IBAN, PRICE_SINGLE, -1.0)
        then:
        thrown(HotelException)
    }
}