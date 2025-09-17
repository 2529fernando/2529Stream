package com.fernan2529.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

public class ChatMessageModel {
    private String message;
    private String senderId;

    @ServerTimestamp
    private Timestamp timestamp;

    // Campos extra
    private String type;       // "text", "image", "audio", etc.
    private String status;     // "sent", "delivered", "seen"
    private String replyTo;    // messageId si es una respuesta
    private String messageId;  // puedes setearlo con el id del doc en Firestore

    public ChatMessageModel() {}

    public ChatMessageModel(String message, String senderId, Timestamp timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.type = "text";
        this.status = "sent";
    }

    // --- Getters / Setters ---
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getType() { return type == null ? "text" : type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status == null ? "sent" : status; }
    public void setStatus(String status) { this.status = status; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    @Exclude // Para que Firestore no lo guarde si no quieres
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
}
