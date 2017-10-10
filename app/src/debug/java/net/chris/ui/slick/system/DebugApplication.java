package net.chris.ui.slick.system;

import net.chris.tool.debug.library.DebugHelper;

public class DebugApplication extends BaseApplication {

    private DebugHelper debugHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        debugHelper = new DebugHelper();
        debugHelper.enableStrictMode();
        debugHelper.installLeakCanary(this);
    }

    @Override
    public void onTerminate() {
        debugHelper.uninstallLeakCanary(this);
        super.onTerminate();
    }
}
