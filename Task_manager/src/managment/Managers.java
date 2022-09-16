package managment;

import managment.http.HttpTaskManager;
import managment.inMemory.InMemoryHistoryManager;


import java.io.IOException;


public class Managers {
    public static TaskManager getDefault() throws IOException {
        getDefaultHistory();
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}