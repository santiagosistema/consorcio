
package br.com.santiagopinheiro.controller.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.santiagopinheiro.dto.CotaDistribuicaoDTO;
import br.com.santiagopinheiro.dto.ParticipanteCotaDTO;
import br.com.santiagopinheiro.dto.SalvarDistribuicaoDTO;
import br.com.santiagopinheiro.model.Cota;
import br.com.santiagopinheiro.model.CotaParticipante;
import br.com.santiagopinheiro.model.Grupo;
import br.com.santiagopinheiro.model.Parcela;
import br.com.santiagopinheiro.model.Usuario;
import br.com.santiagopinheiro.repository.CotaParticipanteRepository;
import br.com.santiagopinheiro.repository.CotaRepository;
import br.com.santiagopinheiro.repository.GrupoRepository;
import br.com.santiagopinheiro.repository.ParcelaRepository;
import br.com.santiagopinheiro.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/cota")
public class CotaRestController {

    private final CotaRepository cotaRepository;
    private final CotaParticipanteRepository cotaParticipanteRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParcelaRepository parcelaRepository;

    public CotaRestController(
            CotaRepository cotaRepository,
            CotaParticipanteRepository cotaParticipanteRepository,
            GrupoRepository grupoRepository,
            UsuarioRepository usuarioRepository,
            ParcelaRepository parcelaRepository) {

        this.cotaRepository = cotaRepository;
        this.cotaParticipanteRepository = cotaParticipanteRepository;
        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
        this.parcelaRepository = parcelaRepository;
    }

    @PostMapping("/salvar-distribuicao")
    @Transactional
    public ResponseEntity<?> salvarDistribuicao(
            @RequestBody SalvarDistribuicaoDTO dados) {
    	


        // 1. Validar os dados básicos
        if (dados == null
                || dados.grupoId() == null
                || dados.cotas() == null
                || dados.cotas().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body("Informe o grupo e pelo menos uma cota.");
        }

        Grupo grupo = grupoRepository.findById(dados.grupoId())
                .orElse(null);

        if (grupo == null) {
            return ResponseEntity.badRequest()
                    .body("Grupo não encontrado.");
        }
        
     // Impede uma nova distribuição para um grupo já distribuído
        if (cotaRepository.existsByGrupo_Id(grupo.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este grupo já possui cotas distribuídas. "
                            + "Não é permitido distribuir novamente.");
        }

        if (grupo.getDataInicio() == null) {
            return ResponseEntity.badRequest()
                    .body("O grupo precisa ter uma data de início.");
        }

        int totalCotas = grupo.getTotalCotas();

        if (totalCotas <= 0) {
            return ResponseEntity.badRequest()
                    .body("O grupo precisa ter um total de cotas válido.");
        }

        BigDecimal valorCota =
                BigDecimal.valueOf(grupo.getValorCota());

        if (valorCota.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest()
                    .body("O valor da cota precisa ser positivo.");
        }

        // 2. Validar todas as cotas antes de gravar qualquer registro
        Set<Integer> numerosInformados = new HashSet<>();
        Map<Long, Usuario> usuariosValidados = new HashMap<>();

        for (CotaDistribuicaoDTO item : dados.cotas()) {

            if (item == null || item.numeroCota() == null) {
                return ResponseEntity.badRequest()
                        .body("Informe o número de cada cota.");
            }

            Integer numero = item.numeroCota();

            if (numero < 1 || numero > totalCotas) {
                return ResponseEntity.badRequest()
                        .body("O número da cota deve estar entre 1 e "
                                + totalCotas + ".");
            }

            if (!numerosInformados.add(numero)) {
                return ResponseEntity.badRequest()
                        .body("O número da cota " + numero
                                + " foi informado mais de uma vez.");
            }

            if (cotaRepository.existsByGrupo_IdAndNumeroCota(
                    grupo.getId(), numero)) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A cota " + numero
                                + " já está cadastrada neste grupo.");
            }

            if (item.participantes() == null
                    || item.participantes().isEmpty()) {

                return ResponseEntity.badRequest()
                        .body("A cota " + numero
                                + " precisa ter pelo menos um participante.");
            }

            Set<Long> usuariosNaCota = new HashSet<>();
            BigDecimal somaPercentuais = BigDecimal.ZERO;

            for (ParticipanteCotaDTO participante
                    : item.participantes()) {

                if (participante == null
                        || participante.usuarioId() == null
                        || participante.percentual() == null) {

                    return ResponseEntity.badRequest()
                            .body("Informe o usuário e o percentual "
                                    + "de cada participante.");
                }

                Long usuarioId = participante.usuarioId();
                BigDecimal percentual = participante.percentual();

                if (!usuariosNaCota.add(usuarioId)) {
                    return ResponseEntity.badRequest()
                            .body("O mesmo usuário não pode ser repetido "
                                    + "na cota " + numero + ".");
                }

                if (percentual.compareTo(BigDecimal.ZERO) <= 0
                        || percentual.compareTo(
                                new BigDecimal("100")) > 0) {

                    return ResponseEntity.badRequest()
                            .body("O percentual deve ser maior que zero "
                                    + "e menor ou igual a 100.");
                }

                if (percentual.stripTrailingZeros().scale() > 2) {
                    return ResponseEntity.badRequest()
                            .body("Use no máximo duas casas decimais "
                                    + "nos percentuais.");
                }

                somaPercentuais = somaPercentuais.add(percentual);

                Usuario usuario = usuariosValidados.get(usuarioId);

                if (usuario == null) {
                    usuario = usuarioRepository.findById(usuarioId)
                            .orElse(null);

                    if (usuario == null || !usuario.getSituacao()) {
                        return ResponseEntity.badRequest()
                                .body("Usuário inexistente ou inativo: "
                                        + usuarioId);
                    }

                    usuariosValidados.put(usuarioId, usuario);
                }
            }

            if (somaPercentuais.compareTo(
                    new BigDecimal("100")) != 0) {

                return ResponseEntity.badRequest()
                        .body("Os percentuais da cota " + numero
                                + " devem somar exatamente 100%. "
                                + "Total informado: " + somaPercentuais + "%.");
            }
        }

        // 3. Criar as cotas, seus participantes e as parcelas
        List<Parcela> parcelas = new ArrayList<>();

        for (CotaDistribuicaoDTO item : dados.cotas()) {

            Cota cota = new Cota();
            cota.setGrupo(grupo);
            cota.setNumeroCota(item.numeroCota());
            cota.setSituacaoCota("ativa");

            cota = cotaRepository.save(cota);

            for (ParticipanteCotaDTO dadosParticipante
                    : item.participantes()) {

                Usuario usuario =
                        usuariosValidados.get(dadosParticipante.usuarioId());

                BigDecimal percentual =
                        dadosParticipante.percentual();

                CotaParticipante participante =
                        new CotaParticipante();

                participante.setCota(cota);
                participante.setUsuario(usuario);
                participante.setPercentualParticipacao(percentual);
                participante.setGrupo(grupo);

                participante =
                        cotaParticipanteRepository.save(participante);

                // Valor mensal correspondente à participação
                BigDecimal valorParticipante = valorCota
                        .multiply(percentual)
                        .divide(
                                new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP);

                // Gerar as parcelas do participante
                for (int i = 0; i < totalCotas; i++) {

                    Parcela parcela = new Parcela();

                    parcela.setGrupo(grupo);
                    parcela.setUsuario(usuario);
                    parcela.setCotaParticipante(participante);
                    parcela.setNumeroParcela(i + 1);

                    parcela.setValorDevido(
                            valorParticipante.doubleValue());

                    LocalDate vencimento =
                            grupo.getDataInicio().plusMonths(i);

                    parcela.setDtVencimento(vencimento);
                    parcela.setSituacao("em aberto");

                    parcelas.add(parcela);
                }
            }
        }

        parcelaRepository.saveAll(parcelas);

        return ResponseEntity.ok(
                "Distribuição de cotas salva com sucesso. "
                + "Cotas cadastradas: " + dados.cotas().size()
                + ". Participantes: "
                + dados.cotas().stream()
                        .mapToInt(c -> c.participantes().size())
                        .sum()
                + ". Parcelas geradas: " + parcelas.size() + ".");
    }
}
