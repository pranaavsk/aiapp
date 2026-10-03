package com.aiapp;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class AppServer {
    private final HttpServer server;


    public AppServer(int port ) throws IOException{
        server =HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/api/health",exchange ->
{
    String response="OK";
    exchange.sendResponseHeaders(200,response.getBytes().length);
  try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.setExecutor(null);
    }
    public void start()
    {
        server.start();
    }
    public void stop()
    {
        server.stop(0);
    }
    public static void main(String[]args) throws IOException{
        AppServer appServer =new AppServer(8080);
        appServer.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
