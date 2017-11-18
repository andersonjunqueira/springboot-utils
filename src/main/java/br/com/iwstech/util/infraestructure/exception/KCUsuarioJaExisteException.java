package br.com.iwstech.util.infraestructure.exception;

/**
 * @author Anderson Junqueira, acosorio@stefanini.com
 */
public class KCUsuarioJaExisteException extends NegocioException {

    private static final long serialVersionUID = 154640526844799318L;

    /**
     * Construtor padrão da exceção.
     * @param statusCode código de erro HTTP
     */
    public KCUsuarioJaExisteException() {
        super();
    }

    /**
     * Construtor onde pode ser informada a mensagem a ser apresentada.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     */
    public KCUsuarioJaExisteException(String msg) {
        super(msg);
    }

    /**
     * Construtor onde pode ser informada a causa da exceção.
     * @param statusCode código de erro HTTP
     * @param cause causa origem da exceção lançada
     */
    public KCUsuarioJaExisteException(Throwable cause) {
        super(cause);
    }

    /**
     * Construtor onde pode ser informada a causa e a mensagem da exceção.
     * @param statusCode código de erro HTTP
     * @param msg mensagem do erro
     * @param cause causa origem da exceção lançada
     */
    public KCUsuarioJaExisteException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
