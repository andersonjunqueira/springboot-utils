package br.com.ertic.util.infraestructure.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
//    @ExceptionHandler(value = NegocioException.class)
//    public ExceptionDTO handleBaseException(NegocioException ex){
//        return tratamentoErro(ex);
//    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(value = InternalException.class)
//    public ExceptionDTO handleMailException(InternalException ex){
//        return tratamentoErro(ex);
//    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(value = Exception.class)
//    public ExceptionDTO handleException(Exception ex){
//        return tratamentoErro(ex);
//    }
//
//    private ExceptionDTO tratamentoErro(Exception ex) {
//        ExceptionDTO erro = new ExceptionDTO();
//        erro.setMensagem(ex.getMessage());
//        erro.setPilha(getStackTrace(ex));
//        return erro;
//    }
//
//    private String getStackTrace(Throwable ex) {
//        StringWriter trace = new StringWriter();
//        PrintWriter pw = new PrintWriter(trace);
//        ex.printStackTrace(pw);
//        return trace.toString();
//    }

}