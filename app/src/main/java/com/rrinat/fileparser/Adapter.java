package com.rrinat.fileparser;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<String> items = Collections.emptyList();
    private BitSet selectedItems = new BitSet();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item, group, false);
        return new ViewHolder(view, selectedItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.bind(items.get(i), selectedItems.get(i));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void onAdd() {
        int position = getItemCount();
        if (position > 0) {
            notifyItemChanged(position - 1);
        }
    }

    void onClearLines() {
        notifyDataSetChanged();
    }

    void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    String getSelectedItems() {
        StringBuilder builder = new StringBuilder();

        for (int i = selectedItems.nextSetBit(0); i >= 0; i = selectedItems.nextSetBit(i+1)) {
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }

            if (i < getItemCount()) {
                builder.append(items.get(i));
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        ViewHolder(@NonNull View itemView, BitSet selectedItems) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView.setOnClickListener(v -> dispatchClick(selectedItems));
        }

        private void dispatchClick(BitSet selectedItems) {
            int position = getAdapterPosition();
            boolean isSelected = selectedItems.get(position);
            if (isSelected) {
                selectedItems.clear(position);
            } else {
                selectedItems.set(position);
            }
            setBackground(!isSelected);
        }

        void bind(String value, boolean isSelected) {
            textView.setText(value);
            setBackground(isSelected);
        }

        private void setBackground(boolean isSelected) {
            if (isSelected) {
                textView.setBackgroundColor(Color.RED);
            } else  {
                textView.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
