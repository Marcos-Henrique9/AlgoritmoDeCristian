/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmodecristian;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author mhhen
 */
public class Server extends Thread {
    
    private final ServerSocket serverSocket;
    private long timeRecv;  // tempo p/ receber mensagem do cliente
    private long timeSend;  // momento em que envia mensagem ao cliente
   
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Saída do nome do servidor
                String localHostName = java.net.InetAddress.getLocalHost().getHostName();
                System.out.println("Nome do Servidor: " + localHostName);
                
                System.out.println("Esperado cliente na porta " +
                                   serverSocket.getLocalPort() + "...");
                
                // Aceite uma conexão de clientes; bloqueio de chamadas
                Socket server = serverSocket.accept();
                System.out.println("Conectado em: " + server.getRemoteSocketAddress());
                
                // Recebe mensagem de clientes
                DataInputStream in = new DataInputStream(server.getInputStream());
                timeRecv = System.currentTimeMillis();
                System.out.println(in.readUTF());
                
                // Envia mensagem de volta aos clientes
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                long time_on_server = System.currentTimeMillis();
                timeSend = System.currentTimeMillis();
                out.writeLong(time_on_server);    // enviar o tempo total no servidor de volta ao cliente
                out.writeLong(timeSend);    // enviar o tempo de envio para o cliente
                
                // fecha a conexão
                server.close();
            } catch (SocketTimeoutException e) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                break;
            }
        }
    } 
    public static void main(String [] args) {
        int port = 3333;
        try {
            Thread t = new Server(port);
            t.start();
        } catch (IOException e) {
        }
    }
}