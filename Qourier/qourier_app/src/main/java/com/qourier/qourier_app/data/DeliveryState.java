package com.qourier.qourier_app.data;

public enum DeliveryState {
    BID_CHECK(0),
    FETCHING(1),
    SHIPPED(2),
    DELIVERED(3);

    private final int order;

    DeliveryState(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
