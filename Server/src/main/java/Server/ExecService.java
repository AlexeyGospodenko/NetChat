package Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecService {

    private final ExecutorService executorService;
    private static ExecService instance;

    public ExecService() {
        executorService = Executors.newCachedThreadPool();
    }

    public static ExecService getInstance() {
        if (instance == null) {
            instance = new ExecService();
        }
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
