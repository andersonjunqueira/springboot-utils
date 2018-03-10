package br.com.iwstech.util.infraestructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <p>Super classe para as esceções lançados pelo framework.</p>
 *
 * <p>Essa classe prevê um atributo <strong>statusCode</strong> que deve ser definido em suas subclasses e representa
 * o código HTTP a ser repassado ao frontend. O tratamento de erros padrão da classe <strong>RestBase</strong>
 * utiliza esse atributo na preparação do Response retornado ao frontend.</p>
 *
 * @author Anderson Junqueira, acosorio@stefanini.com
 * @see <a href="https://httpstatuses.com/">https://httpstatuses.com/</a>
 * @since 1.0.0
 */
@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class EmailException extends InternalException {

    private static final long serialVersionUID = 7750277264481007108L;

    /**
     * Construtor padrão da exceção.
     * @param statusCode código de erro HTTP
     */
    public EmailException() {
        super();
    }

    /**
     * Construtor onde pode ser informada a mensagem a ser apresentada.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     */
    public EmailException(String msg) {
        super(msg);
    }

    /**
     * Construtor onde pode ser informada a causa da exceção.
     * @param statusCode código de erro HTTP
     * @param cause causa origem da exceção lançada
     */
    public EmailException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor onde pode ser informada a causa e a mensagem da exceção.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     * @param cause causa origem da exceção lançada
     */
    public EmailException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
