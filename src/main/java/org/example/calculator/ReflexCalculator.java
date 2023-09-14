package org.example.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ReflexCalculator {
    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        while (true){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            StringBuilder request = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                request.append(inputLine).append("\n");
                if (!in.ready()) {break; }
            }
            sendResponse(request.toString(), clientSocket);

            in.close();
        }
    }


    private static void sendResponse(String inputLine, Socket clientSocket) throws IOException, URISyntaxException {
        String outputLine;
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        URI path = new URI(inputLine.split(" ")[1]);

        try {
            Calculator c = new Calculator(path.getQuery().split("=")[1]);
            outputLine = getHeader() + c.calculate();
        } catch (Exception e){
            outputLine = getHeader() + "No ingresaste un metodo valido";
        }

        out.println(outputLine);
        out.close();


        clientSocket.close();
    }

    private static String getHeader(){
        return """
                HTTP/1.1 200 OK
                Content-Type: text/html
                \r
                """;
    }
}
