package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import com.epmapat.erp_epmapat.servicio.administracion.NextcloudStorageService;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

class MobileNextcloudStorageTest {
    @Test void realWebDavClientCreatesDirectoriesAndStoresStableEncodedPath() throws Exception {
        var files=new ConcurrentHashMap<String,byte[]>();
        var directories=ConcurrentHashMap.<String>newKeySet(); directories.add("/remote.php/webdav/");
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/",exchange -> {
            try {
                String path=exchange.getRequestURI().getPath(); String method=exchange.getRequestMethod();
                if (method.equals("HEAD")) exchange.sendResponseHeaders(directories.contains(path)||files.containsKey(path)?200:404,-1);
                else if (method.equals("MKCOL")) { directories.add(path);exchange.sendResponseHeaders(201,-1); }
                else if (method.equals("PUT")) { files.put(path,exchange.getRequestBody().readAllBytes());exchange.sendResponseHeaders(201,-1); }
                else if (method.equals("GET") && files.containsKey(path)) {
                    byte[] data=files.get(path);exchange.sendResponseHeaders(200,data.length);exchange.getResponseBody().write(data);
                } else exchange.sendResponseHeaders(404,-1);
            } finally { exchange.close(); }
        });server.start();
        try {
            var service=new NextcloudStorageService();
            ReflectionTestUtils.setField(service,"baseUrl","http://127.0.0.1:"+server.getAddress().getPort());
            ReflectionTestUtils.setField(service,"username","test");ReflectionTestUtils.setField(service,"password","test");
            ReflectionTestUtils.setField(service,"baseFolder","/EPMAPAT-PHOTOS/");service.init();
            byte[] bytes={1,2,3};var file=new MockMultipartFile("file","original.png","image/png",bytes);
            String path=service.storeIdempotent(file,"lecturas/ruta con espacio","stable-key");
            assertEquals("EPMAPAT-PHOTOS/lecturas/ruta con espacio/stable-key.png",path);
            assertEquals(path,service.storeIdempotent(file,"lecturas/ruta con espacio","stable-key"));
            assertEquals(1,files.size());assertArrayEquals(bytes,service.load(path).getInputStream().readAllBytes());
            assertThrows(IllegalArgumentException.class,()->service.storeIdempotent(file,"../outside","stable-key"));
        } finally {server.stop(0);}
    }
}
