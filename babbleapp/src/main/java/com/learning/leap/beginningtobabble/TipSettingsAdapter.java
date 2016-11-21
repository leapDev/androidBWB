package com.learning.leap.beginningtobabble;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.learning.leap.beginningtobabble.models.TipReminder;

import java.util.List;

/**
 * Created by ryangunn on 9/26/16.
 */

public class TipSettingsAdapter extends RecyclerView.Adapter<TipSettingsAdapter.TipReminderViewHolder> {
    public List<TipReminder> mTipReminders;

    public TipSettingsAdapter(List<TipReminder> tipReminders){
        mTipReminders = tipReminders;
    }


    public class TipReminderViewHolder extends RecyclerView.ViewHolder{
        public TextView mReminderTime;
        public Switch mReminderSwitch;
        public TipReminderViewHolder(View itemView){
            super(itemView);
            mReminderTime = (TextView)itemView.findViewById(R.id.tipSettingsRecylerViewTimeTextView);
            mReminderSwitch = (Switch)itemView.findViewById(R.id.tipSettingReylerViewSwitch);
        }
    }

    @Override
    public int getItemCount() {
        return mTipReminders.size();
    }

    @Override
    public void onBindViewHolder(TipReminderViewHolder holder, int position) {
        TipReminder tipReminder = mTipReminders.get(position);

    }

    @Override
    public TipReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tipReminderView = inflater.inflate(R.layout.tip_settings_recylcer_view_layout, parent, false);

        // Return a new holder instance
        TipReminderViewHolder viewHolder = new TipReminderViewHolder(tipReminderView);
        return viewHolder;
    }
}
