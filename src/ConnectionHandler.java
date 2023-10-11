import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Класс представляет собой настройку соединения обработчика HTTP запросов.
 * @author mrGreenNV
 */
public class ConnectionHandler {

    /** Заголовок HTTP ответа от сервера. */
    private static final String HTTP_HEADERS = """
            HTTP/1.1 200 OK
            Date: Mon, 18 Sep 2023 14:08:55 +0200
            HttpServer: Simple Webserver
            Content-Length: 180
            Content-Type: text/html
            """;

    /** Тело HTTP ответа от сервера. */
    private static final String HTTP_BODY = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <meta charset="UTF-8">
            <title>Simple Http Server</title>
            </head>
            <body>
            <h1>Hi!</h1>
            <p>This is a simple line in html.</p>
            </body>
            </html>
            """;

    private final Socket socket;

    /**
     * Создает объект ConnectionHandler и инициализирует Socket.
     * @param socket Переданный сокет.
     */
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        handle();
    }

    /**
     * Обрабатывает сетевое соединение, которое работает с сокетом для чтения входящих данных и отправки ответов.
     * @throws RuntimeException Выбрасывает, если возникает ошибка при чтении или записи данных.
     */
    public void handle() {
        try {

            // Создание потока для чтения данных.
            var inputStreamReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII)
            );

            // Создание потока для записи данных.
            var outputStreamWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII)
            );

            parseRequest(inputStreamReader);
            writeResponse(outputStreamWriter);

        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    /**
     * Разбирает запрос из потока чтения данных и выводит данные в консоль.
     * @param inputStreamReader Поток для чтения данных.
     * @throws IOException Выбрасывает, если возникает ошибка при чтении данных.
     */
    private void parseRequest(BufferedReader inputStreamReader) throws IOException {
        var request = inputStreamReader.readLine();

        while (request != null && !request.isEmpty()) {
            System.out.println(request);
            request = inputStreamReader.readLine();
        }
    }

    /**
     * Формирует ответ в поток записи данных.
     * @param outputStreamWriter Поток для записи данных.
     * @throws IOException Выбрасывает, если возникает ошибка при записи данных.
     */
    private void writeResponse(BufferedWriter outputStreamWriter) throws IOException {
        outputStreamWriter.write(HTTP_HEADERS);
        outputStreamWriter.newLine();
        outputStreamWriter.write(HTTP_BODY);
        outputStreamWriter.newLine();
        outputStreamWriter.flush();
    }

}
