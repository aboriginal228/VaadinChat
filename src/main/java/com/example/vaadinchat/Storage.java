package com.example.vaadinchat;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {

    @Getter
    private Queue<Message> messages = new ConcurrentLinkedQueue<>();
    private ComponentEventBus eventBus = new ComponentEventBus(new Div());

    public void addRecord(String s, String value) {
        messages.add(new Message(s, value));
        eventBus.fireEvent(new ChatEvent());
    }

    public void addRecordJoined(String user) {
        messages.add(new Message("", user));
        eventBus.fireEvent(new ChatEvent());
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String name;
        private String message;
    }

    public static class ChatEvent extends ComponentEvent<Div> {
        public ChatEvent() {
            super(new Div(), false);
        }
    }

    public Registration attachListener(ComponentEventListener<ChatEvent> listener) {
        return eventBus.addListener(ChatEvent.class, listener);
    }

    public int size() {
        return messages.size();
    }
}
