package com.krzykrucz.transfers.application.error;

import ratpack.error.ServerErrorHandler;
import ratpack.handling.Context;
import ratpack.http.Status;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

public class DomainExceptionHandler implements ServerErrorHandler {

    private static final Status BAD_REQUEST = Status.of(400);
    private static final Status SERVER_ERROR = Status.of(500);

    @Override
    public void error(Context context, Throwable throwable) throws Exception {
        this.handleError(throwable, context);
    }

    private void handleError(Throwable throwable, Context ctx) {
        Match(throwable).of(
                Case($(instanceOf(IllegalArgumentException.class)), ex -> run(() -> sendBadRequest(ex, ctx))),
                Case($(instanceOf(IllegalStateException.class)), ex -> run(() -> sendBadRequest(ex, ctx))),
                Case($(), ex -> run(() -> sendServerError(ex, ctx)))
        );
    }

    private void sendBadRequest(Exception ex, Context ctx) {
        ctx.getResponse()
                .status(BAD_REQUEST)
                .send(ex.getMessage());
    }

    private void sendServerError(Throwable ex, Context ctx) {
        ctx.getResponse()
                .status(SERVER_ERROR)
                .send(ex.getMessage());
    }
}
