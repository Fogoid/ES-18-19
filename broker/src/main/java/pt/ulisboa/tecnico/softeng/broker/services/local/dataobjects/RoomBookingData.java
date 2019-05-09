package pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.broker.domain.Adventure;
import pt.ulisboa.tecnico.softeng.broker.services.remote.dataobjects.RestRoomBookingData;

public class RoomBookingData {
    private String reference;
    private String cancellation;
    private String hotelName;
    private String hotelCode;
    private String roomNumber;
    private String bookRoom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate arrival;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate departure;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate cancellationDate;
    private long price;
    private String paymentReference;
    private String invoiceReference;
    private String buyerNif;
    private String buyerIban;
    private String adventureId;

    public RoomBookingData() {
    }

    public RoomBookingData(RestRoomBookingData roomBookingData) {
        this.reference = roomBookingData.getReference();
        this.hotelName = roomBookingData.getHotelName();
        this.hotelCode = roomBookingData.getHotelCode();
        this.roomNumber = roomBookingData.getRoomNumber();
        this.arrival = roomBookingData.getArrival();
        this.departure = roomBookingData.getDeparture();
        this.bookRoom = roomBookingData.getRoomType();
        this.price = roomBookingData.getPrice();
        this.paymentReference = roomBookingData.getPaymentReference();
        this.invoiceReference = roomBookingData.getInvoiceReference();
        this.buyerNif = roomBookingData.getBuyerNif();
        this.buyerIban = roomBookingData.getBuyerIban();
        this.adventureId = roomBookingData.getAdventureId();
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCancellation() {
        return cancellation;
    }

    public void setCancellation(String cancellation) {
        this.cancellation = cancellation;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelCode() {
        return hotelCode;
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getBookRoom() {
        return bookRoom;
    }

    public void setBookRoom(String bookRoom) {
        this.bookRoom = bookRoom;
    }

    public LocalDate getArrival() {
        return arrival;
    }

    public void setArrival(LocalDate arrival) {
        this.arrival = arrival;
    }

    public LocalDate getDeparture() {
        return departure;
    }

    public void setDeparture(LocalDate departure) {
        this.departure = departure;
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public double getPriceDouble() {
        return Double.valueOf(price) / Adventure.SCALE;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getInvoiceReference() {
        return invoiceReference;
    }

    public void setInvoiceReference(String invoiceReference) {
        this.invoiceReference = invoiceReference;
    }

    public String getBuyerNif() {
        return buyerNif;
    }

    public void setBuyerNif(String buyerNif) {
        this.buyerNif = buyerNif;
    }

    public String getBuyerIban() {
        return buyerIban;
    }

    public void setBuyerIban(String buyerIban) {
        this.buyerIban = buyerIban;
    }

    public String getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(String adventureId) {
        this.adventureId = adventureId;
    }

    public void setCancellationData(RestRoomBookingData data) {
        this.cancellation = data.getCancellation();
        this.cancellationDate = data.getCancellationDate();
    }
}
