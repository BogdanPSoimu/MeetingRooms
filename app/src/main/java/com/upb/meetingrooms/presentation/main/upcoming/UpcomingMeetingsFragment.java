package com.upb.meetingrooms.presentation.main.upcoming;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upb.meetingrooms.R;
import com.upb.meetingrooms.data.model.Interval;
import com.upb.meetingrooms.data.model.Room;
import com.upb.meetingrooms.presentation.main.room_picker.RoomsRecyclerAdapter;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpcomingMeetingsFragment extends Fragment {

    public static String TAG = UpcomingMeetingsFragment.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference upcomingRef;
    FirebaseAuth auth;
    UpcomingRecyclerAdapter adapter;

    @BindView(R.id.upcomingRecycler)
    RecyclerView upcomingRecycler;

    private HashMap<Interval,String> intervalKeysMap;


    public static UpcomingMeetingsFragment newInstance() {

        Bundle args = new Bundle();

        UpcomingMeetingsFragment fragment = new UpcomingMeetingsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming_meetings,null);
        ButterKnife.bind(this,rootView);
        intervalKeysMap = new HashMap<>();

        getActivity().setTitle("Upcoming Meetings");

        database = FirebaseDatabase.getInstance();
        upcomingRef = database.getReference("userUpcoming");
        auth = FirebaseAuth.getInstance();

        setUpUpcomingRecycler();

        upcomingRecycler.setAdapter(adapter);


        upcomingRef
                .child(auth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                List<Interval> intervalList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Interval i = snapshot.getValue(Interval.class);
                    intervalList.add(i);
                    intervalKeysMap.put(i,snapshot.getKey());
                }

                Collections.sort(intervalList,new IntervalListComparator());

                adapter.setItems(intervalList);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return rootView;
    }


    void setUpUpcomingRecycler(){
        adapter = new UpcomingRecyclerAdapter();
        adapter.setOnItemClickListener(new UpcomingRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Interval interval) {
                Log.d(TAG, "onItemClick: "+interval.getStartPeriod() + "  "+interval.getNrPeriods());
                showCancelEventDialog(interval);
            }
        });
        upcomingRecycler.setAdapter(adapter);
        upcomingRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void showCancelEventDialog(final Interval interval) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cancel meeting")
                .setMessage("Are you sure you want to cancel the selected meeting ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMeetingOnFirebase(interval);
                    }
                })
                .setNegativeButton("Cancel",null);

        builder.setCancelable(false)
                .show();



    }

    private void deleteMeetingOnFirebase(final Interval interval) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(interval.getDateInMillis());

        final StringBuilder dateIdBuilder = new StringBuilder();
        dateIdBuilder.append(calendar.get(Calendar.YEAR))
                .append(calendar.get(Calendar.MONTH))
                .append(calendar.get(Calendar.DAY_OF_MONTH));




        database.getReference("rooms")
                .child(dateIdBuilder.toString())
                .child(interval.getRoomId()+"")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Room room = dataSnapshot.getValue(Room.class);
                        Log.d(TAG, "onDataChange: "+room);
                        updateRoomOnFirebase(room,interval,dateIdBuilder.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void updateRoomOnFirebase(Room room,Interval interval,String dateKey) {
        for (Interval interval1 : room.getIntervals()) {
            if(interval.getDateInMillis() == interval1.getDateInMillis()) {
                room.getIntervals().remove(interval1);
                break;
            }
        }
        for (double i = interval.getStartPeriod(); i <interval.getStartPeriod()+(0.5 * interval.getNrPeriods())  ; i+=0.5) {
            String key = ((int) (i*10))+"";
            room.getOcupationMap().put(key,false);
        }

        database
                .getReference("rooms")
                .child(dateKey)
                .child(room.getId()+"")
                .setValue(room);

        String s = intervalKeysMap.get(interval);

        database.getReference("userUpcoming")
                .child(auth.getCurrentUser().getUid())
                .child(s)
                .removeValue();


    }


}
