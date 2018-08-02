package com.scapetec.user.gateway;

import java.util.List;

import com.scapetec.user.domain.GrupoEntity;
import com.scapetec.user.domain.PermissaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
 
public interface PermissaoRepository extends JpaRepository<PermissaoEntity, Long> {
 
	List<PermissaoEntity> findByGruposIn(GrupoEntity grupoEntity);
}