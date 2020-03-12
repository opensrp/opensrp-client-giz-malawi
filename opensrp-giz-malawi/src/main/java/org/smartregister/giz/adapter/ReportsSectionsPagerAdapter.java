package org.smartregister.giz.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.smartregister.giz.R;
import org.smartregister.giz.activity.HIA2ReportsActivity;
import org.smartregister.giz.fragment.DailyTalliesFragment;
import org.smartregister.giz.fragment.DraftMonthlyFragment;
import org.smartregister.giz.fragment.SentMonthlyFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ReportsSectionsPagerAdapter extends FragmentPagerAdapter {

    private HIA2ReportsActivity hia2ReportsActivity;

    public ReportsSectionsPagerAdapter(HIA2ReportsActivity hia2ReportsActivity, FragmentManager fm) {
        super(fm);
        this.hia2ReportsActivity = hia2ReportsActivity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return DailyTalliesFragment.newInstance();
            case 1:
                return DraftMonthlyFragment.newInstance();
            case 2:
                return SentMonthlyFragment.newInstance();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return hia2ReportsActivity.getString(R.string.hia2_daily_tallies);
            case 1:
                return hia2ReportsActivity.getString(R.string.hia2_draft_monthly);
            case 2:
                return hia2ReportsActivity.getString(R.string.hia2_sent_monthly);
            default:
                break;
        }
        return null;
    }
}
