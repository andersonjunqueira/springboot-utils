package br.com.ertic.util.infraestructure.service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import br.com.ertic.util.infraestructure.exception.EmailException;

public class SendMailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviar(String fromName, String from, String assunto, String mensagem, String... destinatarios) throws EmailException {

        InternetAddress[] dests = new InternetAddress[destinatarios.length];
        for(int i=0; i<destinatarios.length; i++) {

            try {
                dests[i] = new InternetAddress(destinatarios[i]);
            } catch (AddressException e) {
                throw new EmailException("email-invalido", e);
            }

        }
        enviar(fromName, from, assunto, mensagem, dests);

    }

    public void enviar(String fromName, String from, String assunto, String mensagem, InternetAddress... destinatarios) throws EmailException {

        MimeMessage mail = mailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            helper.setFrom(new InternetAddress(from, fromName));
            helper.setTo(destinatarios);
            helper.setSubject(assunto);
            helper.setText("", mensagem);
            //helper.addInline("logo", new ClassPathResource("/img/logo-email.png"));

        } catch (Exception e) {
            throw new EmailException("Erro na preparação do e-mail", e);
        }

        mailSender.send(mail);
    }


}
