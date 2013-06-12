/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.hsinteractive.inbalance;

import java.io.IOException;

/**
 *
 * @author sash
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    
    private int port;
    
    private Thread restarter;
    
    private long nextStartUp;
    
    public Server(int port) {
        this.port = port;
        
        restarter = new Thread(this);
        restarter.start();
        
        nextStartUp = System.currentTimeMillis();
        listen();
    }

    @Override
    public void run() {
        while (true) {
            long nextShutdown = System.currentTimeMillis() + 20000 + (long)(Math.random() * 10000);

            while (System.currentTimeMillis() < nextShutdown) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                }
            }
            nextStartUp  = nextShutdown + 5000 + (long)(Math.random() * 5000);

            System.out.println("Stopping server");
            serverSocket.stop();

            waitForTheStartup();
        }
    }
    
    private void waitForTheStartup() {
        while (System.currentTimeMillis() < nextStartUp) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
            }
        }
    }
    
    private void listen() {
        while (true) {
            waitForTheStartup();
            
            try {
                System.out.println("Starting server");
                serverSocket = new ServerSocket(port);
                serverSocket.listen();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
