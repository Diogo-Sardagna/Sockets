package trabalho1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            String input;
            while (true) {
                System.out.println("=================================================");
                System.out.print("Digite a operação desejada (ou EXIT para sair): ");
                input = scanner.nextLine();

                if (input.equalsIgnoreCase("EXIT")) {
                    break;
                }

                out.println(input);

                // Lê a resposta completa do servidor
                StringBuilder response = new StringBuilder();
                String line;
                boolean isList = input.equalsIgnoreCase("LIST");
                boolean isGetEquipe = input.startsWith("GET_EQUIPE");
                boolean isListEquipes = input.startsWith("LIST_EQUIPES");

                while ((line = in.readLine()) != null) {
                    if (isList && line.equals("END_OF_LIST")) {
                        break;
                    } else if (isGetEquipe && line.equals("END_OF_EQUIPE")) {
                        break;
                    } else if (isListEquipes && line.equals("END_OF_LIST_EQUIPES")) {
                        break;
                    }
                    response.append(line).append("\n");
                    
                    // Se não for, leia apenas a primeira linha e pare
                    if (!isList && !isGetEquipe && !isListEquipes) {
                        break;
                    }
                }

                System.out.println("Resposta do servidor:\n" + response.toString().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
