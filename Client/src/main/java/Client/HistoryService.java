package Client;

import java.io.IOException;
import java.util.List;

public interface HistoryService {

    List<String> getHistory(int maxLines) throws IOException;

    void saveMessage(String history) throws IOException;

}