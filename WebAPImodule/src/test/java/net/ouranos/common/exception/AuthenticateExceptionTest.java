package net.ouranos.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticateExceptionTest {

    @Test
    void testAuthenticateExceptionMessage() {
        String errorMessage = "認証失敗";
        AuthenticateException exception = new AuthenticateException(errorMessage);

        // 例外メッセージが正しく設定されていることを検証
        assertEquals(errorMessage, exception.getMessage(), "エラーメッセージが期待通りであること");
    }

    @Test
    void testAuthenticateExceptionIsRuntimeException() {
        AuthenticateException exception = new AuthenticateException("認証失敗");

        // AuthenticateExceptionがRuntimeExceptionのインスタンスであることを検証
        assertTrue(exception instanceof RuntimeException, "AuthenticateExceptionはRuntimeExceptionのインスタンスであること");
    }
}