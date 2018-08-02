package com.scapetec.user.gateway;

import com.scapetec.user.domain.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

 
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
 
	UsuarioEntity findByLogin(String login);
 
}