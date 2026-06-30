package br.com.ikonbrasil.auth.compartilhado.infraestrutura.excecao;

import java.time.LocalDateTime;

public record RespostaErro(LocalDateTime dataHora, int status, String erro, String mensagem, String caminho) {
}
