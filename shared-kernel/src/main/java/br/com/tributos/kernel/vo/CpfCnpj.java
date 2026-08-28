package br.com.tributos.kernel.vo;

import java.util.Objects;

import br.com.tributos.kernel.exception.ValidationException;

/**
 * Documento de identificação PF (CPF, 11 dígitos) ou PJ (CNPJ, 14 dígitos), sempre
 * normalizado para somente números — evita duplicata por formatação diferente no banco.
 */
public final class CpfCnpj {

    private final String apenasDigitos;
    private final Tipo tipo;

    public enum Tipo {
        CPF, CNPJ
    }

    private CpfCnpj(String apenasDigitos, Tipo tipo) {
        this.apenasDigitos = apenasDigitos;
        this.tipo = tipo;
    }

    public static CpfCnpj de(String valor) {
        String digitos = normalizar(valor);
        if (digitos.length() == 11) {
            validarCpf(digitos);
            return new CpfCnpj(digitos, Tipo.CPF);
        }
        if (digitos.length() == 14) {
            validarCnpj(digitos);
            return new CpfCnpj(digitos, Tipo.CNPJ);
        }
        throw new ValidationException("CPF deve ter 11 dígitos e CNPJ 14 dígitos (apenas números).");
    }

    public static String normalizar(String valor) {
        if (valor == null) {
            throw new ValidationException("Informe o CPF ou CNPJ.");
        }
        return valor.replaceAll("\\D", "");
    }

    public Tipo tipo() {
        return tipo;
    }

    public String apenasDigitos() {
        return apenasDigitos;
    }

    private static void validarCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            throw new ValidationException("CPF inválido.");
        }
        int digito1 = calcularDigitoCpf(cpf, 9, 10);
        int digito2 = calcularDigitoCpf(cpf, 10, 11);
        if (digito1 != Character.getNumericValue(cpf.charAt(9))
            || digito2 != Character.getNumericValue(cpf.charAt(10))) {
            throw new ValidationException("CPF inválido.");
        }
    }

    private static int calcularDigitoCpf(String cpf, int tamanho, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static void validarCnpj(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) {
            throw new ValidationException("CNPJ inválido.");
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int digito1 = calcularDigitoCnpj(cnpj, pesos1);
        int digito2 = calcularDigitoCnpj(cnpj, pesos2);
        if (digito1 != Character.getNumericValue(cnpj.charAt(12))
            || digito2 != Character.getNumericValue(cnpj.charAt(13))) {
            throw new ValidationException("CNPJ inválido.");
        }
    }

    private static int calcularDigitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof CpfCnpj cpfCnpj)) return false;
        return apenasDigitos.equals(cpfCnpj.apenasDigitos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apenasDigitos);
    }

    @Override
    public String toString() {
        return apenasDigitos;
    }
}
