package net.chris.ui.slick.model.inject;

import net.chris.ui.slick.ui.model.HomeViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class HomeModule {

    @Singleton
    @Provides
    public HomeViewModel provideHomeViewModel() {
        return new HomeViewModel();
    }

}
