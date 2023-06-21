package com.exchangeratebot.client;

import com.exchangeratebot.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CbrClient {

    private final OkHttpClient client;

    @Value("${cbr.currency.rates.xml.url}")
    private String url;

    @Autowired
    public CbrClient(OkHttpClient client) {
        this.client = client;
    }

    private String getCurrencyRateXML() throws ServiceException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            } else {
                return body.string();
            }
        } catch (IOException e) {
            throw new ServiceException("Ошибка получения курса валют", e);
        }
    }

    @Cacheable(cacheNames = "rates")
    public String getCacheCurrencyRate() throws ServiceException {
        return getCurrencyRateXML();
    }
}
