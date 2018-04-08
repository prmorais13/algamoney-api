package br.paulo.apicurso.mail;

//import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender mailSender;
	
	/*@EventListener
	private void teste(ApplicationReadyEvent event) {
		this.enviarEmail("prmorais1302@gmail.com",
						 Arrays.asList("prmorais_13@hotmail.com"),
						 "Testando algamoney",
						 "Olá!<br/>Teste. Ok");
		
		System.out.println("E-mail enviado com sucesso.");
	}*/
	
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
