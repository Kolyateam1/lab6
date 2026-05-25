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

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.validator = new InputValidator();
        this.commandReader = new CommandReader(validator);
        this.running = true;
    }

    public void start() {
        try {
            connect();
            System.out.println("Подключено к серверу " + host + ":" + port);
            System.out.println("Введите 'help' для списка команд\n");

            while (running) {
                System.out.print("> ");
                String input = validator.readLine(null);
                if (input == null || input.trim().isEmpty()) continue;

                processCommand(input.trim());
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private void connect() throws IOException {
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(host, port));
        channel.configureBlocking(true); // клиент может быть блокирующим
    }

    private void disconnect() {
        try {
            if (channel != null) channel.close();
        } catch (IOException e) {}
    }

    private void processCommand(String input) {
        try {
            Command command = commandReader.readCommand(input);
            if (command == null) return;

            if (command instanceof ExitCommand) {
                running = false;
                System.out.println("До свидания!");
                return;
            }

            // Сериализуем запрос в байты
            Request request = new Request(command);
            byte[] data = serialize(request);

            // Отправляем длину + данные
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            lengthBuffer.putInt(data.length);
            lengthBuffer.flip();
            channel.write(lengthBuffer);

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            channel.write(dataBuffer);

            // Читаем ответ
            ByteBuffer responseLengthBuffer = ByteBuffer.allocate(4);
            while (responseLengthBuffer.hasRemaining()) {
                channel.read(responseLengthBuffer);
            }
            responseLengthBuffer.flip();
            int responseLength = responseLengthBuffer.getInt();

            ByteBuffer responseBuffer = ByteBuffer.allocate(responseLength);
            while (responseBuffer.hasRemaining()) {
                channel.read(responseBuffer);
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

        } catch (IOException e) {
            System.err.println("Ошибка соединения: " + e.getMessage());
            running = false;
        }
    }

    private byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            System.err.println("Ошибка сериализации: " + e.getMessage());
            return new byte[0];
        }
    }

    private Response deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Response) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка десериализации: " + e.getMessage());
            return new Response(false, "Ошибка десериализации");
        }
    }
}