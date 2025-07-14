package com.car_dealer_web.restful_api.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.car_dealer_web.restful_api.exceptions.TooManyRequestsException;

@Aspect
@Component
public class RateLimiterAspectHandler {
  public static final String ERROR_MESSAGE = "To many request at endpoint %s from IP %s! Please try again after %d milliseconds!";

  private static final Logger LOG = LoggerFactory.getLogger(RateLimiterAspectHandler.class);

  private final ConcurrentHashMap<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

  @Value("${rate.limit.max}")
  private int rateLimitMax;

  @Value("${rate.limit.duration:}")
  private long rateLimitDuration;

  @Before("@annotation(com.car_dealer_web.restful_api.annotations.RateLimit)")
  public void rateLimit() {
    final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes();
    final String ipAddrKey = requestAttributes.getRequest().getRemoteAddr();
    final long currTime = System.currentTimeMillis();

    requestCounts.putIfAbsent(ipAddrKey, new ArrayList<>());
    requestCounts.get(ipAddrKey).add(currTime);

    cleanUpRequestCounts(currTime);

    if (requestCounts.get(ipAddrKey).size() > rateLimitMax) {
      LOG.error(ERROR_MESSAGE);

      throw new TooManyRequestsException(
          String.format(ERROR_MESSAGE, requestAttributes.getRequest().getRequestURI(), ipAddrKey, rateLimitDuration));
    }
  }

  private void cleanUpRequestCounts(final long currTime) {
    requestCounts.values().forEach(l -> {
      l.removeIf(t -> timeIsOverdue(currTime, t));
    });
  }

  private boolean timeIsOverdue(final long currTime, final long timeToCheck) {
    return currTime - timeToCheck > rateLimitDuration;
  }
}
