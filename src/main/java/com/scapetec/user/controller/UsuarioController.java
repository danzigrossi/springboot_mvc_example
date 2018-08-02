package com.scapetec.user.controller;

import java.util.List;
 
import javax.validation.Valid;

import com.scapetec.user.dto.GrupoModel;
import com.scapetec.user.dto.UsuarioModel;
import com.scapetec.user.usecase.GrupoService;
import com.scapetec.user.usecase.UserUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

 
/**
 * 
 * @author Cícero Ednilson
 * 
 *OBJETO RESPONSÁVEL POR REALIZAR AS ROTINAS DE USUÁRIO.
 * 
 */
@Controller
@RequestMapping("/usuario") 
public class UsuarioController {
 
	/**INJETANDO O OBJETO GrupoService*/
	@Autowired
	private GrupoService grupoService;
 
	/**INJETANDO O OBJETO UsuarioService*/
	@Autowired 
	private UserUsecase usuarioService;
 
 
 
	/***
	 * CHAMA A FUNCIONALIDADE PARA CADASTRAR UM NOVO USUÁRIO NO SISTEMA
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/novoCadastro", method= RequestMethod.GET)	
	public ModelAndView novoCadastro(Model model) {
 
		/*LISTA DE GRUPOS QUE VAMOS MOSTRAR NA PÁGINA*/
		model.addAttribute("grupos", grupoService.consultarGrupos());
 
		/*OBJETO QUE VAMOS ATRIBUIR OS VALORES DOS CAMPOS*/
		model.addAttribute("usuarioModel", new UsuarioModel());
 
	    return new ModelAndView("novoCadastro");
	}
 
 
 
	/***
	 * SALVA UM NOVO USUÁRIO NO SISTEMA
	 * @param usuarioModel
	 * @param result
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="/salvarUsuario", method= RequestMethod.POST)
	public ModelAndView salvarUsuario(@ModelAttribute 
								@Valid UsuarioModel usuarioModel, 
								final BindingResult result,
								Model model,
								RedirectAttributes redirectAttributes){
 
		/*VERIFICA SE TEM ALGUM ERRO (@NotEmpty),
		 *SE TIVER ALGUM ERRO DEVEMOS RETORNAR PARA A MESMA PÁGINA PARA O USUÁRIO CORRIGIR*/
		if(result.hasErrors()){
 
 
 
			List<GrupoModel> gruposModel =grupoService.consultarGrupos();
 
			/*POSICIONANDO OS CKECKBOX SELECIONADOS
			 * 
			 * SE O SISTEMA ENCONTROU ALGUM ERRO DE VALIDAÇÃO DEVEMOS 
			 * BUSCAR OS GRUPOS E MARCAR COMO SELECIONADO NOVAMENTE 
			 * PARA MOSTRAR NÁ PÁGINAS DA FORMA QUE O USUÁRIO ENVIO A REQUEST*/
			gruposModel.forEach(grupo ->{
 
				if(usuarioModel.getGrupos() != null && usuarioModel.getGrupos().size() >0){
 
					usuarioModel.getGrupos().forEach(grupoSelecionado->{
 
						/*DEVEMOS MOSTRAR NA PÁGINA OS GRUPOS COM O CHECKBOX SELECIONADO*/
						if(grupoSelecionado!= null){
							if(grupo.getCodigo().equals(grupoSelecionado))
								grupo.setChecked(true);
						}					
					});				
				}
 
			});
 
			/*ADICIONA O GRUPOS QUE VÃO SER MOSTRADOS NA PÁGINA*/
			model.addAttribute("grupos", gruposModel);
 
			/*ADICIONA OS DADOS DO USUÁRIO PARA COLOCAR NO FORMULÁRIO*/
			model.addAttribute("usuarioModel", usuarioModel);
 
			/*RETORNA A VIEW*/
			return new ModelAndView("novoCadastro");	
		}
		else{
 
			/*SALVANDO UM NOVO REGISTRO*/
			usuarioService.salvarUsuario(usuarioModel);
 
		}
 
 
		ModelAndView modelAndView = new ModelAndView("redirect:/usuario/novoCadastro");
 
		/*PASSANDO O ATRIBUTO PARA O ModelAndView QUE VAI REALIZAR 
		 * O REDIRECIONAMENTO COM A MENSAGEM DE SUCESSO*/
		redirectAttributes.addFlashAttribute("msg_resultado", "Registro salvo com sucesso!");
 
		/*REDIRECIONANDO PARA UM NOVO CADASTRO*/
		return modelAndView;
	}
}