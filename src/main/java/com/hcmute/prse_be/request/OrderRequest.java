package com.hcmute.prse_be.request;

import java.util.List;

public class OrderRequest {
    private List<Order> chapterOrders;
    private List<Order> lessonOrders;

    public List<Order> getChapterOrders() {
        return chapterOrders;
    }

    public void setChapterOrders(List<Order> chapterOrders) {
        this.chapterOrders = chapterOrders;
    }

    public List<Order> getLessonOrders() {
        return lessonOrders;
    }

    public void setLessonOrders(List<Order> lessonOrders) {
        this.lessonOrders = lessonOrders;
    }



    
    public static class Order {
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
