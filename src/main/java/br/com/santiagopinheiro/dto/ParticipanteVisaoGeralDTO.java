package br.com.santiagopinheiro.dto;

public class ParticipanteVisaoGeralDTO {
    private final String primeiroNome;
    private final long quantidadeCotas;
    private final boolean contemplado;

    public ParticipanteVisaoGeralDTO(String primeiroNome, long quantidadeCotas, boolean contemplado) {
        this.primeiroNome = primeiroNome;
        this.quantidadeCotas = quantidadeCotas;
        this.contemplado = contemplado;
    }

    public String getPrimeiroNome() { return primeiroNome; }
    public long getQuantidadeCotas() { return quantidadeCotas; }
    public boolean isContemplado() { return contemplado; }
}
