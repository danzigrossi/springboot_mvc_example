package com.scapetec.user.usecase;

import com.scapetec.user.domain.GrupoEntity;
import com.scapetec.user.domain.PermissaoEntity;
import com.scapetec.user.domain.UsuarioEntity;
import com.scapetec.user.domain.UsuarioSecurityModel;
import com.scapetec.user.dto.UsuarioModel;
import com.scapetec.user.gateway.GrupoRepository;
import com.scapetec.user.gateway.PermissaoRepository;
import com.scapetec.user.gateway.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserUsecase  implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private PermissaoRepository permissaoRepository;

    /***
     * CONSULTA UM USUÁRIO POR LOGIN
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws BadCredentialsException,DisabledException {

        UsuarioEntity usuarioEntity = usuarioRepository.findByLogin(login);

        if(usuarioEntity == null)
            throw new BadCredentialsException("Usuário não encontrado no sistema!");

        if(!usuarioEntity.isAtivo())
            throw new DisabledException("Usuário não está ativo no sistema!");

        return new UsuarioSecurityModel(
                usuarioEntity.getLogin(),
                usuarioEntity.getSenha(),
                usuarioEntity.isAtivo(),
                this.buscarPermissoesUsuario(usuarioEntity));
    }

    /***
     * BUSCA AS PERMISSÕES DO USUÁRIO
     * @param usuarioEntity
     * @return
     */
    public List<GrantedAuthority> buscarPermissoesUsuario(UsuarioEntity usuarioEntity) {

        List<GrupoEntity> grupos = grupoRepository.findByUsuariosIn(usuarioEntity);

        return this.buscarPermissoesDosGrupos(grupos);
    }

    /***
     * BUSCA AS PERMISSÕES DO GRUPO
     * */
    public List<GrantedAuthority> buscarPermissoesDosGrupos(List<GrupoEntity> grupos) {
        List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

        for (GrupoEntity grupo: grupos) {

            List<PermissaoEntity> lista = permissaoRepository.findByGruposIn(grupo);

            for (PermissaoEntity permissao: lista) {
                auths.add(new SimpleGrantedAuthority(permissao.getPermissao()));
            }
        }

        return auths;
    }

    /***
     * SALVA UM NOVO REGISTRO DE USUÁRIO
     * @param usuarioModel
     */
    public void salvarUsuario(UsuarioModel usuarioModel){

        UsuarioEntity usuarioEntity =  new UsuarioEntity();

        /*SETA O USUÁRIO COMO ATIVO NO SISTEMA*/
        usuarioEntity.setAtivo(true);

        /*LOGIN DO USUÁRIO*/
        usuarioEntity.setLogin(usuarioModel.getLogin());

        /*NOME DO USUÁRIO A SER SALVO*/
        usuarioEntity.setNome(usuarioModel.getNome());

        /*CRIPTOGRAMA E INFORMA A SENHA*/
        usuarioEntity.setSenha(new BCryptPasswordEncoder().encode(usuarioModel.getSenha()));


        /*PEGANDO A LISTA DE GRUPOS SELECIONADOS*/
        GrupoEntity grupoEntity = null;
        List<GrupoEntity> grupos =  new ArrayList<GrupoEntity>();
        for (Long codigoGrupo : usuarioModel.getGrupos()){


            if(codigoGrupo != null){
                Optional optGroup = grupoRepository.findById(codigoGrupo);
                if  (optGroup.isPresent()) {
                    grupoEntity = (GrupoEntity)optGroup.get();

                }

                grupos.add(grupoEntity);
            }
        }

        usuarioEntity.setGrupos(grupos);

        this.usuarioRepository.save(usuarioEntity);
    }

    /***
     * CONSULTA OS USUÁRIOS CADASTRADOS
     * @return
     */
    public List<UsuarioModel> consultarUsuarios(){

        List<UsuarioModel> usuariosModel = new ArrayList<UsuarioModel>();

        List<UsuarioEntity> usuariosEntity = this.usuarioRepository.findAll();

        usuariosEntity.forEach(usuarioEntity ->{

            usuariosModel.add(
                    new UsuarioModel(usuarioEntity.getCodigo(),
                            usuarioEntity.getNome(),
                            usuarioEntity.getLogin(),
                            null,
                            usuarioEntity.isAtivo(),
                            null));
        });


        return usuariosModel;
    }

    /**
     * DELETA UM USUÁRIO  PELO CÓDIGO
     * */
    public void excluir(Long codigoUsuario){

        this.usuarioRepository.deleteById(codigoUsuario);
    }

    /***
     * CONSULTA UM USUÁRIO PELO SEU CÓDIGO
     * @param codigoUsuario
     * @return
     */
    public UsuarioModel consultarUsuario(Long codigoUsuario){

        UsuarioEntity usuarioEntity = null;

        Optional optUsuario = usuarioRepository.findById(codigoUsuario);
        if  (optUsuario.isPresent()) {
            usuarioEntity = (UsuarioEntity)optUsuario.get();

        }

        List<Long> grupos =  new ArrayList<Long>();

        usuarioEntity.getGrupos().forEach(grupo ->{

            grupos.add(grupo.getCodigo());

        });

        return new UsuarioModel(
                usuarioEntity.getCodigo(),
                usuarioEntity.getNome(),
                usuarioEntity.getLogin(),
                null,
                usuarioEntity.isAtivo(),
                grupos);

    }

    /**
     * ALTERA AS INFORMAÇÕES DO USUÁRIO
     * */
    public void alterarUsuario(UsuarioModel usuarioModel){


        UsuarioEntity usuarioEntity = null;

        Optional optUsuario = usuarioRepository.findById(usuarioModel.getCodigo());
        if  (optUsuario.isPresent()) {
            usuarioEntity = (UsuarioEntity)optUsuario.get();

        }

        /*USUÁRIO ATIVO OU INATIVO*/
        usuarioEntity.setAtivo(usuarioModel.isAtivo());

        /*LOGIN DO USUÁRIO*/
        usuarioEntity.setLogin(usuarioModel.getLogin());

        /*NOME DO USUÁRIO A SER SALVO*/
        usuarioEntity.setNome(usuarioModel.getNome());

        /*CRIPTOGRAMA E INFORMA A SENHA*/
        if(!StringUtils.isEmpty(usuarioModel.getSenha()))
            usuarioEntity.setSenha(new BCryptPasswordEncoder().encode(usuarioModel.getSenha()));


        /*PEGANDO A LISTA DE GRUPOS SELECIONADOS*/
        GrupoEntity grupoEntity = null;
        List<GrupoEntity> grupos =  new ArrayList<GrupoEntity>();
        for (Long codigoGrupo : usuarioModel.getGrupos()){


            if(codigoGrupo != null){

                Optional optGroup = grupoRepository.findById(codigoGrupo);
                if  (optGroup.isPresent()) {
                    grupoEntity = (GrupoEntity)optGroup.get();

                }

                grupos.add(grupoEntity);
            }
        }

        /*SETA A LISTA DE GRUPO DO USUÁRIO*/
        usuarioEntity.setGrupos(grupos);

        /*SALVANDO ALTERAÇÃO DO REGISTRO*/
        this.usuarioRepository.saveAndFlush(usuarioEntity);
    }
}
