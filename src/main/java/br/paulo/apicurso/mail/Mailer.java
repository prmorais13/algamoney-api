package br.paulo.apicurso.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.paulo.apicurso.model.Lancamento;
import br.paulo.apicurso.model.Usuario;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
//	@Autowired
//	private Lancamentos repo;
//	
//	@EventListener
//	private void teste(ApplicationReadyEvent event) {
//		String template = "mail/aviso-lancamentos-vencidos";
//		
//		
//		List<Lancamento> lista = repo.findAll();
//		
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put("lancamentos", lista);
//			
//		this.enviarEmail("prmorais1302@gmail.com",
//						 Arrays.asList("prmorais_13@hotmail.com"),
//						 "Testando algamoney",
//						 template, variaveis);
//		
//		System.out.println("E-mail enviado com sucesso.");
//	}
	
	public void avisarLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> destinatarios) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = destinatarios.stream()
			.map(u -> u.getEmail())
			.collect(Collectors.toList());
		
		this.enviarEmail("prmorais1302@gmail.com",
				emails,
				"Lancamentos vencidos",
				"mail/aviso-lancamentos-vencidos",
				variaveis);
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios,
			String assunto, String template, Map<String, Object> variaveis) {
		
		Context context = new Context(new Locale("pt", "BR"));
		
		variaveis.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem = this.thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, destinatarios, assunto, mensagem);
	}
	
	public void enviarEmail(String remetente, List<String> destinatarios,
			String assunto, String mensagem) {
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			throw new RuntimeException("Erro no envio de e-mail", e);
		}
		
	}

}
