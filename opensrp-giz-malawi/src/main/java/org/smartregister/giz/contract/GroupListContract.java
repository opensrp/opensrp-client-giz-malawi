package org.smartregister.giz.contract;

import androidx.annotation.Nullable;

import org.smartregister.view.ListContract;

public interface GroupListContract extends ListContract {

    interface GroupedAdapterViewHolder<T extends Identifiable> extends AdapterViewHolder<T> {

        void bindHeader(T currentObject, @Nullable T previousObject, View<T> view);

    }
}
