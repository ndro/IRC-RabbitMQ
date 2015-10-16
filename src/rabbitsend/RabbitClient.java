/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rabbitsend;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;
import static rabbitsend.RabbitSend.EXCHANGE_NAME;
import static rabbitsend.RabbitSend.channel;

/**
 *
 * @author Lenovo
 */
public class RabbitClient {
    public static void main(String[] args) throws IOException, TimeoutException {
        
        RabbitSend rabbit = new RabbitSend();
        String channel;
        //inisialisasi
        rabbit.turnOn("167.205.32.46", 5672);
        boolean isOnline = true;
        
        Scanner in = new Scanner(System.in);
        while(isOnline) {
            
            String[] input = in.nextLine().split(" ");
            String command = input[0].toLowerCase();
            
            switch (command) {
                case "/nick":
                    if(input.length >= 2){
                        rabbit.login(input[1]);
                    }
                    else{
                        System.out.println("write nick name please!");
                    }
                    break;
                case "/join":
                    if(input.length >= 2){
                        channel = input[1];
                        rabbit.join(channel);
                    }
                    else{
                        System.out.println("write channel name please!");
                    }
                    break;
                case "/leave":
                    if(input.length >= 2){
                        channel = input[1];
                        if(rabbit.checkChannel(channel)){
                            rabbit.leave(channel);
                        }
                        else{
                            System.out.println("wrong channel name!!");
                        }
                    }
                    else{
                        System.out.println("write channel name please!");
                    }
                    break;
                case "/exit":
                    System.out.println("bye");
                    System.exit(0);
                    break;
                default:
                    if(command.contains("@")) {
                        String channelname = command.substring(1);
                        if(rabbit.checkChannel(channelname)){    
                            String message = "";
                            for(int i=1; i<input.length; i++) {
                                message+=input[i]+" ";
                            }
                            rabbit.chat(channelname, message);
                        }
                    }
                    else {
                        String message = "";
                        for(int i=0; i<input.length; i++) {
                            message+=input[i]+" ";
                        }
                        rabbit.broadcast(message);
                    }
            }
        }
    }
}
