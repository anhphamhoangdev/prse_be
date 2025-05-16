package com.hcmute.prse_be.request;

import java.util.List;

public class CategoryOrderRequest {
    private List<CategoryOrder> categoryOrders;

    public List<CategoryOrder> getCategoryOrders() {
        return categoryOrders;
    }

    public void setCategoryOrders(List<CategoryOrder> categoryOrders) {
        this.categoryOrders = categoryOrders;
    }

    public static class CategoryOrder {
        private Long id;
        private Integer orderIndex;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getOrderIndex() {
            return orderIndex;
        }

        public void setOrderIndex(Integer orderIndex) {
            this.orderIndex = orderIndex;
        }
    }
}
