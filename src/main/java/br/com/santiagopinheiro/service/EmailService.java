package br.com.santiagopinheiro.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

	@Service
	public class EmailService {

	    private final JavaMailSender mailSender;

	    public EmailService(JavaMailSender mailSender) {
	        this.mailSender = mailSender;
	    }

	    public void enviarEmailHtml(
	            String destinatario,
	            String assunto,
	            String titulo,
	            String mensagem,
	            String textoBotao,
	            String linkBotao) throws Exception {

	        MimeMessage email = mailSender.createMimeMessage();

	        MimeMessageHelper helper =
	                new MimeMessageHelper(email, true, "UTF-8");

	        helper.setFrom(
	            "Consórcio Família Pinheiro <mspneto9@gmail.com>"
	        );

	        helper.setTo(destinatario);
	        helper.setSubject(assunto);

	        String html = montarHtml(
	            titulo,
	            mensagem,
	            textoBotao,
	            linkBotao
	        );

	        // true indica que o conteúdo é HTML
	        helper.setText(html, true);

	        mailSender.send(email);
	    }
	    
	    

	    private String montarHtml(
	            String titulo,
	            String mensagem,
	            String textoBotao,
	            String linkBotao) {

	        String botao = "";

	        if (textoBotao != null && linkBotao != null
	                && !textoBotao.isBlank()
	                && !linkBotao.isBlank()) {

	            botao = """
	                <tr>
	                    <td align="center" style="padding: 25px 0;">
	                        <a href="%s"
	                           style="
	                             display:inline-block;
	                             background-color:#1d4ed8;
	                             color:#ffffff;
	                             text-decoration:none;
	                             padding:14px 30px;
	                             border-radius:7px;
	                             font-weight:bold;
	                             font-size:15px;">
	                            %s
	                        </a>
	                    </td>
	                </tr>
	                """.formatted(
	                    escaparHtml(linkBotao),
	                    escaparHtml(textoBotao)
	                );
	        }

	        return """
	            <!DOCTYPE html>
	            <html lang="pt-BR">
	            <head>
	                <meta charset="UTF-8">
	                <meta name="viewport"
	                      content="width=device-width, initial-scale=1.0">
	            </head>

	            <body style="
	                margin:0;
	                padding:0;
	                background-color:#f1f5f9;
	                font-family:Arial,Helvetica,sans-serif;">

	                <table width="100%%"
	                       cellpadding="0"
	                       cellspacing="0"
	                       border="0"
	                       style="background-color:#f1f5f9;padding:30px 10px;">

	                    <tr>
	                        <td align="center">

	                            <table width="600"
	                                   cellpadding="0"
	                                   cellspacing="0"
	                                   border="0"
	                                   style="
	                                     max-width:600px;
	                                     width:100%%;
	                                     background:#ffffff;
	                                     border-radius:12px;
	                                     overflow:hidden;">

	                                <!-- CABEÇALHO -->
	                                <tr>
	                                    <td align="center"
	                                        style="
	                                          background:#1e3a8a;
	                                          padding:30px 20px;">

	                                        <h1 style="
	                                            color:#ffffff;
	                                            font-size:23px;
	                                            margin:0;">
	                                            CONSÓRCIO
	                                        </h1>

	                                        <p style="
	                                            color:#bfdbfe;
	                                            font-size:15px;
	                                            margin:8px 0 0;">
	                                            Família Pinheiro
	                                        </p>

	                                    </td>
	                                </tr>

	                                <!-- CONTEÚDO -->
	                                <tr>
	                                    <td style="padding:35px 30px;">

	                                        <h2 style="
	                                            color:#1e293b;
	                                            font-size:22px;
	                                            margin:0 0 20px;">
	                                            %s
	                                        </h2>

	                                        <p style="
	                                            color:#475569;
	                                            font-size:15px;
	                                            line-height:1.8;
	                                            margin:0;">
	                                            %s
	                                        </p>

	                                    </td>
	                                </tr>

	                                <!-- BOTÃO -->
	                                %s

	                                <!-- RODAPÉ -->
	                                <tr>
	                                    <td align="center"
	                                        style="
	                                          background:#f8fafc;
	                                          padding:25px 20px;
	                                          border-top:1px solid #e2e8f0;">

	                                        <p style="
	                                            color:#64748b;
	                                            font-size:13px;
	                                            margin:0 0 8px;">
	                                            Consórcio Família Pinheiro
	                                        </p>

	                                        <p style="
	                                            color:#94a3b8;
	                                            font-size:11px;
	                                            margin:0;">
	                                            Mensagem automática enviada
	                                            pelo sistema.
	                                            Não responda este e-mail.
	                                        </p>

	                                    </td>
	                                </tr>

	                            </table>

	                        </td>
	                    </tr>

	                </table>

	            </body>
	            </html>
	            """.formatted(
	                escaparHtml(titulo),
	                escaparHtml(mensagem).replace("\n", "<br>"),
	                botao
	            );
	    }

	    private String escaparHtml(String texto) {

	        return texto
	            .replace("&", "&amp;")
	            .replace("<", "&lt;")
	            .replace(">", "&gt;")
	            .replace("\"", "&quot;")
	            .replace("'", "&#39;");
	    }

}
