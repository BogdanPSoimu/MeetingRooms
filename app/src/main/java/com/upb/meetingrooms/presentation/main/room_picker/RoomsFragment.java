package com.upb.meetingrooms.presentation.main.room_picker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upb.meetingrooms.MyApplication;
import com.upb.meetingrooms.R;
import com.upb.meetingrooms.data.model.Interval;
import com.upb.meetingrooms.data.model.Room;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomsFragment extends Fragment {

    private static final String DATE_IN_MILLIS = "dateInMillis";
    private static final String TAG = RoomsFragment.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference userUpcomingRef;
    FirebaseAuth auth;


    @BindView(R.id.roomListRecyclerView)
    RecyclerView recyclerView;
    private RoomsRecyclerAdapter myRecyclerViewAdapter;
    long dateInMillis;

    private StringBuilder dateIdBuilder;

    public static RoomsFragment newInstance(@NonNull long dateInMillis) {

        Bundle args = new Bundle();
        args.putLong(DATE_IN_MILLIS, dateInMillis);

        RoomsFragment fragment = new RoomsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room_list, null);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle("Available Rooms");

        dateInMillis = getArguments().getLong(DATE_IN_MILLIS, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);

        dateIdBuilder = new StringBuilder();
        dateIdBuilder.append(calendar.get(Calendar.YEAR))
                .append(calendar.get(Calendar.MONTH))
                .append(calendar.get(Calendar.DAY_OF_MONTH));


        Log.d(TAG, "onCreateView: " + calendar.toString());
        auth = FirebaseAuth.getInstance();
        setupRecyclerView();


        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("rooms");
        userUpcomingRef = database.getReference("userUpcoming");

        roomRef.child(dateIdBuilder.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                List<Room> roomList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Room r = snapshot.getValue(Room.class);
                    roomList.add(r);
                }
                if (roomList.size() > 0) {
                    myRecyclerViewAdapter.setItems(roomList);
                } else {
                    setUpInitialRooms(dateIdBuilder.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void setUpInitialRooms(String dateId) {
        ArrayList<Room> rooms = new ArrayList<>();
        for (int i = 0; i < MyApplication.getRoomSparseArray().size(); i++) {
            int key = MyApplication.getRoomSparseArray().keyAt(i);
            rooms.add(MyApplication.getRoomSparseArray().get(key));
        }


        roomRef.child(dateId)
                .setValue(rooms);
    }

    public static String getTAG() {
        return TAG;
    }

    public void showTimePickerDialogStart(final Room room) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                double hour = getComposedHour(hourOfDay, minute);
                showTimePickerDialogEnd(hour, room);
            }
        }, true);


        List<Timepoint> selectableTimes = new ArrayList<>();

        for (int i = 0; i < 24; i++) {
            int key = i * 10;
            if (!room.getOcupationMap().get(key + "")) {
                selectableTimes.add(new Timepoint(i, 0));
            }
            key += 5;
            if (!room.getOcupationMap().get(key + "")) {
                selectableTimes.add(new Timepoint(i, 30));
            }
        }

        Timepoint[] tps = new Timepoint[selectableTimes.size()];
        tps = selectableTimes.toArray(tps);

        tpd.setSelectableTimes(tps);
        tpd.setTitle("Pick meeting start time");
        tpd.show(getActivity().getFragmentManager(), "Pick meeting start time");
    }

    private double getComposedHour(int hourOfDay, int minute) {
        double minutes = minute == 0 ? 0 : 0.5;
        return hourOfDay + minutes;
    }

    private void reserveOnFirebase(Room room, Interval interval) {
        roomRef.child(dateIdBuilder.toString())
                .child(room.getId() + "")
                .setValue(room);

        reserveForUserUpcoming(interval);
    }

    private void reserveForUserUpcoming(Interval interval) {
        userUpcomingRef
                .child(auth.getCurrentUser().getUid())
                .push()
                .setValue(interval);
    }


    private void showTimePickerDialogEnd(final double startHour, final Room room) {
        TimePickerDialog tpdE = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                double hourEnd = getComposedHour(hourOfDay, minute);
                int noIntervals = (int) ((hourEnd - startHour) / 0.5);
                Interval interval = room.reserverRoom(startHour, noIntervals, auth.getCurrentUser().getDisplayName(), dateInMillis);
                reserveOnFirebase(room, interval);

            }
        }, true);

        int nrOfFreeIntervals = room.getNrOfFreeIntervals(startHour);
        List<Timepoint> selectableTimepoints = new ArrayList<>();
        for (int i = 0; i < nrOfFreeIntervals; i++) {
            double newTp = startHour + ((i + 1) * 0.5);
            int hour = (int) newTp;
            int minutes = newTp == hour ? 0 : 30;
            selectableTimepoints.add(new Timepoint(hour, minutes));
        }

        Timepoint[] tps = new Timepoint[selectableTimepoints.size()];
        tps = selectableTimepoints.toArray(tps);

        tpdE.setSelectableTimes(tps);
        tpdE.setTitle("Pick meeting end time");
        tpdE.show(getActivity().getFragmentManager(), "Pick meeting end time");


    }

    private void setupRecyclerView() {
        myRecyclerViewAdapter = new RoomsRecyclerAdapter();
        myRecyclerViewAdapter.setOnItemClickListener(new RoomsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Room room) {
                showTimePickerDialogStart(room);

            }
        });
        recyclerView.setAdapter(myRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
