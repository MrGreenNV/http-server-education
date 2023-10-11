import java.io.IOException;
import java.net.ServerSocket;

/**
 * Класс представляет собой HTTP сервер.
 * @author mrGreenNV
 */
public class HttpServer {

    /** Порт для доступа к серверу. */
    private final int tcpPort;

    /**
     * Создает объект HttpServer и инициализирует переданный порт для доступа к серверу.
     * @param tcpPort Переданный порт.
     */
    public HttpServer(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    /**
     * Запускает сервер и прослушивает подключение, установленное к сокету.
     * @throws RuntimeException Выбрасывает, если возникает ошибка при чтении или записи данных.
     */
    public void startServer() {
        try (var serverSocket = new ServerSocket(this.tcpPort)) {
            System.out.println("Server accepting requests on port: " + tcpPort);

            while (true) {
                var acceptedSocked = serverSocket.accept();
                var connectionHandler = new ConnectionHandler(acceptedSocked);
                connectionHandler.handle();
            }

        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }
}
