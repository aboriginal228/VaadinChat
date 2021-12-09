package com.example.vaadinchat;

import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Push
@Route("")
public class MainView extends VerticalLayout {

    private final Storage storage;
    private Grid<Storage.Message> grid;
    private Registration registration;
    private VerticalLayout chat;
    private VerticalLayout login;
    private String user = "";

    public MainView(Storage storage) {
        this.storage = storage;
        buildChat();
        buildLogin();

    }

    private void buildLogin() {
        login = new VerticalLayout() {{

            TextField textField = new TextField();
            textField.setPlaceholder("Please introduce");
            add(
                    textField,
                    new Button("Login") {{
                        addClickListener(click -> {
                            login.setVisible(false);
                            chat.setVisible(true);
                            user = textField.getValue();
                            storage.addRecordJoined(user);
                        });
                        addClickShortcut(Key.ENTER);
                    }}
            );
        }};
        add(login);
    }

    private void buildChat() {
        grid = new Grid<>();
        grid.setItems(storage.getMessages());
        grid.addColumn(new ComponentRenderer<>(message -> new Html(render(message))))
                .setAutoWidth(true);

        TextField field = new TextField();

        chat = new VerticalLayout();
        add(chat);
        chat.setVisible(false);
        chat.add(
                new H3("!Chat!"),
                grid,
                new HorizontalLayout() {{
                    add(
                            field,
                            new Button("Button") {{
                                addClickListener(click -> {
                                    storage.addRecord(user, field.getValue());
                                    field.clear();
                                });
                                addClickShortcut(Key.ENTER);
                            }}
                    );
                }}

        );
    }

    public void onMessage(Storage.ChatEvent event) {

        if (getUI().isPresent()) {
            UI ui = getUI().get();
            ui.getSession().lock();
            ui.beforeClientResponse(grid, a -> grid.scrollToEnd());
            ui.access(() -> grid.getDataProvider().refreshAll());
            ui.getSession().unlock();
        }

    }

    private String render(Storage.Message message) {
        if (message.getName().isEmpty()) {
            return Processor.process(String.format("_User **%s** is joined", message.getMessage()));
        } else {
            return Processor.process(String.format("**%s**: %s", message.getName(), message.getMessage()));
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = storage.attachListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }
}
