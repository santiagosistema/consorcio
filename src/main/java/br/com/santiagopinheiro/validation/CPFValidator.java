package br.com.santiagopinheiro.validation;

public class CPFValidator {

	public static boolean validaCPF(String cpf) {
		
		// checa se o CPF veio informado da tela
			if (cpf == null || cpf.isBlank()) {
				return false;
			}

			// limpa o CPF antes de salvar (deixa só numeros)
			cpf = cpf.replaceAll("\\D", "");
			cpf = cpf.replaceAll(".*(\\d{11}).*", "$1");

			if (cpf.length() != 11) {
				return false;
			}

			// rejeita cpf com digitos iguais
			if (cpf.matches("(\\d)\\1{10}")) {
				return false;
			}
		
		/* VALIDA DIGITO VERIFICADOR DO CPF  */

		    int soma = 0;
		    int resto;

		    // Calcula o primeiro dígito verificador
		    for (int i = 0; i < 9; i++) {
		        soma += (cpf.charAt(i) - '0') * (10 - i);
		    }

		    resto = (soma * 10) % 11;

		    if (resto == 10) {
		        resto = 0;
		    }

		    if (resto != cpf.charAt(9) - '0') {
		        return false;
		    }

		    // Calcula o segundo dígito verificador
		    soma = 0;

		    for (int i = 0; i < 10; i++) {
		        soma += (cpf.charAt(i) - '0') * (11 - i);
		    }

		    resto = (soma * 10) % 11;

		    if (resto == 10) {
		        resto = 0;
		    }

		
		    return resto == cpf.charAt(10) - '0';

	}
}
