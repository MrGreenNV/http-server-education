import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Класс представляет собой настройку соединения обработчика HTTP запросов.
 * @author mrGreenNV
 */
public class ConnectionHandler {

    /** Заголовок HTTP ответа от сервера в формате HTML страницы. */
    private static final String HTTP_HEADERS_HTML = """
            HTTP/1.1 200 OK
            Date: Mon, 18 Sep 2023 14:08:55 +0200
            HttpServer: Simple Webserver
            Content-Length: 180
            Content-Type: text/html
            """;

    /** Заголовок HTTP ответа от сервера в виде вложения TXT файла. */
    private static final String HTTP_HEADERS_TEXT = """
            HTTP/1.1 200 OK
            Date: Mon, 18 Sep 2023 14:08:55 +0200
            HttpServer: Simple Webserver
            Content-Length: 38
            Content-Type: text/plain
            Content-Disposition: attachment; filename=file.txt
            """;

    /** Заголовок HTTP ответа от сервера в формате JSON. */
    private static final String HTTP_HEADERS_JSON = """
            HTTP/1.1 200 OK
            Date: Mon, 18 Sep 2023 14:08:55 +0200
            HttpServer: Simple Webserver
            Content-Length: 10
            Content-Type: application/json
            """;

    /** Тело HTTP ответа от сервера для отображения HTML страницы. */
    private static final String HTTP_BODY_HTML = """
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

    /** Тело HTTP ответа от сервера во вложенном TXT файле. */
    private static final String HTTP_BODY_TEXT = "Hi! This is a simple line in TXT file.";

    /** Сокет */
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

            switch (parseRequest(inputStreamReader)) {
                case "application/json" -> writeJSONResponse(outputStreamWriter);
                case "text/html" -> writeHTMLResponse(outputStreamWriter);
                default -> writeTEXTResponse(outputStreamWriter);
            }

        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    /**
     * Разбирает запрос из потока чтения данных и выводит данные в консоль.
     * @param inputStreamReader Поток для чтения данных.
     * @throws IOException Выбрасывает, если возникает ошибка при чтении данных.
     */
    private String parseRequest(BufferedReader inputStreamReader) throws IOException {
        String acceptHeader = "";
        var request = inputStreamReader.readLine();

        while (request != null && !request.isEmpty()) {
            if (request.contains("Accept:")) {
                acceptHeader = request.substring(7).trim();
            }
            System.out.println(request);
            request = inputStreamReader.readLine();
        }
        return acceptHeader;
    }

    /**
     * Вызывает метод формирования ответа в поток записи данных в виде HTML страницы.
     * @param outputStreamWriter Поток для записи данных.
     * @throws IOException Выбрасывает, если возникает ошибка при записи данных.
     */
    private void writeHTMLResponse(BufferedWriter outputStreamWriter) throws IOException {
        writeResponse(outputStreamWriter, HTTP_HEADERS_HTML, HTTP_BODY_HTML);
    }

    /**
     * Вызывает метод формирования ответа в поток записи данных в виде JSON.
     * @param outputStreamWriter Поток для записи данных.
     * @throws IOException Выбрасывает, если возникает ошибка при записи данных.
     */
    private void writeJSONResponse(BufferedWriter outputStreamWriter) throws IOException {
        writeResponse(outputStreamWriter, HTTP_HEADERS_JSON, new MessageTXT().mapMessageToJson());
    }

    /**
     * Вызывает метод формирования ответа в поток записи данных в виде вложения формата TXT файла.
     * @param outputStreamWriter Поток для записи данных.
     * @throws IOException Выбрасывает, если возникает ошибка при записи данных.
     */
    private void writeTEXTResponse(BufferedWriter outputStreamWriter) throws IOException {
        writeResponse(outputStreamWriter, HTTP_HEADERS_TEXT, HTTP_BODY_TEXT);
    }

    /**
     * Формирует ответ в поток записи данных.
     * @param outputStreamWriter Поток для записи данных.
     * @throws IOException Выбрасывает, если возникает ошибка при записи данных.
     */
    private void writeResponse(BufferedWriter outputStreamWriter, String headers, String body) throws IOException {
        outputStreamWriter.write(headers);
        outputStreamWriter.newLine();
        outputStreamWriter.write(body);
        outputStreamWriter.newLine();
        outputStreamWriter.flush();
    }
}