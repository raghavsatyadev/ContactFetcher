package com.rocky.contacter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class GenRecyclerAdapter
        <ViewHolder extends RecyclerView.ViewHolder, Model>
        extends RecyclerView.Adapter<ViewHolder> {
    private GenRecyclerAdapter.MyClickListener myClickListener;
    private ArrayList<Model> models;

    public GenRecyclerAdapter(ArrayList<Model> models) {
        this.models = models;
    }

    public MyClickListener getMyClickListener() {
        return myClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return creatingViewHolder(parent, viewType);
    }

    protected abstract ViewHolder creatingViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindingViewHolder(holder, position);
    }

    protected abstract void bindingViewHolder(ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    public void addAll(ArrayList<Model> models) {
        int position = getItemCount();
        this.getItems().addAll(models);
        notifyItemRangeInserted(position, models.size());
    }

    public void addItem(Model model, int index) {
        getItems().add(model);
        notifyItemInserted(index);
    }

    public void addItem(Model model) {
        getItems().add(model);
        notifyItemInserted(getItemCount() - 1);
    }

    private ArrayList<Model> getItems() {
        return models;
    }

    public void deleteAll() {
        int itemCount = getItemCount();
        getItems().clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public void replaceAll(ArrayList<Model> models) {
        int previousSize = getItemCount();
        getItems().clear();
        notifyItemRangeRemoved(0, previousSize);
        getItems().addAll(models);
        notifyItemRangeInserted(0, getItemCount());
//        notifyDataSetChanged();
    }


    public Model getItem(int index) {
        return getItems().get(index);
    }

    public void deleteItem(int index) {
        if (index >= 0 && index < getItemCount()) {
            getItems().remove(index);
            notifyItemRemoved(index);
        }
    }

    public void setOnItemClickListener(GenRecyclerAdapter.MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}
