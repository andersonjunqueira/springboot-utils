package br.com.ertic.util.infraestructure.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static ExceptionDTO tratamentoErro(Exception ex) {
        ExceptionDTO erro = new ExceptionDTO();
        erro.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR);
        erro.setMensagem(ex.getMessage());
        erro.setPilha(getStackTrace(ex));

        if(ex instanceof NegocioException) {
            erro.setErrorCode(HttpStatus.UNPROCESSABLE_ENTITY);

        } else if(ex instanceof RequisicaoInvalidaException) {
            erro.setErrorCode(HttpStatus.BAD_REQUEST);
        }

        return erro;
    }

    public static String getStackTrace(Throwable ex) {
        StringWriter trace = new StringWriter();
        PrintWriter pw = new PrintWriter(trace);
        ex.printStackTrace(pw);
        return trace.toString();
    }

}