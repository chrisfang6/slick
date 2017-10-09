package net.chris.ui.slick;

import android.databinding.DataBindingUtil;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;

import net.chris.ui.slick.databinding.DialogDetailBinding;
import net.chris.ui.slick.model.HomeMenuItem;
import net.chris.ui.slick.ui.model.DimensionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static net.chris.ui.slick.Constants.KEY_CONTENT;
import static net.chris.ui.slick.Constants.KEY_DIALOG_HEIGHT;
import static net.chris.ui.slick.Constants.KEY_DIALOG_IMAGE_HEIGHT;
import static net.chris.ui.slick.Constants.KEY_DIALOG_RADIUS;
import static net.chris.ui.slick.Constants.KEY_DIALOG_WIDTH;

public class MenuItemDetailActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.item_menu_item_layout)
    View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.dialog_detail);
        setWindow();
        binding.setDialogItem((HomeMenuItem) getIntent().getSerializableExtra(KEY_CONTENT));
        binding.setDialogImageHeight(getIntent().getIntExtra(KEY_DIALOG_IMAGE_HEIGHT, 100));
        unbinder = ButterKnife.bind(this);
        int width = getIntent().getIntExtra(KEY_DIALOG_WIDTH, 100);
        int height = getIntent().getIntExtra(KEY_DIALOG_HEIGHT, 100);
        int radius = getIntent().getIntExtra(KEY_DIALOG_RADIUS, 10);
        root.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(new Rect(0, 0, width, height), radius);
            }
        });
        root.setClipToOutline(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.dialog_close)
    public void onClick(View view) {
        supportFinishAfterTransition();
    }

    private void setWindow() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = DimensionHelper.getInstance().getDialogWidth();
        params.height = DimensionHelper.getInstance().getDialogHeight();
        getWindow().setAttributes(params);
    }
}
