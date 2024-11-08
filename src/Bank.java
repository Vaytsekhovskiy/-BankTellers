import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Bank {
    private final static ArrayBlockingQueue<Client> clients = new ArrayBlockingQueue<>(40);
    private static WorkingDay workingDay;
    private static Integer dailyRevenue;
    private static Integer dailyClientsServed ;
    public static void main(String[] args) {
        long startWorking = System.currentTimeMillis();
        for (int i = 1 ; i < 10 ; i++) {
            dailyRevenue = 0;
            dailyClientsServed = 0;
            clients.clear();
            workingDay = new WorkingDay();
            workingDay.start();
            clients.clear();
            GetClientGenerator().start();
            System.out.println("Количество кассиров, которые выйдут на работу: "+ i);
            Cashier[] cashiers = new Cashier[i];
            System.out.println("Начало рабочего дня");
            for (int j = 0; j < i; j++) {
                cashiers[j] = new Cashier();
                cashiers[j].setName("Кассир №" + (j + 1));
                System.out.println(cashiers[j].getName() + " Вышел на работу");
                cashiers[j].start();
            }
            Arrays.stream(cashiers).forEach(cashier -> {
                try {
                    cashier.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            System.out.println("------------------------------------------------------------");
            System.out.printf("Суммарная выручка: %d, Суммарная прибыль: %d, Клиентов обслужено: %d \n",
                    dailyRevenue, dailyRevenue - i * Cashier.dailySalary, dailyClientsServed);
            System.out.println("------------------------------------------------------------");
        }
        System.out.println("Конец программы: " + (System.currentTimeMillis() - startWorking));
    }

    private static Thread GetClientGenerator() {
        AtomicInteger order = new AtomicInteger(1);
        Thread thread = new Thread(() -> {
            while (workingDay.isAlive()) {
                ServiceType serviceType = switch ((int) (Math.random() * 3) + 1) {
                    case 1 -> ServiceType.MAKING_LOAN;
                    case 2 -> ServiceType.CREDIT_CARD;
                    case 3 -> ServiceType.CURRENCY_EXCHANGE;
                    default -> throw new IllegalStateException("Unexpected value");
                };
                clients.offer(new Client(order.getAndIncrement(), serviceType));
                try {
                    Thread.sleep((int) (Math.random() *
                            WorkingDay.getHalfAnHourDurationToMills()) + 200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return thread;
    }

    static class Cashier extends Thread {
        boolean isRested;
        static int dailySalary = 3000;
        @Override
        public void run() {
            while (workingDay.isAlive()) {
                if (!isRested && workingDay.getCurrentTime().isAfter(LocalTime.of(12,0))) {
                    isRested = true;
                    System.out.println(this.getName() + " Ушёл на обед на 1 час");
                    try {
                        Thread.sleep(2L * WorkingDay.getHalfAnHourDurationToMills());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Client client;
                try {
                    client = clients.poll(5, TimeUnit.SECONDS);
                    System.out.printf("%s Обслуживает клиента №%d. Тип услуги: %s\n",
                            this.getName(), client.order(), client.serviceType().name());
                    try {
                        Thread.sleep(client.serviceType().getDuration());
                        dailyRevenue += client.serviceType().getPrice();
                        dailyClientsServed++;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(this.getName() + " Обслужил клиента №" + client.order());
                } catch (Throwable _) {
                }
            }
        }
    }
}

