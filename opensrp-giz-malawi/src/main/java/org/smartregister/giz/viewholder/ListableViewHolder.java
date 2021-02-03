package org.smartregister.giz.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.giz.contract.ListContract;


public abstract class ListableViewHolder<T extends ListContract.Identifiable> extends RecyclerView.ViewHolder implements ListContract.AdapterViewHolder<T> {
    public ListableViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

