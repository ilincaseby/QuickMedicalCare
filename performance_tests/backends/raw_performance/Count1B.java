public class Count1B {
    public static void main(String[] args) {
        int []a = new int[3000];
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000000; ++i) {
            int index = i % 3000;
            a[index] = a[0] + 1;
        }

        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        System.out.printf("Execution time: %.8f seconds%n", durationInSeconds);
    }
}