package com.upb.meetingrooms.presentation.main.day_picker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.upb.meetingrooms.R;
import com.upb.meetingrooms.presentation.main.MainActivity;
import com.upb.meetingrooms.presentation.main.room_picker.RoomsFragment;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DayPickerFragment extends Fragment{


    public static final String TAG = DayPickerFragment.class.getSimpleName();


    @BindView(R.id.datePicker)
    DatePicker datePicker;


    public static DayPickerFragment newInstance() {
        Bundle args = new Bundle();
        DayPickerFragment fragment = new DayPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day_picker,null);
        ButterKnife.bind(this,rootView);
        Calendar cal = Calendar.getInstance();
        datePicker.setMinDate(cal.getTimeInMillis());

        getActivity().setTitle("Pick date");

        return rootView;
    }

    public static String getTAG() {
        return TAG;
    }

    @OnClick(R.id.goToAvailableRooms)
    void goToAvailableRoomsClicked(){
        ((MainActivity) getActivity()).goToFragment(RoomsFragment.newInstance(getDateFromDatePicker(datePicker)),true);
    }

    public static long getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTimeInMillis();
    }
}
