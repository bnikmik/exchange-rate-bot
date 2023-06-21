package com.exchangeratebot.service;

import com.exchangeratebot.exception.ServiceException;

public interface ExchangeRateService {

    String getUSDExchangeRate() throws ServiceException;

    String getEURExchangeRate() throws ServiceException;

    String getCNYExchangeRate() throws ServiceException;

}
