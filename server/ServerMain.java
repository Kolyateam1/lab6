package server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Неверный порт, используется 8080");
            }
        }

        ServerCore server = new ServerCore(port);
        server.start();
    }
}