package com.fitsync.interfaces.common;

import com.fitsync.application.identity.CurrentUser;
import com.fitsync.application.identity.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/** Permite recibir "CurrentUser me" como parámetro en cualquier controlador. */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mav,
                                  NativeWebRequest request, WebDataBinderFactory binderFactory) {
        Object user = request.getAttribute(AuthInterceptor.ATTR, RequestAttributes.SCOPE_REQUEST);
        if (user == null) {
            throw new UnauthorizedException("Inicia sesión para continuar.");
        }
        return user;
    }
}
