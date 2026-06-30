package br.com.ikonbrasil.auth.compartilhado.infraestrutura.excecao;

import br.com.ikonbrasil.auth.autenticacao.infraestrutura.seguranca.CredenciaisInvalidasException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorGlobalExcecoes {

    @ExceptionHandler(CredenciaisInvalidasException.class)
    ResponseEntity<RespostaErro> tratarCredenciaisInvalidas(CredenciaisInvalidasException excecao, HttpServletRequest request) {
        return resposta(HttpStatus.UNAUTHORIZED, excecao.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<RespostaErro> tratarArgumentoInvalido(IllegalArgumentException excecao, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, excecao.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<RespostaErro> tratarValidacao(MethodArgumentNotValidException excecao, HttpServletRequest request) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return resposta(HttpStatus.BAD_REQUEST, mensagem, request);
    }

    private static ResponseEntity<RespostaErro> resposta(HttpStatus status, String mensagem, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new RespostaErro(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                request.getRequestURI()
        ));
    }
}
