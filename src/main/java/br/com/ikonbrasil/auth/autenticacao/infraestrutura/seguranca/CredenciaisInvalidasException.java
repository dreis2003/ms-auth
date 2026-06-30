package br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais invalidas");
    }
}
