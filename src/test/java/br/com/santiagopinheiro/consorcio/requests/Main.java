package br.com.santiagopinheiro.consorcio.requests;

import java.time.LocalDate;

public class Main {

	public static void main(String[] args) {
		
		LocalDate hoje = LocalDate.of(2026, 11, 15);
		
		LocalDate inicioMes = hoje.withDayOfMonth(1);
		
		LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
		
		System.out.println("Hoje: " + hoje);
		System.out.println("Dia 01: " + inicioMes);
		System.out.println("Ultimo dia: " + fimMes);
		System.out.println("Falta para acabar mes: " + hoje.lengthOfMonth());
		
		
	}

}
