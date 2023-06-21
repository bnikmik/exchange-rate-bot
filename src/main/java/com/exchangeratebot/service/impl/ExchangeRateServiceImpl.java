package com.exchangeratebot.service.impl;

import com.exchangeratebot.client.CbrClient;
import com.exchangeratebot.exception.ServiceException;
import com.exchangeratebot.service.ExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
    private static final String CNY_XPATH = "/ValCurs//Valute[@ID='R01375']/Value";
    private final CbrClient client;

    @Autowired
    public ExchangeRateServiceImpl(CbrClient client) {
        this.client = client;
    }

    @Override
    public String getUSDExchangeRate() throws ServiceException {
        String xml = client.getCacheCurrencyRate();
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        String xml = client.getCacheCurrencyRate();
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    @Override
    public String getCNYExchangeRate() throws ServiceException {
        String xml = client.getCacheCurrencyRate();
        return extractCurrencyValueFromXML(xml, CNY_XPATH);
    }

    private String extractCurrencyValueFromXML(String xml, String xPathExpression) throws ServiceException {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            Object document = xPath.evaluate("/", source, XPathConstants.NODE);
            return xPath.evaluate(xPathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }


}
