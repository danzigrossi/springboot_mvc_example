package com.scapetec.user.usecase;

import java.util.ArrayList;
import java.util.List;

import com.scapetec.user.domain.GrupoEntity;
import com.scapetec.user.dto.GrupoModel;
import com.scapetec.user.gateway.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
public class GrupoService {
 
	@Autowired
	private GrupoRepository grupoRepository;
 
	/**CONSULA OS GRUPOS CADASTRADOS*/
	@Transactional(readOnly = true)
	public List<GrupoModel> consultarGrupos(){
 
		List<GrupoModel> gruposModel =  new ArrayList<GrupoModel>();
 
		/*CONSULTA TODOS OS GRUPOS*/
		List<GrupoEntity> gruposEntity = this.grupoRepository.findAll();
 
 
	    gruposEntity.forEach(grupo ->{ 
		   gruposModel.add(new GrupoModel(grupo.getCodigo(), grupo.getDescricao())); 
	    });
 
		return gruposModel;
	}
 
}