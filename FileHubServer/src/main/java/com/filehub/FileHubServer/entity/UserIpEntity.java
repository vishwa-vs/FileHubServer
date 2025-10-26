package com.filehub.FileHubServer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserIpEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int userId;

        @Column
        String userEmail;

        @Column
        String userIP;
}
