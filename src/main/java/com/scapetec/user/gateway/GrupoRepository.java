package com.scapetec.user.gateway;

import java.util.List;

import com.scapetec.user.domain.GrupoEntity;
import com.scapetec.user.domain.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<GrupoEntity, Long>{
 
	List<GrupoEntity> findByUsuariosIn(UsuarioEntity usuarioEntity);
 
}