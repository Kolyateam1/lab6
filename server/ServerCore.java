package server;

import common.network.Request;
import common.network.Response;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCore {
    private final int port;
    private CollectionManager collectionManager;
    private RequestHandler requestHandler;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    // Храним недополученные данные для каждого клиента
    private final Map<SocketChannel, ByteArrayOutputStream> pendingData = new ConcurrentHashMap<>();
    private final Map<SocketChannel, Integer> expectedLength = new ConcurrentHashMap<>();

    public ServerCore(int port) {
        this.port = port;
        this.collectionManager = new CollectionManager();
        this.requestHandler = new RequestHandler(collectionManager);
    }

    public void start() {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            ServerLogger.info("Сервер запущен на порту " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                ServerLogger.info("Сервер завершает работу, сохранение коллекции...");
                collectionManager.shutdown();
            }));

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        acceptConnection(key);
                    } else if (key.isReadable()) {
                        readRequest(key);
                    }
                }
            }
        } catch (IOException e) {
            ServerLogger.severe("Ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        pendingData.put(client, new ByteArrayOutputStream());
        ServerLogger.info("Новое подключение: " + client.getRemoteAddress());
    }

    private void readRequest(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        ByteArrayOutputStream baos = pendingData.get(client);

        try {
            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {
                closeConnection(client);
                return;
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            baos.write(data);

            // Пытаемся обработать данные
            byte[] fullData = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(fullData);

            while (bais.available() > 0) {
                // Сначала читаем длину сообщения (4 байта)
                Integer length = expectedLength.get(client);
                if (length == null) {
                    if (bais.available() < 4) break;
                    byte[] lenBytes = new byte[4];
                    bais.read(lenBytes);
                    length = ByteBuffer.wrap(lenBytes).getInt();
                    expectedLength.put(client, length);
                }

                // Потом читаем само сообщение
                if (bais.available() < length) break;
                byte[] msgBytes = new byte[length];
                bais.read(msgBytes);

                // Десериализуем запрос
                Request request = deserialize(msgBytes);
                if (request != null) {
                    ServerLogger.info("Получена команда: " + request.getCommand().getName());
                    Response response = requestHandler.handle(request);
                    byte[] responseData = serialize(response);
                    sendResponse(client, responseData);
                }

                // Очищаем ожидаемую длину для следующего сообщения
                expectedLength.remove(client);
            }

            // Сохраняем остаток для следующего раза
            byte[] remaining = new byte[bais.available()];
            bais.read(remaining);
            baos.reset();
            baos.write(remaining);

        } catch (IOException e) {
            ServerLogger.warning("Ошибка чтения: " + e.getMessage());
            closeConnection(client);
        }
    }

    private void sendResponse(SocketChannel client, byte[] data) {
        try {
            // Отправляем длину + данные
            ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
            lengthBuffer.putInt(data.length);
            lengthBuffer.flip();
            client.write(lengthBuffer);

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            client.write(dataBuffer);

            ServerLogger.info("Отправлен ответ клиенту");
        } catch (IOException e) {
            ServerLogger.warning("Ошибка отправки: " + e.getMessage());
            closeConnection(client);
        }
    }

    private void closeConnection(SocketChannel client) {
        try {
            pendingData.remove(client);
            expectedLength.remove(client);
            client.close();
            ServerLogger.info("Клиент отключился");
        } catch (IOException e) {
            ServerLogger.warning("Ошибка закрытия соединения");
        }
    }

    private byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            ServerLogger.severe("Ошибка сериализации: " + e.getMessage());
            return new byte[0];
        }
    }

    private Request deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Request) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            ServerLogger.warning("Ошибка десериализации: " + e.getMessage());
            return null;
        }
    }
}