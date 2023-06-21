package com.exchangeratebot.config;

import com.exchangeratebot.bot.ExchangeRateBot;
import com.google.common.cache.CacheBuilder;
import okhttp3.OkHttpClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.TimeUnit;

@Configuration
public class ExchangeRateBotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(ExchangeRateBot exchangeRateBot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(exchangeRateBot);
        return api;
    }

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean("CacheManager")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder()
                                .expireAfterWrite(1, TimeUnit.HOURS)
                                .build().asMap(),
                        false);
            }
        };
    }

}
