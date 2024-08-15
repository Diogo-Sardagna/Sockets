/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.udesc.sockets;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Notebook
 */
public class Server {
    
    public static void main(String[] args) throws IOException {
        
        ServerSocket server = new ServerSocket(80);
        server.setReuseAddress(true);
        
        while (true) {
            System.out.println("Aguardando conexão...");
            try (Socket conn = server.accept();) {
                System.out.println("Conectando com: " + conn.getInetAddress().getHostAddress());
                
                OutputStream out = conn.getOutputStream();
                String msg = "Olá mundo do outro lado";
                out.write(msg.getBytes());
            }
        }
    }
}
