package com.upb.meetingrooms.presentation.main.upcoming;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upb.meetingrooms.MyApplication;
import com.upb.meetingrooms.R;
import com.upb.meetingrooms.data.model.Interval;
import com.upb.meetingrooms.data.model.Room;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class UpcomingRecyclerAdapter extends RecyclerView.Adapter<UpcomingRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    private List<Interval> intervalList;

    private OnItemClickListener onItemClickListener;

    UpcomingRecyclerAdapter() {
        this.intervalList = new ArrayList<>();
    }

    public void setItems(List<Interval> intervals) {
        this.intervalList.clear();
        this.intervalList.addAll(intervals);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        int i = ((ViewGroup) view.getParent()).indexOfChild(view);
        onItemClickListener.onItemClick(view, intervalList.get(i));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upcoming, parent, false);
        view.setOnClickListener(this);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Interval interval = intervalList.get(position);
        holder.roomName.setText(MyApplication.getRoomSparseArray().get((int) interval.getRoomId()).getName());
        holder.dateText.setText(getDateString(interval.getDateInMillis()));
        holder.intervalText.setText(getIntervalString(interval));

    }

    private String getDateString(long timeStamp) {
        String date = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        date = calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR);

        return date;
    }

    private String getIntervalString(Interval interval) {
        String intervalString = "";

        int hour = (int) interval.getStartPeriod();
        int minutes = hour == interval.getStartPeriod() ? 0 : 30;
        String startInt = hour +":" + minutes;
        if(minutes == 0) {
            startInt+="0";
        }

        double finishTime = interval.getStartPeriod()+ (0.5 * interval.getNrPeriods());
        hour = (int) finishTime;
        minutes = finishTime == hour ? 0: 30;
        String finishInterval = hour +":" + minutes;
        if(minutes == 0) {
            finishInterval+="0";
        }

        intervalString = startInt + " - " + finishInterval;


        return intervalString;

    }


    @Override
    public int getItemCount() {
        return intervalList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.roomName)
        TextView roomName;

        @BindView(R.id.dateText)
        TextView dateText;

        @BindView(R.id.intervalText)
        TextView intervalText;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, Interval interval);
    }





}
