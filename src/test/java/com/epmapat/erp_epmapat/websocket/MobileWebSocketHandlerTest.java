package com.epmapat.erp_epmapat.websocket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.SessionLimitExceededException;

class MobileWebSocketHandlerTest {
    private WebSocketSession connect(MobileWebSocketHandler handler, String id) throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn(id);
        when(session.getUri()).thenReturn(URI.create("ws://localhost/mobile?userId=10089"));
        when(session.isOpen()).thenReturn(true);
        handler.afterConnectionEstablished(session);
        clearInvocations(session);
        return session;
    }

    @Test
    void bufferOverflowDoesNotAbortBroadcastAndRemovesFailedSession() throws Exception {
        MobileWebSocketHandler handler = new MobileWebSocketHandler();
        WebSocketSession failed = connect(handler, "failed");
        WebSocketSession healthy = connect(handler, "healthy");
        doThrow(new SessionLimitExceededException("Buffer exceeds limit", CloseStatus.SESSION_NOT_RELIABLE))
                .when(failed).sendMessage(any());

        assertDoesNotThrow(() -> handler.notifyLecturaUpdated(1L, 2L));
        assertEquals(1, handler.activeConnections());
        verify(failed).close(CloseStatus.SESSION_NOT_RELIABLE);
        verify(healthy).sendMessage(any());

        clearInvocations(failed, healthy);
        handler.notifyAssignmentChanged(10089L, 1L);
        verify(failed, never()).sendMessage(any());
        verify(healthy).sendMessage(any());
    }

    @Test
    void ioAndCloseFailuresDoNotEscapeNotification() throws Exception {
        MobileWebSocketHandler handler = new MobileWebSocketHandler();
        WebSocketSession failed = connect(handler, "failed");
        doThrow(new IOException("Write timeout")).when(failed).sendMessage(any());
        doThrow(new IOException("Already closed")).when(failed).close(any());

        assertDoesNotThrow(() -> handler.notifyAssignmentChanged(10089L, 1L));
        assertEquals(0, handler.activeConnections());
    }
}
