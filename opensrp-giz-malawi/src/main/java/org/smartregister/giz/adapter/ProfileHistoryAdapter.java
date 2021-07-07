package org.smartregister.giz.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.giz.R;
import org.smartregister.giz.domain.ProfileHistory;
import org.smartregister.giz.util.VisitUtils;
import org.smartregister.giz.viewholder.GroupedListableViewHolder;
import org.smartregister.view.ListContract;

import java.util.List;

import static org.smartregister.opd.utils.OpdUtils.context;

public class ProfileHistoryAdapter extends GroupedListableAdapter<ProfileHistory, GroupedListableViewHolder<ProfileHistory>> {

    public ProfileHistoryAdapter(List<ProfileHistory> items, ListContract.View<ProfileHistory> view) {
        super(items, view);
    }

    @Override
    public void reloadData(@Nullable List<ProfileHistory> items) {
        super.reloadData(items);
    }

    @NonNull
    @Override
    public GroupedListableViewHolder<ProfileHistory> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_history_row, parent, false);
        return new ProfileHistoryViewHolder(view);
    }

    public static class ProfileHistoryViewHolder extends GroupedListableViewHolder<ProfileHistory> {

        private View currentView;
        private TextView tvHeader;
        private TextView tvEvent;
        private TextView tvEdit;

        private ProfileHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            currentView = itemView;
            tvHeader = itemView.findViewById(R.id.tvHeader);
            tvEvent = itemView.findViewById(R.id.tv_action);
            tvEdit = itemView.findViewById(R.id.tv_edit);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void bindView(ProfileHistory history, ListContract.View<ProfileHistory> view) {
            tvEvent.setText(history.getEventTime() + " " + VisitUtils.getTranslatedVisitTypeName(history.getEventType()));
            tvEdit.setOnClickListener(v -> view.onListItemClicked(history, v.getId()));


            //This might Change Depending on View Functionality
            if(history.getEventDate() != context().getStringResource(R.string.today)){
                tvEdit.setText(context().getStringResource(R.string.view));
            }
        }

        @Override
        public void resetView() {
            tvHeader.setText("");
            tvHeader.setVisibility(View.GONE);

            tvEvent.setText("");
            tvEdit.setOnClickListener(null);
        }

        @Override
        public void bindHeader(ProfileHistory currentObject, @Nullable ProfileHistory previousObject, ListContract.View<ProfileHistory> view) {
            if (previousObject == null || !currentObject.getEventDate().equals(previousObject.getEventDate())) {
                tvHeader.setVisibility(View.VISIBLE);
                tvHeader.setText(currentObject.getEventDate());
            }
        }
    }
}
