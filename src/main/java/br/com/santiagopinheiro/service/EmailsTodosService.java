package br.com.santiagopinheiro.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import br.com.santiagopinheiro.model.Cota;
import br.com.santiagopinheiro.model.CotaParticipante;
import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.model.Usuario;
import jakarta.annotation.PreDestroy;

@Service
public class EmailsTodosService {

	private final EmailService emailService;
	private final WhatsAppService whatsAppService;

	// Cria um pool com 5 threads
	// Executor service é melhor do que thread porque controlo de quantos em quantos
	// os emails irão
	private final ExecutorService executor = Executors.newFixedThreadPool(5);

	public EmailsTodosService(EmailService emailService,
			WhatsAppService whatsAppService) {
		this.emailService = emailService;
		this.whatsAppService = whatsAppService;
	}

	private void enviar(String email, String titulo, String mensagem) {

		try {
			String assunto = "Notificação - Consórcio Família Pinheiro";

			String textoBotao = "Acessar o sistema";

			String linkBotao = "https://santiagosistemas.com.br";

			emailService.enviarEmailHtml(email, assunto, titulo, mensagem, textoBotao, linkBotao);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

	public void emailCadastroNovo(String email) {

		String titulo = "Bem vindo ao Consórcio da Família Pinheiro!";

		String mensagem = """
				Seu cadastro foi realizado com sucesso!
				
				É um prazer ter você conosco.
				
				Você agora faz parte do melhor consórcio do Brasil.
				
				

				Agora você já pode acessar o sistema e acompanhar
				suas cotas, parcelas e informações do consórcio.
				""";
		
		executor.submit(() -> {
			enviar(email, titulo, mensagem);
		});

	}
	
	

	public void emailCadastroAtualizado(String email) {

		String titulo = "Seu Cadastro foi atualizado com sucesso!";
		String mensagem = """
				Seu cadastro foi atualizado.

				Acesse o sistema, e confira seus dados na opção USUÁRIOS.

				Acompanhe suas cotas e contemplações na opção CONSÓRCIO

				Veja o Extrato Financeiro de todo o grupo!

				""";
		
		executor.submit(() -> {
			enviar(email, titulo, mensagem);
		});
		

	}

	public void emailNovaNotificacao(List<Usuario> usuarios) {
		
		if(usuarios.isEmpty()) return;

		String titulo = "Você tem uma nova notificação";
		String mensagem = """
				    Você tem uma nova notificação!

				Acesse o sistema e confira as novidades. Fique sempre por dentro de tudo o que acontece no nosso grupo!

			    E tem mais!

				Você também pode enviar notificações personalizadas para os demais participantes do grupo.

				É muito simples:

				1. Acesse o sistema.
				2. Entre na área de -> Notificações.
				3. Clique em -> Ver todas as notificações.
				4. Utilize a opção de -> enviar uma nova notificação.

				Assim, você poderá compartilhar avisos, informações e mensagens importantes com todos os membros do grupo.

				Consórcio Família Pinheiro — informação e comunicação sempre em dia!

				         """;

		for (Usuario u : usuarios) {

			executor.submit(() -> {

				enviar(u.getEmail(), titulo, mensagem);

			});

		}

	}

	
	
	
	
	
	public void emailParcelaPaga(String email, String nome, LocalDate dtVencimento, double valor) {
		
		String dataFormatada = dtVencimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		
		NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
		formato.setMinimumFractionDigits(2);
		formato.setMaximumFractionDigits(2);
		formato.setMinimumIntegerDigits(1);
		
		String valorFormatado = formato.format(valor);
		
		String titulo = "Consórcio - Registramos o pagamento da sua cota.";
		String mensagem = """
				
				Olá, %s
				
				Informamos que nesta data registramos o pagamento da sua cota consórcio, com vencimento em: %s
				
				Valor pago: %s
				
				
				Mantenha suas mensalidades sempre em dia.
				

				-----
				Acesse o sistema e confira o extrato financeiro do grupo. 
				
				Fique sempre por dentro de tudo o que acontece.

				Consórcio Família Pinheiro — informação e comunicação sempre em dia!

				         """.formatted(nome, dataFormatada, valorFormatado);
		executor.submit(() -> {
			enviar(email, titulo, mensagem);
		});
		
	}
	
	
	
	public void emailParcelaProximaVencimento(List<Parcela> parcelas) {
		
		if (parcelas.isEmpty()) {
			return;
		}
		
		String titulo = "Atenção a data de vencimento da sua parcela está próxima.";
		
		Parcela parcela = parcelas.get(0);
		
		String dataVencimento = parcela.getDtVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		for(Parcela p : parcelas) {
			
			String mensagem = """
					Olá, %s
					
					Informamos que a data de vencimento da sua parcela está próxima.
					
					Mantenha seu consórcio em dia.
					
					Não esqueça, sua parcela vence em: %s
					
					Pague o quanto antes.
					
					
					O consórcio da Família Pinheiro é o melhor do Brasil.
					
					
					-----
					Acesse o sistema:
					   Consulte suas notificações e
					   Confira seu extrato financeiro.
					
					Não perca nenhuma novidade.
					
					""".formatted(p.getUsuario().getNome(), dataVencimento);
			
			String email = p.getUsuario().getEmail();
			
			executor.submit(() -> {
				enviar(email, titulo, mensagem);
			});
		
		}//for
		
		
	}//emailParcelaProximaVencimento
	
	
	
public void emailParcelaVenceHoje(List<Parcela> parcelas) {
		
		if (parcelas.isEmpty()) {
			return;
		}
		
		String titulo = "Atenção a parcela do seu consórcio vence hoje!";
		
		Parcela parcela = parcelas.get(0);
		
		String dataVencimento = parcela.getDtVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		for(Parcela p : parcelas) {
			
			String mensagem = """
					Olá, %s
					
					Informamos que a data de vencimento da sua parcela chegou.
					
					Faça o pix. Fique em dia. 😁
					
					Mantenha seu consórcio adimplente. Evite a visita do agiota. 😱
					
					Vencimento da parcela em: %s
					
					Pague o quanto antes.
					
					
					O consórcio da Família Pinheiro é o melhor do Brasil.
					
					
					-----
					Acesse o sistema:
					   Consulte suas notificações e
					   Confira seu extrato financeiro.
					
					Não perca nenhuma novidade.
					
					""".formatted(p.getUsuario().getNome(), dataVencimento);
			
			String email = p.getUsuario().getEmail();
			
			executor.submit(() -> {
				enviar(email, titulo, mensagem);
			});
		
		}//for
		
		
	}//emailParcelaVenceHoje
	
	
	
	
	
	public void emailParcelaAtrasada(Parcela parcela) {
		
		String nome;
		
		String dataVencimento = parcela.getDtVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		String titulo = "Aviso de parcela em atraso!";
		
			nome = parcela.getUsuario().getNome();
			
			String email = parcela.getUsuario().getEmail();
			
			if(email.isEmpty()) return;
			
			String mensagem = """
					
					Olá, %s
					
					Estamos tristes...
					
					Sua parcela venceu em %s e você esqueceu de pagar 😭
					
					
					A polícia federal muito em breve estará na sua porta. 🚓
					Normalmente eles chegam falando que são do Mercado Livre. Prepare-se. 🏃‍➡️
					
					
					ou...
					aproveite que não cobramos juros de atraso, e pague agora!!! 🥰
					
					Fique em dia com o melhor consórcio do Brasil. 
					
					
					
					Caso já tenha feito o pagamento por favor desconsiderar essa mensagem. 🫣
					
					Qualquer problema avise nossas administradores Monique e Eveline. 
					
					
					
					-----
					Acesse o sistema:
					   Consulte suas notificações e
					   Confira seu extrato financeiro.
					
					Não perca nenhuma novidade.
					
					""".formatted(nome, dataVencimento);
			
			
			executor.submit(() -> {
				enviar(email, titulo, mensagem);
			});
			
	}
	
	
	
	public void emailContemplacao(Cota cota, List<CotaParticipante> cotaParticipantes) {
		
		int numeroCota = cota.getNumeroCota();
		
		String titulo = "PARABÉNS SUA COTA FOI CONTEMPLADA!!!";
		
		for(CotaParticipante cp : cotaParticipantes) {
			
			int percentual =cp.getPercentualParticipacao().intValue();
			
			try {
				/* ENVIO DE WHATSAPP */
				String telefone = cp.getUsuario().getTelefone();
				whatsAppService.notificaContemplacao(telefone);
				/* ENVIO DE WHATSAPP */
			}catch(Exception e) {
				e.printStackTrace();
				System.out.println("Falha ao enviar WhatsApp de contemplação.");
			}
			
			
			String nome = cp.getUsuario().getNome();
			
			String email = cp.getUsuario().getEmail();
			
			String mensagem = 
				"""
				🎉 PARABÉNS! SUA COTA FOI CONTEMPLADA! 🎉
				
				%s,

				A sua cota de número: %s acaba de ser CONTEMPLADA! 🏆

                -> Você possui %s%% de participação nesta cota.

				Chegoua a hora de celebrar essa conquista e realizar seus sonhos!


                📞 E agora, como receber seu prêmio?

                Entre em contato com nossa administradora Eveline para receber as orientações e dar início ao processo de resgate do seu valor.
                
                

                🥇 CONSÓRCIO FAMÍLIA PINHEIRO

                Aqui, os sonhos saem do papel e as conquistas acontecem!
                
                Somos o consórcio que mais contempla em todo o Brasil!

                Parabéns por essa conquista! Que seja a primeira de muitas! 🎊

                """.formatted(nome,numeroCota, percentual);
			
			executor.submit(() -> {
				
				enviar(email, titulo, mensagem);
				
			});
		}
		
	}//emailContemplacao
	
	
	
	public void emailNotificaTodosContemplacao(List<Usuario> usuarios) {
		
		String titulo = "Notificação de nova assembléia.";
		String mensagem = """
				🎉 Temos novidades!

				Uma nova assembleia foi realizada no seu grupo! 
				
				Acesse o sistema e descubra quem foi o felizardo desta vez! 💰🍀

				Boa sorte a todos! 🤞🍀
				""";
		
		executor.submit(() -> {
			for(Usuario u : usuarios) {
				enviar(u.getEmail(), titulo, mensagem);
			}
		});
		
	}//emailNotificaTodosContemplacao
	
	
	// Encerra o ExecutorService quando a aplicação for desligada
		@PreDestroy
		public void finalizarExecutor() {

			executor.shutdown();

		}

}
