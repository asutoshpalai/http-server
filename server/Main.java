package server;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {

    HTTPServer server = new HTTPServer();

    try {
      server.runServer(3000);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      //          
    }


  }


}

