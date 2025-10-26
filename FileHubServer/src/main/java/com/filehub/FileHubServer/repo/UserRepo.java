package com.filehub.FileHubServer.repo;

import com.filehub.FileHubServer.entity.UserIpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<UserIpEntity, Integer> {
    UserIpEntity findByUserEmail(String username);
}
