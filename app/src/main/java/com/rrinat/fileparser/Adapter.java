package com.rrinat.fileparser;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private final List<String> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item, group, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.bind(items.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void add(String value) {
        int position = getItemCount();
        items.add(value);
        notifyItemChanged(position);
    }

    void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
        }

        void bind(String value) {
            textView.setText(value);
        }
    }
}
