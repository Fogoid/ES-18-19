package pt.ulisboa.tecnico.softeng.activity.services.local.dataobjects;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import pt.ulisboa.tecnico.softeng.activity.domain.Activity;
import pt.ulisboa.tecnico.softeng.activity.domain.ActivityOffer;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityOfferData {
    private String externalId;
    private String codeProvider;
    private String codeActivity;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate begin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;
    private Integer capacity;
    private Double amount;
    private List<ActivityBookingData> reservations;
    private int availablePlaces;

    public ActivityOfferData() {
    }

    public ActivityOfferData(ActivityOffer offer) {
        this.externalId = offer.getExternalId();
        this.codeProvider = offer.getActivity().getActivityProvider().getCode();
        this.codeActivity = offer.getActivity().getCode();
        this.name = offer.getActivity().getName();
        this.begin = offer.getBegin();
        this.end = offer.getEnd();
        this.capacity = offer.getCapacity();
        this.amount = new Double(offer.getAmount()) / Activity.SCALE;
        this.reservations = offer.getBookingSet().stream().map(b -> new ActivityBookingData(b))
                .collect(Collectors.toList());
        this.availablePlaces = this.capacity - (int) offer.getBookingSet().stream().filter(booking -> !booking.isCancelled()).count();
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCodeProvider() {
        return this.codeProvider;
    }

    public void setCodeProvider(String codeProvider) {
        this.codeProvider = codeProvider;
    }

    public String getCodeActivity() {
        return this.codeActivity;
    }

    public void setCodeActivity(String codeActivity) {
        this.codeActivity = codeActivity;
    }

    public LocalDate getBegin() {
        return this.begin;
    }

    public void setBegin(LocalDate begin) {
        this.begin = begin;
    }

    public LocalDate getEnd() {
        return this.end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<ActivityBookingData> getReservations() {
        return this.reservations;
    }

    public void setReservations(List<ActivityBookingData> reservations) {
        this.reservations = reservations;
    }

    public Double getAmount() {
        return this.amount;
    }

    public long getAmountLong() {
        return Math.round(getAmount() * Activity.SCALE);
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailablePlaces() {
        return this.availablePlaces;
    }

    public void setAvailablePlaces(int availablePlaces) {
        this.availablePlaces = availablePlaces;
    }
}
