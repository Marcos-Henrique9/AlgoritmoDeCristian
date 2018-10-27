/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritmodecristian;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author mhhen
 */
public class Client {

    private String NomeServidor;
    private int PortaServidor;
    private static int count;   // quant. conexões
    private Timer timer;        // este temporizador é para enviar o pedido ao servidor a cada 6 segundos
    private PrintWriter pr;     // escrever os timestamps neste arquivo
    private long t0;      // tempo que envia o pedido ao servidor
    private long t3;      // a hora em que recebe a resposta do servidor
    
    // Constructor
    public Client(String nomeServidor, int portaServidor) {
        this.NomeServidor = nomeServidor;
        this.PortaServidor = portaServidor;
        Client.count = 0;
        this.timer = new Timer();
        try {
            this.pr = new PrintWriter("C:\\Users\\mhhen\\Documents\\NetBeansProjects\\AlgoritmoDeCristian\\Acari.txt", "UTF-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    class Conversation extends TimerTask {
        
        @Override
        public void run() {
            if (count < 8) {
                try {
                    System.out.println("Conectando a " + NomeServidor + " na porta " + PortaServidor);
                    
                    // Conectando ao servidor
                    Socket client = new Socket(NomeServidor, PortaServidor);
                    System.out.println("Conectado a " + client.getRemoteSocketAddress());
                
                    // Enviar mensagem para o servidor
                    OutputStream outToServer = client.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);
                    t0 = System.currentTimeMillis();
                    out.writeUTF("Olá de " + client.getLocalSocketAddress());
                
                    // receber msg do servidor
                    InputStream inFromServer = client.getInputStream();
                    DataInputStream in = new DataInputStream(inFromServer);
                    long t1 = in.readLong();   // recebe o tempo total no servidor
                    long t2 = in.readLong();   // recebe o tempo de envio no servidor
                    t3 = System.currentTimeMillis();
                    
                    // fecha a conexão
                    client.close();
                    
                    // Incremento
                    count ++;
                    
                    //Insere uns tempos pra simular o delay nas msgs
                    //Definir tempos de atraso para simular os atrasos de solicitação / resposta
                    /**/
                    t1 += 1;
                    t2 += 2;
                    t3 += 3;
                    
                    
                    // Obtém o RTT (tempo de atraso de ida e volta)
                    long rtt = (t3 - t0) - (t2 - t1);

                    
                    pr.println("Tempo de envio do cliente: " + t0); 
                    pr.println("Tempo de Recebimento do Servidor : " + t1);
                    pr.println("Tempo de envio do Servidor: " + t2);
                    pr.println("Tempo de Recebimento do  Cliente: " + t3);
                    
                    pr.println(" RTT ");
                    pr.println("formula1 -> (t3 - t0) = " + (t3 - t0));
                    pr.println("formula2 -> (t2 - t1) = " + (t2 - t1));
                    
                    pr.println(" RTT divido por 2 ");
                    pr.println("(formula1 - formula2)/2 =  " + rtt / 2);

                    //RTT Offset
                    long th = (t1 - t0) + (t2 - t3 ) / 2;
                    pr.println(" RTT Offset ");
                    pr.println("th -> (t1 - t0) + (t2 - t3 ) / 2 = " + th); 
                    
                    
                    long cristianTime = t2 + (rtt / 2);
                    long cristianTimeComOffset = t2 + (th);
                    pr.println(" Horario de Cristian ");
                    pr.println("Horario de Cristian -> t2 + (rtt/2): " + cristianTime); 
                    pr.println("Horario de Cristian -> t2 + (rtt_offset): " + cristianTimeComOffset); 
                } catch (IOException e) {
                }
            } else {
                pr.close(); // Liberar pra o arquivo
                timer.cancel();
                timer.purge();
            }
        }
    }
    
    public static void main(String [] args) {
        
        //Server name
        String nomeServidor = "localhost";
        
        //Server port
        int serverPort = 3333;
        
        //Cria um client que vai conectar no servidor
        Client client = new Client(nomeServidor, serverPort);
        
        //tempo que o objeto Timer vai fazer as conexoes
         long period = 80;
        
        //Instancia a classe Conversation
        Client.Conversation  conversation = client.new Conversation();
        
        client.timer.schedule(conversation, 0, period);
    }
}