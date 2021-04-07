package Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class HistoryServiceImpl implements HistoryService {

    private static HistoryServiceImpl instance;
    private String path;
    OutputStream os;

    private HistoryServiceImpl() {
    }

    public static HistoryServiceImpl getInstance() {
        if (instance == null) {
            instance = new HistoryServiceImpl();
        }
        return instance;
    }

    public void setLogin(String login) throws FileNotFoundException {
        path = login + ".txt";
        os = new FileOutputStream(path, true);
    }

    @Override
    public List<String> getHistory(int maxLines) throws IOException {
        List<String> history = new ArrayList<>();
        RandomAccessFile raf = new RandomAccessFile(path, "r");

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        int linesRead = 0;
        boolean isEnd = false;

        //Вычисляем номер позиции указателя как количество симовлов в файле минус размер буффера
        long pos = raf.length() - buffer.length;

        while (!isEnd) {
            //Если позиция положительная, то есть размер буффера меньше чем остаток файла
            if (pos > 0) {
                raf.seek(pos);
                //то заполняем весь буффер
                raf.read(buffer);
                String[] data = (new String(buffer, StandardCharsets.UTF_8)).split("\n");
                //Оставляя первую строку из массива так как она может быть прочитана не вся
                for (int i = data.length - 1; i > 0; i--) {
                    history.add(0, data[i]);
                    linesRead++;
                    //Если количество прочитанных строк совпало с необходимым то выходим
                    if (linesRead == maxLines) {
                        isEnd = true;
                        break;
                    }
                }
                //Сохраняем количество прочитанных байт без учета первой строки
                bytesRead += buffer.length - data[0].length();
                //Перемещаем указатель на: количество прочитанных байт и длину буффера
                pos = raf.length() - buffer.length - bytesRead;
            } else {
                //Если позиция меньше 0, то есть размер буффера больше оставшегося файла, то идем вначало файла
                raf.seek(0);
                //И заполняем в буффер только не прочитанные байты
                raf.read(buffer, 0, (int) (raf.length() - bytesRead));
                //Это же количество байт и бьем на строки чтобы не попал старый буффер
                String[] data = (new String(buffer, 0, (int) (raf.length() - bytesRead), StandardCharsets.UTF_8)).split("\n");
                for (int i = data.length - 1; i >= 0; i--) {
                    history.add(0, data[i]);
                    linesRead++;
                    //Если количество прочитанных строк совпало с необходимым то выходим
                    if (linesRead == maxLines) {
                        break;
                    }
                }
                //Или просто дочитываем остатки и выходим
                isEnd = true;
            }
        }

        return history;
    }

    @Override
    public void saveMessage(String message) throws IOException {
        byte[] buffer;
        buffer = message.getBytes(StandardCharsets.UTF_8);
        os.write(buffer);
        os.flush();
    }
}