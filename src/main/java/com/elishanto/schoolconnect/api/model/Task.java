package com.elishanto.schoolconnect.api.model;

public class Task {
    private int id;
    private String created;
    private String delivery;
    private String subject;
    private String desc;
    private String full;
    private int status;

    private Task() {
    }

    public int getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getSubject() {
        return subject;
    }

    public String getDesc() {
        return desc;
    }

    public String getFull() {
        return full;
    }

    public int getStatus() {
        return status;
    }

    public static Builder newBuilder() {
        return new Task().new Builder();
    }

    public class Builder {

        private Builder() {
        }

        public Builder setId(int id) {
            Task.this.id = id;

            return this;
        }

        public Builder setCreated(String created) {
            Task.this.created = created;

            return this;
        }

        public Builder setDelivery(String delivery) {
            Task.this.delivery = delivery;

            return this;
        }

        public Builder setSubject(String subject) {
            Task.this.subject = subject;

            return this;
        }

        public Builder setDesc(String desc) {
            Task.this.desc = desc;

            return this;
        }

        public Builder setFull(String full) {
            Task.this.full = full;

            return this;
        }

        public Builder setStatus(int status) {
            Task.this.status = status;

            return this;
        }

        public Task build() {
            return Task.this;
        }

    }
}