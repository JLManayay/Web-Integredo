package com.fitsync.interfaces.subscription;

import com.fitsync.application.identity.CurrentUser;
import com.fitsync.application.subscription.SubscriptionCommandHandler;
import com.fitsync.application.subscription.SubscriptionQueryHandler;
import com.fitsync.domain.identity.Payment;
import com.fitsync.domain.identity.SubscriptionPlan;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    public record PaymentResponse(UUID id, BigDecimal amount, String currency, String method, String reference, Instant paidAt) {
        static PaymentResponse from(Payment p) {
            return new PaymentResponse(p.id(), p.amount(), p.currency(), p.method(), p.reference(), p.paidAt());
        }
    }

    public record PlanInfo(String plan, BigDecimal proMonthlyPrice, String currency, List<PaymentResponse> payments) {}

    private final SubscriptionCommandHandler commands;
    private final SubscriptionQueryHandler queries;

    public SubscriptionController(SubscriptionCommandHandler commands, SubscriptionQueryHandler queries) {
        this.commands = commands;
        this.queries = queries;
    }

    @GetMapping
    public PlanInfo info(CurrentUser me) {
        return new PlanInfo(me.plan().name(), SubscriptionPlan.PRO.monthlyPrice(), SubscriptionPlan.CURRENCY,
            queries.paymentsOf(me.id()).stream().map(PaymentResponse::from).toList());
    }

    /** Pago simulado: registra la operación y activa el plan Pro. */
    @PostMapping("/upgrade")
    public PaymentResponse upgrade(CurrentUser me) {
        return PaymentResponse.from(commands.upgradeToPro(me.id()));
    }

    @PostMapping("/cancel")
    public PlanInfo cancel(CurrentUser me) {
        commands.cancelPro(me.id());
        return new PlanInfo(SubscriptionPlan.GRATIS.name(), SubscriptionPlan.PRO.monthlyPrice(), SubscriptionPlan.CURRENCY,
            queries.paymentsOf(me.id()).stream().map(PaymentResponse::from).toList());
    }
}
