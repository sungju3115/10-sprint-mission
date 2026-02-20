package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;


@Getter
public class Channel extends Base  {
    // 필드
    private String name;
    private String description;
    private final String type;
    private final List<User> membersList;
    private final List<Message> messageList;

    // 생성자
    public Channel(String name, String description) {
        super();
        this.name = name;
        this.description = description;
        this.membersList = new ArrayList<>();
        this.messageList = new ArrayList<>();
        this.type = "Public";
    }

    public Channel(){
        super();
        this.name = null;
        this.description = null;
        this.membersList = new ArrayList<>();
        this.messageList = new ArrayList<>();
        this.type = "Private";
    }

    // setter
    public void updateName(String name) {
        this.name = name;
        updateUpdatedAt();
    }

    // other
    public void addMember(User member){
        membersList.add(member);
        updateUpdatedAt();
    }

    public void removeMember(User member) {
        membersList.remove(member);
        updateUpdatedAt();
    }

    public void addMessage(Message msg){
        messageList.add(msg);
        updateUpdatedAt();
    }

    public void removeMessage(Message msg){
        messageList.remove(msg);
        updateUpdatedAt();
    }
}
