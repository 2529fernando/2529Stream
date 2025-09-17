package com.fernan2529.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;
import java.util.Map;

public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;

    @ServerTimestamp
    private Timestamp lastMessageTimestamp;

    private String lastMessageSenderId;
    private String lastMessage;
    private String lastMessageType; // "text", "image", "audio", etc.

    // Denormalizados (opcionales, útiles en lista de chats)
    private String otherUsername;
    private String otherPhotoUrl;

    // Lecturas: userId -> último mensaje leído en Timestamp
    private Map<String, Timestamp> lastSeenMap;

    public ChatroomModel() {}

    public ChatroomModel(String chatroomId, List<String> userIds,
                         Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessage = lastMessage;
        this.lastMessageType = "text";
    }

    public String getChatroomId() { return chatroomId; }
    public void setChatroomId(String chatroomId) { this.chatroomId = chatroomId; }

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }

    public Timestamp getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }

    public String getLastMessageSenderId() { return lastMessageSenderId; }
    public void setLastMessageSenderId(String lastMessageSenderId) { this.lastMessageSenderId = lastMessageSenderId; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getLastMessageType() { return lastMessageType == null ? "text" : lastMessageType; }
    public void setLastMessageType(String lastMessageType) { this.lastMessageType = lastMessageType; }

    public String getOtherUsername() { return otherUsername; }
    public void setOtherUsername(String otherUsername) { this.otherUsername = otherUsername; }

    public String getOtherPhotoUrl() { return otherPhotoUrl; }
    public void setOtherPhotoUrl(String otherPhotoUrl) { this.otherPhotoUrl = otherPhotoUrl; }

    public Map<String, Timestamp> getLastSeenMap() { return lastSeenMap; }
    public void setLastSeenMap(Map<String, Timestamp> lastSeenMap) { this.lastSeenMap = lastSeenMap; }
}
