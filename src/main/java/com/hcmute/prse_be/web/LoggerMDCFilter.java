package com.hcmute.prse_be.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggerMDCFilter extends OncePerRequestFilter{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggerMDCFilter.class);

    private static final String MDC_UUID_TOKEN_KEY = "UUID";
    private static final String MDC_IP_KEY = "IP";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put(MDC_UUID_TOKEN_KEY, UUID.randomUUID().toString().replaceAll("-", ""));
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            logger.error("Error in the filter", ex);
        } finally {
            MDC.remove(MDC_UUID_TOKEN_KEY);
            MDC.remove(MDC_IP_KEY);
        }
    }

    @Override
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
