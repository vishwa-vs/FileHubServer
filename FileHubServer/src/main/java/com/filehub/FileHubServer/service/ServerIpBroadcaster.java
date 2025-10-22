package com.filehub.FileHubServer.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
@Service
public class ServerIpBroadcaster implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        broadcastIp();
    }

    private DatagramSocket udpSocket;

    @PostConstruct
    public void init() throws Exception {
        udpSocket = new DatagramSocket();
        udpSocket.setBroadcast(true);
    }

    @Scheduled(fixedRate = 2000)
    public void broadcastIp()
    {
        try {
            String myIP = InetAddress.getLocalHost().getHostAddress();
            String broadcastMessage = "SERVER:" + myIP;
            byte[] buffer = broadcastMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length,InetAddress.getByName("255.255.255.255"),8888);
            udpSocket.send(packet);
        } catch (Exception e) {}
    }

    @PreDestroy
    public void cleanup() {
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
    }
}
