package pt.ulisboa.tecnico.softeng.broker.domain;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import pt.ulisboa.tecnico.softeng.broker.services.remote.HotelInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.CarInterface
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.HotelException;
import pt.ulisboa.tecnico.softeng.broker.services.remote.exception.RemoteAccessException;
import spock.lang.Unroll

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Ignore;

class SpockBulkRoomBookingProcessBookingMethodTest extends SpockRollbackTestAbstractClass {
    BulkRoomBooking bulk
    def broker
    def result
    def hotelInterface
    def carInterface



    @Override
    def "populate4Test"() {

        hotelInterface = Mock(HotelInterface)
        carInterface = Mock(CarInterface)

        broker = new Broker("BR01", "eXtremeADVENTURE", BROKER_NIF_AS_SELLER, NIF_AS_BUYER, BROKER_IBAN, null, null, carInterface, hotelInterface, null)
        bulk = new BulkRoomBooking(broker, NUMBER_OF_BULK, BEGIN, END, NIF_AS_BUYER, IBAN_BUYER)
    }

    @Unroll
    def 'success'() {

        when:
        hotelInterface.bulkBooking(*_) >> new HashSet<>(Arrays.asList("ref1", "ref2"))

        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2

        /*new Expectations() {
            {

                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());*/
    }



    @Unroll
    def 'successTwice'() {

        when:
        hotelInterface.bulkBooking(*_) >> new HashSet<>(Arrays.asList("ref1", "ref2"))
        hotelInterface.bulkBooking(*_) >> new HashSet<>(Arrays.asList("ref3", "ref4"))

        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 2
        /*
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
                this.result = new HashSet<>(Arrays.asList("ref3", "ref4"));
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());*/
    }


    @Unroll
    def 'HotelException'() {

        when:


        bulk.processBooking()
        bulk.processBooking()

        then:
        1*hotelInterface.bulkBooking(*_) >> {throw new HotelException()}
        1*hotelInterface.bulkBooking(*_) >> {new HashSet<>(Arrays.asList("ref1", "ref2"))}

        bulk.getReferences().size() == 2
        !bulk.getCancelled()

/*

        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HotelException();
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());*/
    }

    @Unroll
    def 'maxHotelException'() {

        given:
        hotelInterface.bulkBooking(*_) >> {throw new HotelException()}

        when:

        bulk.processBooking()
        bulk.processBooking()
        bulk.processBooking()

        then:
        bulk.getReferences().size() == 0
        bulk.getCancelled()
        /*

        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HotelException();
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(0, this.bulk.getReferences().size());
        assertTrue(this.bulk.getCancelled());*/
    }


    @Unroll
    def "maxMinusOneHotelException"() {

        given:
        hotelInterface.bulkBooking(*_) >> new HashSet<>(Arrays.asList("ref3", "ref4"))

        when:
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        then:
        2*hotelInterface.bulkBooking(*_) >> {throw new HotelException()}

        and:
        1*hotelInterface.bulkBooking(*_) >> {new HashSet<>(Arrays.asList("ref1", "ref2"))}

        and:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()
        /*
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new HotelException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
        */
    }

    def 'hotelExceptionValueIsResetByRemoteException'() {
        given:
        hotelInterface.bulkBooking(*_) >> new HashSet<>(Arrays.asList("ref3", "ref4"))

        when:
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()
        this.bulk.processBooking()

        then:
        2*hotelInterface.bulkBooking(*_) >> {throw new HotelException()}

        and:
        1*hotelInterface.bulkBooking(*_) >> {throw new RemoteAccessException()}

        and:
        2*hotelInterface.bulkBooking(*_) >> {throw new HotelException()}

        and:
        1*hotelInterface.bulkBooking(*_) >> {new HashSet<>(Arrays.asList("ref1", "ref2"))}

        and:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()

        /*
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new HotelException();
                        } else if (this.i == BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new RemoteAccessException();
                        } else if (this.i < 2 * BulkRoomBooking.MAX_HOTEL_EXCEPTIONS - 1) {
                            throw new HotelException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
            }
        };

        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());*/
    }

    @Unroll
    def 'oneRemoteException'() {

        given:

        when:

        bulk.processBooking()
        bulk.processBooking()

        then:
        1*hotelInterface.bulkBooking(*_) >> {throw new RemoteAccessException()}
        1*hotelInterface.bulkBooking(*_) >> {new HashSet<>(Arrays.asList("ref1", "ref2"))}

        bulk.getReferences().size() == 2
        !bulk.getCancelled()

        /*
            new Expectations() {
                {
                    HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                            BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                    this.result = new RemoteAccessException();
                    this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
                }
            };

            this.bulk.processBooking();
            this.bulk.processBooking();

            assertEquals(2, this.bulk.getReferences().size());
            assertFalse(this.bulk.getCancelled());*/
    }


    def 'maxRemoteException'() {

        given:
        hotelInterface.bulkBooking(*_) >> {throw new RemoteAccessException()}

        when:
        for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++)
            this.bulk.processBooking()
        this.bulk.processBooking()


        then:
        bulk.getReferences().size() == 0
        bulk.getCancelled()


    /*
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new RemoteAccessException();
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();

        assertEquals(0, this.bulk.getReferences().size());
        assertTrue(this.bulk.getCancelled());*/
    }


    def 'maxMinusOneRemoteException'() {


        when:
        for (def i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++)
            this.bulk.processBooking();
        this.bulk.processBooking()

        then:
        9*hotelInterface.bulkBooking(*_) >> {throw new RemoteAccessException()}

        and:
        1*hotelInterface.bulkBooking(*_) >> {new HashSet<>(Arrays.asList("ref1", "ref2"))}

        and:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()

    /*
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
                this.result = new RemoteAccessException();
                this.times = BulkRoomBooking.MAX_REMOTE_ERRORS - 1;

                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new HashSet<>(Arrays.asList("ref1", "ref2"));
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());*/
    }


    def 'remoteExceptionValueIsResetByHotelException'() {

        when:
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++)
            this.bulk.processBooking()
        this.bulk.processBooking()
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++)
            this.bulk.processBooking()
        this.bulk.processBooking()

        then:
        9*hotelInterface.bulkBooking(*_) >> {throw new RemoteAccessException()}

        and:
        1*hotelInterface.bulkBooking(*_) >> {throw new HotelException()}

        and:
        9*hotelInterface.bulkBooking(*_) >> {throw new RemoteAccessException()}

        and:
        1*hotelInterface.bulkBooking(*_) >> {new HashSet<>(Arrays.asList("ref1", "ref2"))}

        and:
        bulk.getReferences().size() == 2
        !bulk.getCancelled()

    }/*
        new Expectations() {
            {
                HotelInterface.bulkBooking(NUMBER_OF_BULK, ARRIVAL, DEPARTURE, NIF_AS_BUYER, IBAN_BUYER,
                        BulkRoomBookingProcessBookingMethodTest.this.bulk.getId());
                this.result = new Delegate() {
                    int i = 0;

                    Set<String> delegate() {
                        this.i++;
                        if (this.i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else if (this.i == BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new HotelException();
                        } else if (this.i < 2 * BulkRoomBooking.MAX_REMOTE_ERRORS - 1) {
                            throw new RemoteAccessException();
                        } else {
                            return new HashSet<>(Arrays.asList("ref1", "ref2"));
                        }
                    }
                };
            }
        };

        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();
        for (int i = 0; i < BulkRoomBooking.MAX_REMOTE_ERRORS - 1; i++) {
            this.bulk.processBooking();
        }
        this.bulk.processBooking();

        assertEquals(2, this.bulk.getReferences().size());
        assertFalse(this.bulk.getCancelled());
    }*/

}