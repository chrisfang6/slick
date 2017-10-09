package net.chris.ui.slick.ui;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.chris.ui.slick.R;
import net.chris.ui.slick.model.HomeMenuItem;

import java.util.concurrent.CopyOnWriteArrayList;

public class HomeMenuAdapter extends RecyclerView.Adapter<MenuHolder> {

    private final CopyOnWriteArrayList<HomeMenuItem> items;
    private final int itemWidth;
    private final int itemHeight;
    private final int radius;
    private final int imageHeight;
    private final ObservableInt itemOffset;


    private LayoutInflater inflater;

    public HomeMenuAdapter(@NonNull final CopyOnWriteArrayList<HomeMenuItem> items,
                           @NonNull final int itemWidth,
                           @NonNull final int itemHeight,
                           @NonNull final int imageHeight,
                           @NonNull final int radius,
                           @NonNull final ObservableInt itemOffset) {
        this.items = items;
        this.itemWidth = itemWidth;
        this.imageHeight = imageHeight;
        this.itemHeight = itemHeight;
        this.radius = radius;
        this.itemOffset = itemOffset;
    }

    @Override
    public MenuHolder onCreateViewHolder(@NonNull final ViewGroup parent, @NonNull final int viewType) {
        return new MenuHolder(getLayoutInflater(parent.getContext()).inflate(R.layout.item_menu_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuHolder holder, @NonNull final int position) {
        HomeMenuItem item = getItem(position);
        if (item == null) {
            return;
        }
        holder.bind(item, itemWidth, itemHeight, imageHeight, radius, itemOffset);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private HomeMenuItem getItem(@NonNull final int position) {
        return items.get(position);
    }

    private LayoutInflater getLayoutInflater(@NonNull final Context context) {
        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }
        return inflater;
    }

}
