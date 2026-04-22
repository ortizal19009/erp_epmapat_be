package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class InMemoryMultipartFile implements MultipartFile {

   private final String name;
   private final String originalFilename;
   private final String contentType;
   private final byte[] content;

   public InMemoryMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
      this.name = name;
      this.originalFilename = originalFilename;
      this.contentType = contentType;
      this.content = content == null ? new byte[0] : content.clone();
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getOriginalFilename() {
      return originalFilename;
   }

   @Override
   public String getContentType() {
      return contentType;
   }

   @Override
   public boolean isEmpty() {
      return content.length == 0;
   }

   @Override
   public long getSize() {
      return content.length;
   }

   @Override
   public byte[] getBytes() throws IOException {
      return content.clone();
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(content);
   }

   @Override
   public void transferTo(File dest) throws IOException, IllegalStateException {
      try (FileOutputStream out = new FileOutputStream(dest)) {
         out.write(content);
      }
   }
}
