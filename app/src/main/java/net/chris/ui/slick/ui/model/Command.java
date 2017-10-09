package net.chris.ui.slick.ui.model;

public interface Command {

    void execute();

    void execute(Object... args);

    enum CommandType {
        PULL_BACKGROUND,
        ACTION_UP,
        CLICK_ITEM,
    }
}
