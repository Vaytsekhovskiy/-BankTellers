
public enum ServiceType {

    MAKING_LOAN(5_000, WorkingDay.getHalfAnHourDurationToMills() * 3),
    CREDIT_CARD(1000, (int) (WorkingDay.getHalfAnHourDurationToMills() * 2.5)),
    CURRENCY_EXCHANGE(100, (int) (WorkingDay.getHalfAnHourDurationToMills() * 1.3));

    private final int price;
    private final int duration;

    ServiceType(int price, int duration) {
        this.price = price;
        this.duration = duration;
    }
    public int getPrice() {
        return price;
    }
    public int getDuration() {
        return duration;
    }
}
