package com.sawatruck.loader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.GetToDo;
import com.sawatruck.loader.view.activity.ActivityRating;
import com.sawatruck.loader.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {
    private ArrayList<GetToDo> todos = new ArrayList<>();
    private Context context;

    @Override
    public ToDoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.todo_item, parent, false);
        return new ToDoAdapter.MyViewHolder(v);
    }

    public ToDoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ToDoAdapter.MyViewHolder holder, final int position) {
        final GetToDo toDo = getTodos().get(position);
        holder.txtToDo.setText(toDo.getMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityRating.class);
                intent.putExtra(Constant.INTENT_TRAVEL_ID, toDo.getID());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }


    public void initializeAdapter() {
        this.todos = new ArrayList<>();
        notifyDataSetChanged();
    }

    public ArrayList<GetToDo> getTodos() {
        return todos;
    }

    public void setTodos(ArrayList<GetToDo> todos) {
        this.todos = todos;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_todo)
        CustomTextView txtToDo;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}