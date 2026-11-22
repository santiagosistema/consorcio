package br.com.santiagopinheiro.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.santiagopinheiro.model.Notificacao;
import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.repository.NotificacaoRepository;
import br.com.santiagopinheiro.repository.ParcelaRepository;

@Service
public class NotificacaoService {

    private final EmailsTodosService emailsTodosService;
	private final NotificacaoRepository notificacaoRepository;
    private final ParcelaRepository parcelaRepository;
    private final WhatsAppService whats;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository,
            ParcelaRepository parcelaRepository, 
            EmailsTodosService emailsTodosService,
            WhatsAppService whats) {

        this.notificacaoRepository = notificacaoRepository;
        this.parcelaRepository = parcelaRepository;
		this.emailsTodosService = emailsTodosService;
		this.whats = whats;
    }

    @Transactional
    public void verificarParcelasAtrasadas() {

        LocalDate hoje = LocalDate.now();

        // Atualiza a situação das parcelas vencidas.
        parcelaRepository.atualizarParcelasVencidas(hoje);

        // Busca parcelas vencidas que ainda não foram pagas.
        List<Parcela> parcelas = parcelaRepository
                .findByDtVencimentoBeforeAndDtPagamentoIsNullOrderByDtVencimentoAsc(
                        hoje);
        

        for (Parcela parcela : parcelas) {

            if (parcela.getUsuario() == null
                    || parcela.getUsuario().getId() == null) {
                continue;
            }

            Long usuarioId = parcela.getUsuario().getId();
            Long parcelaId = parcela.getId();

            // Evita criar outra notificação para a mesma parcela e usuário.
            if (notificacaoRepository.existsByParcela_IdAndUsuario_Id(
                    parcelaId, usuarioId)) {
                continue;
            }

            Notificacao notificacao = new Notificacao();

            notificacao.setUsuario(parcela.getUsuario());
            notificacao.setGrupo(parcela.getGrupo());
            notificacao.setParcela(parcela);

            notificacao.setTitulo("Parcela em atraso");

            String vencimento = parcela.getDtVencimento()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            notificacao.setMensagem(
                    "A parcela " + parcela.getNumeroParcela()
                    + " está em atraso desde " + vencimento + ".");

            notificacao.setLida(false);
            notificacao.setDataCriacao(hoje);
            notificacao.setUrlDestino("/parcelas");
            
            
            //envia email
        	emailsTodosService.emailParcelaAtrasada(parcela);
            
            //envia whatsapp
            String telefone = "55" + parcela.getUsuario().getTelefone();
            String nome = parcela.getUsuario().getNome().trim().split("\\s+")[0];
            String data = parcela.getDtVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            
            String valor = nf.format(parcela.getValorDevido());
            
            whats.enviarLembrete(telefone, nome, data, valor);
            

            notificacaoRepository.save(notificacao);
        }
    }
    

    @Transactional(readOnly = true)
    public List<Notificacao> buscarNotificacoesDoUsuario(Long usuarioId) {
        return notificacaoRepository
                .findByUsuario_IdOrderByDataCriacaoDesc(
                        usuarioId);
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return notificacaoRepository
                .countByUsuario_IdAndLidaFalse(usuarioId);
    }

    @Transactional
    public void marcarComoLida(Long notificacaoId, Long usuarioId) {

        Notificacao notificacao = notificacaoRepository
                .findById(notificacaoId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notificação não encontrada."));

        // Impede marcar a notificação de outro usuário como lida.
        if (notificacao.getUsuario() == null
                || !notificacao.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException(
                    "Você não tem permissão para alterar esta notificação.");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }
    
    
    @Transactional
    public int marcarTodasComoLidas(Long usuarioId) {
        return notificacaoRepository.marcarTodasComoLidas(usuarioId);
    }
    
    @Transactional(readOnly = true)
    public Notificacao buscarNotificacaoPorId(Long notificacaoId) {
        return notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Notificação não encontrada."));
    }
    
    
    @Transactional
    public void excluirNotificacaoDoUsuario(Long notificacaoId, Long usuarioId) {

        Notificacao notificacao = notificacaoRepository
            .findByIdAndUsuario_Id(notificacaoId, usuarioId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Notificação não encontrada ou não pertence ao usuário."
            ));

        notificacaoRepository.delete(notificacao);
    }
    
    
    @Transactional
    public void parcelasProximasDoVencimento() {
    	
    	// define inicio e fim do mês atual
    	LocalDate hoje = LocalDate.now();
    			
    	LocalDate inicioMes = hoje.withDayOfMonth(1);
    	
    	LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
    	

    	// captura as parcelas do mês que não foram pagas
    	List<Parcela> parcelas = parcelaRepository.findParcelasDoMes(inicioMes, fimMes);
    	
    	int diaAtual = hoje.getDayOfMonth();
    	
    	if(!parcelas.isEmpty()) {
    		
    		if (diaAtual == 5) {
    			emailsTodosService.emailParcelaVenceHoje(parcelas);
    		}else if (diaAtual < 5 && diaAtual >= 1){
    			emailsTodosService.emailParcelaProximaVencimento(parcelas);
    		}else if(diaAtual > 5 && diaAtual <= 31) {
    			
    		}
    	}
    	
    }//parcelasProximasDoVencimento
}