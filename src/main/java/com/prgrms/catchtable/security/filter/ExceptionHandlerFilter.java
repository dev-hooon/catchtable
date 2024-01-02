package com.prgrms.catchtable.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.catchtable.common.exception.ErrorResponse;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandlerFilter extends GenericFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (BadRequestCustomException be) {
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(httpServletResponse, toJson(new ErrorResponse(be.getErrorCode())));
        } catch (NotFoundCustomException ne) {
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            sendJson(httpServletResponse, toJson(new ErrorResponse(ne.getErrorCode())));
        }
    }

    private String toJson(ErrorResponse response) throws JsonProcessingException {
        return mapper.writeValueAsString(response);
    }

    private void sendJson(HttpServletResponse response, String resultJson) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(resultJson.getBytes().length);
        response.getWriter().write(resultJson);
    }
}
