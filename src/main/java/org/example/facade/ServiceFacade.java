package org.example.facade;

import org.example.calculator.ReflexCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ServiceFacade {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
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
            System.out.println(request);

            sendResponse(request.toString(), clientSocket, in);

            in.close();
        }
    }

    private static void sendResponse(String inputLine, Socket clientSocket, BufferedReader in) throws IOException, URISyntaxException {
        String outputLine;
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        URI path = new URI(inputLine.split(" ")[1]);


        outputLine = buildResponse(path);
        out.println(outputLine);
        out.close();


        clientSocket.close();
    }

    private static String buildResponse(URI path) throws IOException {
        if (path.getPath().equals("/") || path.getPath().equals("/calculator")){
            return  getHeader() + getBody();
        } else if (path.getPath().contains("/computar")){
            return getHeader() + HttpConnectionToCalculator.calculate(path.getQuery().split("=")[1]);
        }
        return null;
    }

    private static String getHeader(){
        return """
                HTTP/1.1 200 OK
                Content-Type: text/html
                \r
                """;
    }

    private static String getBody(){
        return """
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Form Example</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body>
                        <h1>Holi, Mete una funcion que te la contesto</h1>
                        <form action="/hello">
                            <label for="name">Name:</label><br>
                            <input type="text" id="name" name="name" value="John"><br><br>
                            <input type="button" value="Submit" onclick="loadGetMsg()">
                        </form>
                        <div id="getrespmsg"></div>
                        <script>
                            function loadGetMsg() {
                                let nameVar = document.getElementById("name").value;
                                const xhttp = new XMLHttpRequest();
                                xhttp.onload = function() {
                                    document.getElementById("getrespmsg").innerHTML =
                                    this.responseText;
                                }
                                xhttp.open("GET", "/computar?comando="+nameVar);
                                xhttp.send();
                            }
                        </script>
                    </body>
                </html>""";
    }
}
