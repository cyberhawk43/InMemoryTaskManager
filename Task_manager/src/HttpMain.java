import managment.http.HttpTaskServer;

import java.io.IOException;

public class HttpMain {
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
