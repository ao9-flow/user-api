package io.ao9.flow.api.user.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
	UserEntity findByUserId(String userId);
    UserEntity findByEmail(String email);
    
}