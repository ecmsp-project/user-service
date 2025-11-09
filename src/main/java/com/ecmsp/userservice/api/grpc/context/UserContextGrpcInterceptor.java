package com.ecmsp.userservice.api.grpc.context;

import com.ecmsp.userservice.user.domain.Permission;
import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@GrpcGlobalServerInterceptor
public class UserContextGrpcInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        UserContextData userContextData = new UserContextData(
                headers.get(Metadata.Key.of("X-User-ID", Metadata.ASCII_STRING_MARSHALLER)),
                headers.get(Metadata.Key.of("X-Login", Metadata.ASCII_STRING_MARSHALLER)),
                Arrays.stream(headers.get(Metadata.Key.of("X-Permissions", Metadata.ASCII_STRING_MARSHALLER))
                        .split(","))
                        .filter(s -> !s.trim().isBlank())
                        .map(Permission::getPermissionByName)
                        .filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toSet())
        );

        Context context = Context.current()
                .withValue(UserContextGrpcHolder.USER_CONTEXT_KEY, userContextData);

        return Contexts.interceptCall(context, call, headers, next);
    }
}
