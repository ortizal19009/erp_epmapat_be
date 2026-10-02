package com.epmapat.erp_epmapat.excepciones;

import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/** Handles the final async dispatch after a caja SSE client disconnects. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CajaSseDisconnectResolver implements HandlerExceptionResolver {
    private static final Logger log = LoggerFactory.getLogger(CajaSseDisconnectResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception exception) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean cajaStream = "/recaudacion-cobro/caja/stream".equals(path)
                || "/recaudacion-cobro/caja/stream/global".equals(path);
        if (!cajaStream || !response.isCommitted() || !isDisconnect(exception)) {
            return null;
        }
        // The stream is already committed and the peer is gone: do not write or sendError.
        log.debug("Cliente SSE desconectado en {}", path);
        return new ModelAndView();
    }

    private boolean isDisconnect(Throwable error) {
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Throwable cause = error; cause != null && visited.add(cause); cause = cause.getCause()) {
            if (!(cause instanceof IOException)) continue;
            if ("org.apache.catalina.connector.ClientAbortException".equals(cause.getClass().getName())) {
                return true;
            }
            String message = cause.getMessage();
            if (message == null) continue;
            String normalized = message.toLowerCase(Locale.ROOT);
            if (normalized.contains("broken pipe") || normalized.contains("connection reset")
                    || normalized.contains("forcibly closed")) return true;
        }
        return false;
    }
}
