package eu.hsinteractive.inbalance;


import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sash
 */
public class InDaBalanceTest {
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: inDaBalanceTest [client hostname port count | server port_to_listen_on]");
            return;
        }
        
        if ("client".equals(args[0]) && args.length == 4) {
            for (int i = 0; i < Integer.parseInt(args[3]); i++) {
                new Client(args[1], Integer.parseInt(args[2]), i).start();
            } 
        } else if ("server".equals(args[0]) && args.length == 2) {
            new Server(Integer.parseInt(args[1]));
        } else {
            System.out.println("Usage: inDaBalanceTest [client hostname:port | server port_to_listen_on]");
            return;
        }
    }
    
}
