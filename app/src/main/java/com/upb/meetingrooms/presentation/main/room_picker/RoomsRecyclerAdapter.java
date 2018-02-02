package com.upb.meetingrooms.presentation.main.room_picker;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.upb.meetingrooms.R;
import com.upb.meetingrooms.data.model.Interval;
import com.upb.meetingrooms.data.model.Room;
import com.upb.meetingrooms.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RoomsRecyclerAdapter extends RecyclerView.Adapter<RoomsRecyclerAdapter.MyViewHolder> implements View.OnClickListener {


    private List<Room> roomList;

    private OnItemClickListener onItemClickListener;

    RoomsRecyclerAdapter() {
        this.roomList = new ArrayList<>();
    }

    public void setItems(List<Room> roomList) {
        this.roomList.clear();
        this.roomList.addAll(roomList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        int i = ((ViewGroup) view.getParent()).indexOfChild(view);
        onItemClickListener.onItemClick(view,roomList.get(i));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_element, parent, false);
        view.setOnClickListener(this);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Room room = roomList.get(position);
        holder.roomName.setText(room.getName());

        ArrayList<Interval> intervals = room.getIntervals();
        if (intervals != null && intervals.size() > 0) {
            //TODO sort intervals before adding them to view
            Collections.sort(intervals,new IntervalComparatorByHourStarting());
            holder.ocupationContainer.removeAllViews();
            for (Interval interval : intervals) {
                TextView tv = new TextView(holder.ocupationContainer.getContext());
                double start = interval.getStartPeriod();
                double finish = interval.getStartPeriod() + 0.5 * interval.getNrPeriods();
                tv.setText("    "+StringUtils.getHourString(start) + " - " + StringUtils.getHourString(finish) + "    "+ interval.getUserName());
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.red_circle,0,0,0);
                tv.setTextColor(ContextCompat.getColor(tv.getContext(),R.color.mdtp_white));
                holder.ocupationContainer.addView(tv);
            }
        } else {
            holder.ocupationContainer.removeAllViews();
            TextView tv = new TextView(holder.ocupationContainer.getContext());
            tv.setText("No upcoming meetings in this room");
            tv.setTextColor(ContextCompat.getColor(tv.getContext(),R.color.mdtp_white));
            holder.ocupationContainer.addView(tv);
        }

    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.roomName)
        TextView roomName;

        @BindView(R.id.ocupationContainer)
        LinearLayout ocupationContainer;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view,Room room);
    }



}
