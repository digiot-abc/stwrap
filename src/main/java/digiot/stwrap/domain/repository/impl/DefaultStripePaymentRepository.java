package digiot.stwrap.domain.repository.impl;

import digiot.stwrap.domain.model.StripePayment;
import digiot.stwrap.domain.repository.StripePaymentRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultStripePaymentRepository implements StripePaymentRepository {

    private final DataSource dataSource;

    public DefaultStripePaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<StripePayment> findById(String id) {
        String sql = "SELECT * FROM stripe_payment WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStripePayment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding stripe payment by ID", e);
        }

        return Optional.empty();
    }

    @Override
    public List<StripePayment> findAllByStripeLinkedUserId(String StripeLinkedUserId) {
        List<StripePayment> payments = new ArrayList<>();
        String sql = "SELECT * FROM stripe_payment WHERE stripe_linked_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, StripeLinkedUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapRowToStripePayment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding stripe payments by stripe user link ID", e);
        }

        return payments;
    }

    @Override
    public StripePayment create(StripePayment stripePayment) {
        String sql = "INSERT INTO stripe_payment (stripe_linked_user_id, stripe_charge_id, amount, currency, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, stripePayment.getStripeLinkedUserId());
            stmt.setString(2, stripePayment.getStripeChargeId());
            stmt.setBigDecimal(3, stripePayment.getAmount());
            stmt.setString(4, stripePayment.getCurrency());
            stmt.setString(5, stripePayment.getStatus());

            if (stripePayment.getId() != null) {
                stmt.setString(6, stripePayment.getId());
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Saving stripe payment failed, no rows affected.");
            }

            if (stripePayment.getId() == null) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        stripePayment.setId(generatedKeys.getString(1));
                    } else {
                        throw new SQLException("Creating stripe payment failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving stripe payment", e);
        }

        return stripePayment;
    }

    @Override
    public void delete(StripePayment stripePayment) {
        String sql = "DELETE FROM stripe_payment WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, stripePayment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting stripe payment", e);
        }
    }

    private StripePayment mapRowToStripePayment(ResultSet rs) throws SQLException {
        StripePayment payment = new StripePayment();
        payment.setId(rs.getString("id"));
        payment.setStripeLinkedUserId(rs.getString("stripe_linked_user_id"));
        payment.setStripeChargeId(rs.getString("stripe_charge_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setCurrency(rs.getString("currency"));
        payment.setStatus(rs.getString("status"));
        return payment;
    }
}
