import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionDTO (
    UUID id,
    String name,
    String logoKey,
    String category,
    BigDecimal amount,
    String currency,
    BillingCycle cycle,
    LocalDate renewsOn,
    boolean reminderOn
) {}