package br.com.iwstech.util.infraestructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Anderson Junqueira, acosorio@stefanini.com
 */
@ResponseStatus(value=HttpStatus.CONFLICT)
public class RegistroExistenteException extends NegocioException {

    private static final long serialVersionUID = 154640526844799318L;

    /**
     * Construtor padrão da exceção.
     * @param statusCode código de erro HTTP
     */
    public RegistroExistenteException() {
        super();
    }

    /**
     * Construtor onde pode ser informada a mensagem a ser apresentada.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     */
    public RegistroExistenteException(String msg) {
        super(msg);
    }

    /**
     * Construtor onde pode ser informada a causa da exceção.
     * @param statusCode código de erro HTTP
     * @param cause causa origem da exceção lançada
     */
    public RegistroExistenteException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor onde pode ser informada a causa e a mensagem da exceção.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     * @param cause causa origem da exceção lançada
     */
    public RegistroExistenteException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
