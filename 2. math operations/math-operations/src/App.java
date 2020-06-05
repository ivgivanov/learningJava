public class App {
    public static void main(String[] args) throws Exception {
        int num1 = 4;
        int num2 = num1;

        int result1 = ++num1 * 2;
        System.out.println(result1);

        int result2 = num2++ * 2;
        System.out.println(result2);

        int a = 43521353;
        short b = (short) a;

        System.out.println(b);
    }
}
