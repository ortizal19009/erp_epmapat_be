package com.epmapat.erp_epmapat.sri.services;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.sri.models.YourDataModel;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
@Service
public class XmlParserService {
    public YourDataModel parseXmlToObject(String xmlString) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlString, YourDataModel.class);
    }
}
