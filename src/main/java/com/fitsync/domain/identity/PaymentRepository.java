package com.fitsync.domain.identity;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findByUserId(UUID userId);
}
