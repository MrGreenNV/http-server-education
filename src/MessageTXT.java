import java.lang.reflect.Field;

/**
 * Класс представляет собой текстовое сообщение для тестирования программы.
 * @author mrGreenNV
 */
public class MessageTXT {

    /** Содержимое сообщения. */
    private String message = "Hi! This is a simple line in JSON.";

    /** Размер сообщения. */
    private int size = 34;

    /** Получает значение размера сообщения. */
    public int getSize() {
        return size;
    }

    /** Устанавливает размер сообщения. */
    public void setSize(int size) {
        this.size = size;
    }

    /** Получает содержимое сообщения. */
    public String getMessage() {
        return message;
    }

    /** Устанавливает содержимое сообщения. */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Конвертирует объект MessageTXT в JSON формат с использованием рефлексии.
     * @return Строку в JSON формате.
     */
    public String mapMessageToJson() {

        StringBuilder res = new StringBuilder();

        res.append("{");
        res.append("\n");

        Class<?> clazz = this.getClass();
        // Получаем список приватных полей класса.
        Field[] fields = clazz.getDeclaredFields();

        try {

            for (int i = 0; i < fields.length; i++) {
                // Получаем доступ к приватным полям класса.
                fields[i].setAccessible(true);
                String fieldName = fields[i].getName();
                res.append("\t\"");
                res.append(fieldName);
                res.append("\": \"");
                res.append(fields[i].get(this));
                if (i != fields.length - 1) {
                    res.append("\",\n");
                } else {
                    res.append("\"");
                }
            }

        } catch (IllegalAccessException iaEc) {
            throw new RuntimeException(iaEc);
        }

        res.append("\n");
        res.append("}");

        System.out.println(res);

        return res.toString();
    }
}
