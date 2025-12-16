package org.delcom.app.dto;

import java.util.UUID;

public class CashFlowForm {

    private UUID id;

    private String type;

    private String source;

    private String label;

    private int amount;

    private String description;

    private String confirmId;

    // Constructor
    public CashFlowForm() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(String confirmId) {
        this.confirmId = confirmId;
    }

}
