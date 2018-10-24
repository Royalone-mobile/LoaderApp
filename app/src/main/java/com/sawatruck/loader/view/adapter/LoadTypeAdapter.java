package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.LoadType;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.view.design.CircularImage;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 9/26/2017.
 */


public class LoadTypeAdapter extends RecyclerView.Adapter<LoadTypeAdapter.MyViewHolder> {
    private ArrayList<LoadType> loadTypes = new ArrayList<>();
    private Context context;
    private int currentSelectedItem = 0;
    RecyclerView.LayoutManager lm;

    @Override
    public LoadTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.load_type_list_item, parent, false);
        return new LoadTypeAdapter.MyViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
       lm = recyclerView.getLayoutManager();
    }

    public LoadTypeAdapter(Context context) {
        this.context = context;
    }
    @Override
    public void onBindViewHolder(final LoadTypeAdapter.MyViewHolder holder, final int position) {
        LoadType loadType = loadTypes.get(position);

        holder.txtLoadTypeName.setText(loadType.getName());
        if(currentSelectedItem == position){
            holder.imgLoadType.setShadowRadius(5);
            holder.imgLoadType.setBackground(ContextCompat.getDrawable(context,R.drawable.drawable_circular_image));
            holder.imgLoadType.setShadowColor(R.color.colorLightBlue);
            holder.imgLoadType.getLayoutParams().width = (int)Misc.dpToPx(100);
            holder.imgLoadType.getLayoutParams().height = (int)Misc.dpToPx(100);
        }
        else {
            holder.imgLoadType.setShadowRadius(0);
            holder.imgLoadType.setBackground(ContextCompat.getDrawable(context,R.drawable.drawable_circular_image));
            holder.imgLoadType.getLayoutParams().width = (int)Misc.dpToPx(80);
            holder.imgLoadType.getLayoutParams().height = (int)Misc.dpToPx(80);
        }

        try {
            BaseApplication.getPicasso().load(loadType.getPhoto()).placeholder(R.drawable.ico_truck).into(holder.imgLoadType);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectedItem = position;
                notifyDataSetChanged();
            }
        });


    }

    public boolean tryMoveSelection() {
        int tryFocusItem = currentSelectedItem;

        // If still within valid bounds, move the selection, notify to redraw, and scroll
        if (tryFocusItem >= 0 && tryFocusItem < getItemCount()) {
            lm.scrollToPosition(currentSelectedItem);
            return true;
        }

        return false;
    }
    @Override
    public int getItemCount() {
        return loadTypes.size();
    }

    public ArrayList<LoadType> getLoadTypes() {
        return loadTypes;
    }

    public void setLoadTypes(ArrayList<LoadType> loadTypes) {
        this.loadTypes = loadTypes;
        for(int i =0; i<this.loadTypes.size(); i++) {
            if(loadTypes.get(i).getLoadTypeID() == 10)
                currentSelectedItem = i;
        }
    }

    public int getCurrentSelectedItem() {
        return currentSelectedItem;
    }

    public void setCurrentSelectedItem(int currentSelectedItem) {
        this.currentSelectedItem = currentSelectedItem;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_load_type) CircularImage imgLoadType;
        @Bind(R.id.txt_load_type_name) CustomTextView txtLoadTypeName;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
} 