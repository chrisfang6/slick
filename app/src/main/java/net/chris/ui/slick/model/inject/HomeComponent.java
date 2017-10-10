package net.chris.ui.slick.model.inject;

import net.chris.ui.slick.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {HomeModule.class})
public abstract class HomeComponent {

    public abstract void inject(MainActivity activity);

    private static HomeComponent instance;

    public static final HomeComponent getInstance() {
        if (instance == null) {
            synchronized (HomeComponent.class) {
                if (instance == null) {
                    instance = DaggerHomeComponent.builder().build();
                }
            }
        }
        return instance;
    }

    public static final void uninject() {
        instance = null;
    }

}
