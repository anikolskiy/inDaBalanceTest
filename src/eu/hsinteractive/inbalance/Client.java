package eu.hsinteractive.inbalance;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.NumberFormat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sash
 */
public class Client extends Thread {

    private String host;
    
    private int port;
    
    private boolean done;
    
    private int clientNumber;
    
    private int requestsProcessed;
    
    private int errors;
    
    private long startTime;

    private long totalResponseTime;
    
    private long maxResponseTime;
    
    private long minResponseTime;
    
    private long bytesProcessed;
    
    public Client(String host, int port, int clientNumber) {
        this.host = host;
        this.port = port;
        this.done = false;
        this.clientNumber = clientNumber;
        this.requestsProcessed = 0;
        this.errors = 0;
        this.bytesProcessed = 0;
        this.startTime = 0;
        this.maxResponseTime = 0;
        this.minResponseTime = Long.MAX_VALUE;
    }
    
    public void finish() {
        done = true;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        
        while (!done) {
            try {
                simulateOneRequest();
            } catch (Exception e) {
                e.printStackTrace();
                errors++;
                printStats();
            }
        }
    }

    public void simulateOneRequest() throws UnknownHostException, IOException {
        byte data[]          = new byte[(int)(Math.random() * (Math.random() > 0.1 ? 5 : 100)*1024*100) + 1];
        byte receiveBuffer[] = new byte[1024];
        
        int sendOffset = 0;
        int receiveOffset = 0;
        
        int rn = (int)(Math.random() * 255);
        
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)((rn + i) % 256);
        }

        //System.out.println("connecting...");
        long responseTime = System.currentTimeMillis();
        Socket s = new Socket();
        s.setReuseAddress(true);
        s.connect(new InetSocketAddress(host, port));
        
        //System.out.println("connected");
        
        InputStream is = s.getInputStream();
        OutputStream os = s.getOutputStream();
        
        responseTime = System.currentTimeMillis() - responseTime;
        
        minResponseTime = Math.min(responseTime, minResponseTime);
        maxResponseTime = Math.max(responseTime, maxResponseTime);
        totalResponseTime += responseTime;
        
        //System.out.println("sending data, length: " + data.length);
        while (sendOffset < data.length) {
            int bytesToSend = ((data.length - sendOffset) > 20480 ? 20480 : (data.length - sendOffset));
            os.write(data, sendOffset, bytesToSend);
            sendOffset += bytesToSend;
            
            int bytesRead;

            //System.out.println("receiving data");
            while ((receiveOffset < sendOffset) && ((bytesRead = is.read(receiveBuffer)) > 0)) {
                for (int i = 0; i < bytesRead; i++, receiveOffset++) {
                    if (data[receiveOffset] != receiveBuffer[i]) {
                        System.err.println("ERROR: Data mismatch!");
                        errors++;
                        printStats();
                        s.close();
                        return;
                    }
                }

                //System.out.println("dl: " + data.length + ", offset: " + receiveOffset);
            }
        }
        
        
        //System.out.println("done");
        
        s.close();
        
        //System.out.println("socket closed");

        if (receiveOffset < data.length) {
            System.err.println("ERROR: Sent " + data.length + " bytes, received only " + receiveOffset + " bytes");
            errors++;
            printStats();
            return;
        }
        
        bytesProcessed += data.length;
        
        if (++requestsProcessed % 10 == 0) {
            printStats();
        }
    }
    
    private void printStats() {
        long runTime = System.currentTimeMillis() - startTime;
        double mbProcessed = (bytesProcessed / (1024*1024));

        System.out.println("Client " + clientNumber + ", success: " + requestsProcessed + ", errors: " + errors + ", MB: "
                + (int)mbProcessed + ", run time: " + (runTime / 1000) + "s - " + NumberFormat.getNumberInstance().format((double)runTime / 3600000)
                + "h, speed: " + NumberFormat.getNumberInstance().format(mbProcessed / (runTime / 1000))
                + "MB/s, avg resp: " + (totalResponseTime / requestsProcessed)
                + "ms, min resp: " + minResponseTime + "ms, max resp: " + maxResponseTime + "ms");
    }
    
}
