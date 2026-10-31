package com.epmapat.erp_epmapat.emails.component;

import java.io.IOException;
import java.time.Duration;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.*;
import org.springframework.web.filter.OncePerRequestFilter;

/** Mantiene las URL del ERP y evita dos motores SMTP diferentes sobre la misma cola. */
@Component
@ConditionalOnProperty(name="erp.mail.proxy-enabled",havingValue="true",matchIfMissing=true)
public class EmailMicroserviceProxyFilter extends OncePerRequestFilter {
    @org.springframework.beans.factory.annotation.Autowired private EmailAdminAccessService access;
    @Value("${OPERATIONS_API_KEY:}") private String operationsKey;
    private final RestTemplate client=new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(40)).build();
    @Value("${erp.mail.base-url:http://192.168.0.33:9099}") private String emailUrl;
    @Value("${erp.sri.base-url:http://192.168.0.33:9096}") private String sriUrl;
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        String p=request.getServletPath();
        return !(p.matches("/api/v1/(emails|email-accounts|email-blacklist)(/.*)?")
                || p.matches("/api/v1/sri-control(/.*)?"));
    }
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)
            throws ServletException,IOException {
        Long userId;
        try { userId=access.require(req,!("GET".equals(req.getMethod()) || "HEAD".equals(req.getMethod()))); }
        catch(org.springframework.web.server.ResponseStatusException denied){
            res.setStatus(denied.getRawStatusCode());res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Sesion o permisos insuficientes\"}");return;
        }
        String path=req.getServletPath();boolean sri=path.startsWith("/api/v1/sri-control");
        String target=(sri?sriUrl:emailUrl)+(sri?path.replaceFirst("/api/v1/sri-control","/api/v1/facturas/monitor"):path);
        if(req.getQueryString()!=null)target+="?"+req.getQueryString();
        HttpHeaders headers=new HttpHeaders();
        for(String name:new String[]{"Content-Type","Accept","Authorization","X-Operations-Key","X-Operations-Actor","X-API-Key"}) {
            String value=req.getHeader(name);if(value!=null)headers.set(name,value);
        }
        if(operationsKey!=null && !operationsKey.isBlank())headers.set("X-Operations-Key",operationsKey);
        headers.set("X-Operations-Actor","usuario:"+userId);
        try {
            ResponseEntity<byte[]> reply=client.exchange(java.net.URI.create(target),HttpMethod.valueOf(req.getMethod()),
                    new HttpEntity<byte[]>(req.getInputStream().readAllBytes(),headers),byte[].class);
            res.setStatus(reply.getStatusCodeValue());
            for(String name:new String[]{"Content-Type","Content-Disposition"})
                if(reply.getHeaders().getFirst(name)!=null)res.setHeader(name,reply.getHeaders().getFirst(name));
            if(reply.getBody()!=null)res.getOutputStream().write(reply.getBody());
        } catch(HttpStatusCodeException e) {
            res.setStatus(e.getRawStatusCode());res.setContentType("application/json");
            res.getOutputStream().write(e.getResponseBodyAsByteArray());
        } catch(RestClientException e) {
            res.setStatus(502);res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Microservicio temporalmente no disponible\"}");
        }
    }
}
