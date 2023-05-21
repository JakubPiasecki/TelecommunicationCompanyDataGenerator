import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TelecommunicationCompanyDataGenerator {

    public static void main(String[] args) {
        int numberOfCustomers = 500;
        int numberOfCalls = 200_000;
        String fileName = "telecom_data.txt";

        generateTelecomData(fileName, numberOfCustomers, numberOfCalls);
        List<String> telecomData = readTelecomData(fileName);

        int N = 3;

        List<Map.Entry<Integer, Integer>> topCallingCustomers = getTopCallingCustomers(telecomData, N);
        System.out.println("Top " + N + " customers with the longest call durations as callers:");
        topCallingCustomers.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " seconds"));

        List<Map.Entry<Integer, Integer>> topReceivingCustomers = getTopReceivingCustomers(telecomData, N);
        System.out.println("Top " + N + " customers with the longest call durations as receivers:");
        topReceivingCustomers.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " seconds"));

        List<Map.Entry<Integer, Integer>> topCallingCustomersCount = getTopCallingCustomersCount(telecomData, N);
        System.out.println("Top " + N + " customers who made calls to the highest number of unique customers:");
        topCallingCustomersCount.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " unique customers"));

        List<Map.Entry<Integer, Integer>> topReceivingCustomersCount = getTopReceivingCustomersCount(telecomData, N);
        System.out.println("Top " + N + " customers who received calls from the highest number of unique customers:");
        topReceivingCustomersCount.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " unique customers"));

        List<Map.Entry<Integer, Integer>> topFrequentCallers = getTopFrequentCallers(telecomData, N);
        System.out.println("Top " + N + " customers who made the highest number of calls:");
        topFrequentCallers.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " calls"));

        List<Map.Entry<Integer, Integer>> topFrequentReceivers = getTopFrequentReceivers(telecomData, N);
        System.out.println("Top " + N + " customers who received the highest number of calls:");
        topFrequentReceivers.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " calls"));

        List<Map.Entry<Integer, Integer>> topInfrequentCallers = getTopInfrequentCallers(telecomData, N);
        System.out.println("Top " + N + " customers who made the fewest number of calls:");
        topInfrequentCallers.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " calls"));

        List<Map.Entry<Integer, Integer>> topInfrequentReceivers = getTopInfrequentReceivers(telecomData, N);
        System.out.println("Top " + N + " customers who received the fewest number of calls:");
        topInfrequentReceivers.forEach(entry -> System.out.println("Customer " + entry.getKey() + ": " + entry.getValue() + " calls"));

        int customerId = 42;
        CustomerInfo customerInfo = getCustomerInfo(telecomData, customerId);
        System.out.println("Customer information for customer " + customerId + ":");
        System.out.println("Total calls made: " + customerInfo.getCallsMade());
        System.out.println("Total calls received: " + customerInfo.getCallsReceived());
        System.out.println("Total call duration: " + customerInfo.getTotalCallDuration() + " seconds");
    }

    private static void generateTelecomData(String fileName, int numberOfCustomers, int numberOfCalls) {
        try {
            Random random = new Random();

            List<String> lines = new ArrayList<>();

            for (int i = 0; i < numberOfCalls; i++) {
                int callerId = random.nextInt(numberOfCustomers) + 1;
                int receiverId = random.nextInt(numberOfCustomers) + 1;

                double mean = 300;
                double stdDev = 60;

                int callDuration = (int) Math.round(random.nextGaussian() * stdDev + mean);

                if (callerId != receiverId && callDuration > 0) {
                    String line = callerId + " " + receiverId + " " + callDuration;
                    lines.add(line);
                } else {
                    i--;
                }
            }

            Files.write(Path.of(fileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readTelecomData(String fileName) {
        try {
            return Files.readAllLines(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private static List<Map.Entry<Integer, Integer>> getTopCallingCustomers(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> line.split(" "))
                .collect(Collectors.toMap(
                        parts -> Integer.parseInt(parts[0]),
                        parts -> Integer.parseInt(parts[2]),
                        Integer::sum))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopReceivingCustomers(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> line.split(" "))
                .collect(Collectors.toMap(
                        parts -> Integer.parseInt(parts[1]),
                        parts -> Integer.parseInt(parts[2]),
                        Integer::sum))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopCallingCustomersCount(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> line.split(" "))
                .collect(Collectors.groupingBy(parts -> Integer.parseInt(parts[0]), Collectors.mapping(parts -> Integer.parseInt(parts[1]), Collectors.collectingAndThen(Collectors.toSet(), Set::size))))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopReceivingCustomersCount(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> line.split(" "))
                .collect(Collectors.groupingBy(parts -> Integer.parseInt(parts[1]), Collectors.mapping(parts -> Integer.parseInt(parts[0]), Collectors.collectingAndThen(Collectors.toSet(), Set::size))))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopFrequentCallers(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> Integer.parseInt(line.split(" ")[0]))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopFrequentReceivers(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> Integer.parseInt(line.split(" ")[1]))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopInfrequentCallers(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> Integer.parseInt(line.split(" ")[0]))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(N)
                .collect(Collectors.toList());
    }

    private static List<Map.Entry<Integer, Integer>> getTopInfrequentReceivers(List<String> telecomData, int N) {
        return telecomData.stream()
                .map(line -> Integer.parseInt(line.split(" ")[1]))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(N)
                .collect(Collectors.toList());
    }


    private static CustomerInfo getCustomerInfo(List<String> telecomData, int customerId) {
        long callsMade = telecomData.stream()
                .map(line -> line.split(" ")[0])
                .filter(id -> id.equals(String.valueOf(customerId)))
                .count();

        long callsReceived = telecomData.stream()
                .map(line -> line.split(" ")[1])
                .filter(id -> id.equals(String.valueOf(customerId)))
                .count();

        int totalCallDuration = telecomData.stream()
                .filter(line -> {
                    String[] parts = line.split(" ");
                    int callerId = Integer.parseInt(parts[0]);
                    int receiverId = Integer.parseInt(parts[1]);
                    return callerId == customerId || receiverId == customerId;
                })
                .map(line -> line.split(" ")[2])
                .mapToInt(Integer::parseInt)
                .sum();

        return new CustomerInfo((int) callsMade, (int) callsReceived, totalCallDuration);
    }
}

class CustomerInfo {
    private int callsMade;
    private int callsReceived;
    private int totalCallDuration;

    public CustomerInfo(int callsMade, int callsReceived, int totalCallDuration) {
        this.callsMade = callsMade;
        this.callsReceived = callsReceived;
        this.totalCallDuration = totalCallDuration;
    }

    public int getCallsMade() {
        return callsMade;
    }

    public void setCallsMade(int callsMade) {
        this.callsMade = callsMade;
    }

    public int getCallsReceived() {
        return callsReceived;
    }

    public void setCallsReceived(int callsReceived) {
        this.callsReceived = callsReceived;
    }

    public int getTotalCallDuration() {
        return totalCallDuration;
    }

    public void setTotalCallDuration(int totalCallDuration) {
        this.totalCallDuration = totalCallDuration;
    }

    @Override
    public String toString() {
        return "CustomerInfo= " + callsMade + callsReceived + totalCallDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerInfo that = (CustomerInfo) o;
        return callsMade == that.callsMade && callsReceived == that.callsReceived && totalCallDuration == that.totalCallDuration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(callsMade, callsReceived, totalCallDuration);
    }
}
