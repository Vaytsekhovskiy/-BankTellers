import java.time.LocalTime;

public class WorkingDay extends Thread{
    // 30 минут симуляции = 1 секунда
    private static final int halfAnHourDurationToMills = 1000;
    private static LocalTime currentTime;
    @Override
    public void run() {
        currentTime = LocalTime.of(9,0);
        while(currentTime.isBefore(LocalTime.of(18,0))) {
            try {
                Thread.sleep(halfAnHourDurationToMills);
                currentTime = currentTime.plusMinutes(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println("Рабочий день закончен!");
    }
    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public static int getHalfAnHourDurationToMills() {
        return halfAnHourDurationToMills;
    }
}
