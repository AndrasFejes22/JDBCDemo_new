package pojo;

import java.util.Objects;

public class Order {

    private final int id;
    private final int customerId;
    private final int productId;


    public Order(int id, int customerId, int productId) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && customerId == order.customerId && productId == order.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, productId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", productId=" + productId +
                '}';
    }
}
