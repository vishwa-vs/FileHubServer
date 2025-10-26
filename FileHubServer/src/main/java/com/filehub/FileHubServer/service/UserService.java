package com.filehub.FileHubServer.service;

import com.filehub.FileHubServer.entity.UserIpEntity;
import com.filehub.FileHubServer.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @PostConstruct
    public void saveServerIp() {
        try {
           UserIpEntity userIpEntity = userRepo.findByUserEmail("Vishwa@gmail.com");

            if(userIpEntity == null)
            {
                userIpEntity = new UserIpEntity();
                String myIP1 = InetAddress.getLocalHost().getHostAddress();
                userIpEntity.setUserIP(myIP1);
                userIpEntity.setUserEmail("Vishwa@gmail.com");
                userRepo.save(userIpEntity);
            }
            else
            {
                String myIP = InetAddress.getLocalHost().getHostAddress();
                userIpEntity.setUserIP(myIP);
                userRepo.save(userIpEntity);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
