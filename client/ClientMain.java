package client;

import client.NetworkClient;

public class ClientMain {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        NetworkClient client = new NetworkClient(host, port);
        client.start();
    }
}
