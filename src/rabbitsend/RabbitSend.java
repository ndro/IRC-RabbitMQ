/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rabbitsend;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RabbitSend {

    public final static String EXCHANGE_NAME = "direct_logs";
    
    public static Connection connection;
    public static Channel channel;
    public static String username = "Unknown";
    public static String messageQueue;
    public static List<String> channels = new ArrayList<>();
    
    public void turnOn(String host, int port) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        //deklarasi exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        //deklarasi queue
        messageQueue =channel.queueDeclare().getQueue();
        
//channel.queueBind(messageQueue, EXCHANGE_NAME, "");
        
        //deklarasi listener
        Consumer receiver = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws UnsupportedEncodingException {
                String message = new String(body, "UTF-8");
                System.out.println(message);
            }
        };
        channel.basicConsume(messageQueue, true, receiver);
    }
    
    public void login(String nickname) {        
        username = nickname;
        System.out.println("Your username is "+username);        
    }   
    
    public void join(String channelname) throws IOException {
        channel.queueBind(messageQueue, EXCHANGE_NAME, channelname);
        System.out.println("You're now member of channel " + channelname);
        
        channels.add(channelname);
    }
    
    public void chat(String channelname, String text) throws IOException {
        text = "["+ channelname + "]("+ username +") : " + text;
        channel.basicPublish(EXCHANGE_NAME, channelname, null, text.getBytes());
    }
    
    public void broadcast(String text) throws IOException {
        for(String cList : channels){
            String baru = "["+ cList + "]("+ username +") : " + text;
            channel.basicPublish(EXCHANGE_NAME, cList, null, baru.getBytes());
        }
    }
    
    public void leave(String channelname) throws IOException {
        channel.queueUnbind(messageQueue, EXCHANGE_NAME, channelname);
        System.out.println("You're now leave " + channelname);
        
        channels.remove(channelname);
    }
    
    public boolean checkChannel(String channelname) {
        boolean found = false;
        
        int i = 0;
        while(!found && i<channels.size()){
            if(channels.get(i).equals(channelname)){
                found = true;
            }
            i++;
        }
        
        return found;
    }
    
}
