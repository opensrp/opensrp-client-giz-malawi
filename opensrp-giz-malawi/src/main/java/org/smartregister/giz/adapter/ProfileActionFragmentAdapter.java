package org.smartregister.giz.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.smartregister.giz.R;
import org.smartregister.giz.domain.ProfileAction;
import org.smartregister.view.ListContract;
import org.smartregister.view.adapter.ListableAdapter;
import org.smartregister.view.viewholder.ListableViewHolder;

import java.util.List;

public class ProfileActionFragmentAdapter extends ListableAdapter<ProfileAction, ListableViewHolder<ProfileAction>> {

    public ProfileActionFragmentAdapter(List<ProfileAction> items, ListContract.View<ProfileAction> view) {
        super(items, view);
    }

    @NonNull
    @Override
    public ListableViewHolder<ProfileAction> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_action_fragment_item, parent, false);
        return new ProfileActionHolder(view);
    }

    public static class ProfileActionHolder extends ListableViewHolder<ProfileAction> {

        private TextView tvAction;
        private View currentView;
        private LinearLayout llHistory;

        private ProfileActionHolder(@NonNull View itemView) {
            super(itemView);
            currentView = itemView;
            tvAction = itemView.findViewById(R.id.tvAction);
            llHistory = itemView.findViewById(R.id.ll_history);
        }

        @Override
        public void bindView(ProfileAction profileAction, ListContract.View<ProfileAction> view) {
            tvAction.setText(profileAction.getName());

            currentView.setOnClickListener(v -> view.onListItemClicked(profileAction.setSelectedAction(null), v.getId()));

            // add all the children
            if (profileAction.getVisits() != null) {
                for (ProfileAction.ProfileActionVisit paVisit : profileAction.getVisits()) {
                    View childView = LayoutInflater.from(llHistory.getContext()).inflate(R.layout.profile_action_history, null, false);
                    TextView tvAction = childView.findViewById(R.id.tv_action);
                    tvAction.setText(paVisit.getVisitTime());

                    childView.findViewById(R.id.tv_edit).setOnClickListener(v -> view.onListItemClicked(profileAction.setSelectedAction(paVisit), v.getId()));

                    llHistory.addView(childView);
                }
            }
        }

        @Override
        public void resetView() {
            tvAction.setText("");
            llHistory.removeAllViews();
        }
    }
}
