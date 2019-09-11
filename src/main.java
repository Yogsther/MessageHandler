public class main {
    public static void main(String[] args) {
        Message testMessage = new Message("Hello world!");
                testMessage.update("Oh no");

        System.out.println(testMessage.toString());
    }
}
