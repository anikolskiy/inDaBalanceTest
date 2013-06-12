package eu.hsinteractive.inbalance;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sash
 */
public class ServerSocket extends java.net.ServerSocket {

    private boolean done;
    
    public ServerSocket(int port) throws IOException {
        super(port);
        System.out.println("Listening on " + port);
    }
    
    public void stop() {
        done = true;
    }

    public void listen() {
        int errors = 0;
        int successfull = 0;
        done = false;
        
        while (!done) {
            try {
                //System.out.println("Waiting for connection...");
                Socket requestSocket = accept();
                
                //System.out.println("Connected");
                
                InputStream  is = requestSocket.getInputStream();
                OutputStream os = requestSocket.getOutputStream();
                
                byte[] buffer = new byte[20480]; // 10kB
                
                int bytesRead;
                
                // echo data back
                //int i = 0;

                while ((bytesRead = is.read(buffer)) > 0) {
                    //System.out.println("" + ++i + " br: " + bytesRead);
                    os.write(buffer, 0, bytesRead);
                    //System.out.println("written...");
                }
                
                //System.out.println("Closing socket");
                requestSocket.close();
                errors = 0;
                
                System.out.println("Success: " + ++successfull + ", errors: " + errors);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                errors++;
                
                /*if (errors == 50) {
                    done = true;
                }*/
            }
        }
        
        try {
            close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
}
