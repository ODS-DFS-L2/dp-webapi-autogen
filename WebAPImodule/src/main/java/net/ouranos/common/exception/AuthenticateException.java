package net.ouranos.common.exception;

/**
 * バックエンド認証例外
 */
public class AuthenticateException extends RuntimeException {
    public AuthenticateException(String message) {
        super(message);
    }
}
