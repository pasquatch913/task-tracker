package tracker.task.subscription;

public enum TaskPeriod {
    // TODO remove integer mapping and move logic to service level
    DAILY(1),
    WEEKLY(7),
    MONTHLY(30);

    private Integer days;

    TaskPeriod(Integer days) {
        this.days = days;
    }

    public Integer getDays() {
        return days;
    }
}
