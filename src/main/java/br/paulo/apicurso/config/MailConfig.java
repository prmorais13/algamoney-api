package br.paulo.apicurso.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import br.paulo.apicurso.config.property.ApiCursoProperty;

@Configuration
public class MailConfig {
	
	@Autowired
	private ApiCursoProperty property;
	
	@Bean
	public JavaMailSender javaEnvioEmail() {
		Properties propriedades = new Properties();
		propriedades.put("mail.transport.protocol", "smtp");
		propriedades.put("mail.smtp.auth", true);
		propriedades.put("mail.smtp.starttls.enable", true);
		propriedades.put("mail.smtp.connectiontimeout", 10000);
		
		JavaMailSenderImpl envioEmail = new JavaMailSenderImpl();
		envioEmail.setJavaMailProperties(propriedades);
		envioEmail.setHost(this.property.getMail().getHost());
		envioEmail.setPort(this.property.getMail().getPort());
		envioEmail.setUsername(this.property.getMail().getUsername());
		envioEmail.setPassword(this.property.getMail().getPassword());
		
		return envioEmail;
	}

}
