package client;

import common.commands.*;
import common.network.Request;
import common.network.Response;
import client.utils.InputValidator;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkClient {
    private final String host;
    private final int port;
    private SocketChannel channel;
    private InputValidator validator;
    private CommandReader commandReader;
    private boolean running;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int RECONNECT_DELAY_MS = 2000;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.validator = new InputValidator();
        this.commandReader = new CommandReader(validator);
        this.running = true;
    }

    public void start() {
        System.out.println("Подключено к серверу " + host + ":" + port);
        System.out.println("Введите 'help' для списка команд\n");

        while (running) {
            System.out.print("> ");
            String input = validator.readLine(null);
            if (input == null || input.trim().isEmpty()) continue;

            if (input.trim().equalsIgnoreCase("exit")) {
                running = false;
                System.out.println("До свидания!");
                break;
            }

            boolean success = executeWithReconnect(input.trim());
            if (!success && running) {
                System.out.println("Команда не выполнена. Сервер недоступен.");
            }
        }

        disconnect();
    }

    private boolean executeWithReconnect(String input) {
        int attempts = 0;
        while (attempts < MAX_RECONNECT_ATTEMPTS && running) {
            try {
                if (channel == null || !channel.isOpen() || !channel.isConnected()) {
                    connect();
                }

                processCommand(input);
                return true;

            } catch (SocketTimeoutException e) {
                System.out.println("Сервер не отвечает. Попытка " + (attempts + 1) + "/" + MAX_RECONNECT_ATTEMPTS);
                attempts++;
                if (attempts < MAX_RECONNECT_ATTEMPTS) {
                    try {
                        Thread.sleep(RECONNECT_DELAY_MS);
                    } catch (InterruptedException ignored) {}
                }

            } catch (IOException e) {
                System.out.println("Соединение потеряно. Переподключение... Попытка " + (attempts + 1) + "/" + MAX_RECONNECT_ATTEMPTS);
                attempts++;
                disconnect();
                if (attempts < MAX_RECONNECT_ATTEMPTS) {
                    try {
                        Thread.sleep(RECONNECT_DELAY_MS);
                    } catch (InterruptedException ignored) {}
                }

            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка протокола: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    private void connect() throws IOException {
        disconnect();
        channel = SocketChannel.open();
        channel.socket().connect(new InetSocketAddress(host, port), 3000);
        channel.configureBlocking(true);
        channel.socket().setSoTimeout(5000);
        System.out.println("Переподключено к серверу");
    }

    private void disconnect() {
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (IOException e) {
            // ignore
        }
        channel = null;
    }

    private void processCommand(String input) throws IOException, ClassNotFoundException {
        Command command = commandReader.readCommand(input);
        if (command == null) return;

        Request request = new Request(command);
        byte[] data = serialize(request);

        // Отправляем длину + данные
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(data.length);
        lengthBuffer.flip();
        channel.write(lengthBuffer);

        ByteBuffer dataBuffer = ByteBuffer.wrap(data);
        channel.write(dataBuffer);

        ByteBuffer responseLengthBuffer = ByteBuffer.allocate(4);
        int bytesRead = 0;
        while (responseLengthBuffer.hasRemaining() && bytesRead != -1) {
            bytesRead = channel.read(responseLengthBuffer);
            if (bytesRead == -1) throw new EOFException("Сервер закрыл соединение");
        }
        responseLengthBuffer.flip();
        int responseLength = responseLengthBuffer.getInt();

        if (responseLength <= 0 || responseLength > 10 * 1024 * 1024) {
            throw new IOException("Неверная длина ответа: " + responseLength);
        }

        ByteBuffer responseBuffer = ByteBuffer.allocate(responseLength);
        while (responseBuffer.hasRemaining() && bytesRead != -1) {
            bytesRead = channel.read(responseBuffer);
            if (bytesRead == -1) throw new EOFException("Сервер закрыл соединение");
        }

        Response response = deserialize(responseBuffer.array());

        if (response.isSuccess()) {
            if (response.getData() != null) {
                System.out.println(response.getData());
            } else if (!response.getMessage().isEmpty()) {
                System.out.println(response.getMessage());
            }
        } else {
            System.err.println("Ошибка: " + response.getMessage());
        }
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }

    private Response deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Response) ois.readObject();
        }
    }
}
