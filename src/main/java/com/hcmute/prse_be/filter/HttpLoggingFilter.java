package com.hcmute.prse_be.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isSwaggerRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            filterChain.doFilter(request, response);
            return;
        }

        RepeatableContentCachingRequestWrapper requestWrapper = new RepeatableContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        logRequest(request, requestWrapper);
        filterChain.doFilter(requestWrapper, responseWrapper);
        if (request.getRequestURI() != null && request.getRequestURI().contains("api/v1/web/get_all_user")) {
            responseWrapper.copyBodyToResponse();
        }else {
            logResponse(responseWrapper);
        }
    }

    private void logRequest(HttpServletRequest request, RepeatableContentCachingRequestWrapper requestWrapper) throws IOException {
        String body = requestWrapper.readInputAndDuplicate();
        if (request.getRequestURI() != null && (
                request.getRequestURI().contains("api/driver/doFinishGo") ||
                        request.getRequestURI().contains("api/instructor/upload-preview-video")
        )){
            return;
        }
        logger.info("REQUEST " + request.getRequestURI() + ": " + body.replaceAll("\\s", ""));
    }

    private void logResponse(ContentCachingResponseWrapper responseWrapper) throws IOException {
        logger.info("RESPONSE " + new String(responseWrapper.getContentAsByteArray()));
        responseWrapper.copyBodyToResponse();
    }

    private boolean isSwaggerRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (
                uri.contains("swagger") ||
                        uri.contains("api-docs") ||
                        uri.contains("webjars") ||
                        uri.startsWith("/v3/api-docs") ||
                        uri.startsWith("/swagger-ui")
        );
    }
}