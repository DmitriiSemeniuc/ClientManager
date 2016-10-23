package com.semeniuc.dmitrii.clientmanager.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.semeniuc.dmitrii.clientmanager.MyApplication;
import com.semeniuc.dmitrii.clientmanager.R;
import com.semeniuc.dmitrii.clientmanager.model.Appointment;
import com.semeniuc.dmitrii.clientmanager.repository.AppointmentRepository;
import com.semeniuc.dmitrii.clientmanager.utils.Constants;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class AppointmentReviewActivity extends AppointmentActivity {

    public static final String LOG_TAG = AppointmentReviewActivity.class.getSimpleName();

    private Context mContext = MyApplication.getInstance().getApplicationContext();
    private Appointment mAppointment;

    @BindView(R.id.appointment_client_name)
    AppCompatEditText clientName;
    @BindView(R.id.appointment_client_phone)
    AppCompatEditText clientPhone;
    @BindView(R.id.appointment_service)
    AppCompatEditText service;
    @BindView(R.id.appointment_info)
    AppCompatEditText info;
    @BindView(R.id.appointment_calendar_date)
    AppCompatTextView date;
    @BindView(R.id.appointment_time)
    AppCompatTextView time;

    @OnClick(R.id.appointment_calendar_icon)
    void onCalendarIconClicked() {
        showPickerDialog(DATE_PICKER_DIALOG_ID);
    }
    @OnClick(R.id.appointment_calendar_date)
    void onCalendarDateClicked() {
        showPickerDialog(DATE_PICKER_DIALOG_ID);
    }
    @OnClick(R.id.appointment_time_icon)
    void onClockIconClicked() {
        showPickerDialog(TIME_PICKER_DIALOG_ID);
    }
    @OnClick(R.id.appointment_time)
    void onClockClicked() {
        showPickerDialog(TIME_PICKER_DIALOG_ID);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        mAppointment = bundle.getParcelable(Constants.APPOINTMENT_PATH);
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateAppointmentFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Toolbar menu with Delete|Update options
        inflater.inflate(R.menu.appointment_review_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_appointment:
                if (mUtils.isAppointmentFormValid()) {
                    setDataFromFields();
                    new UpdateAppointment().execute();
                } else {
                    hideKeyboard();
                }
                break;
            case R.id.action_delete_appointment:
                new DeleteAppointment().execute();
                break;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_PICKER_DIALOG_ID) {
            return getDatePickerDialog();
        } else if (id == TIME_PICKER_DIALOG_ID) {
            return getTimePickerDialog();
        } else {
            return null;
        }
    }

    /**
    * Fill appointment form fields with coming data
    * */
    public void populateAppointmentFields() {
        clientName.setText(mAppointment.getClientName());
        clientPhone.setText(mAppointment.getClientPhone());
        service.setText(mAppointment.getService());
        info.setText(mAppointment.getInfo());
        date.setText(mUtils.convertDateToString(mAppointment.getDate(), Constants.DATE_FORMAT));
        time.setText(mUtils.convertDateToString(mAppointment.getDate(), Constants.TIME_FORMAT));
    }

    /**
    * Open date picker dialog with date coming from Appointment
    * */
    private DatePickerDialog getDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        // Set date for dialog coming from appointment
        Date dateForDialog = mUtils.convertStringToDate(date.getText().toString(), Constants.DATE_FORMAT);
        calendar.setTime(dateForDialog);
        return new DatePickerDialog(
                this, datePickerListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
    * Open time picker dialog with time coming from Appointment
    * */
    private TimePickerDialog getTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        // Set time for dialog coming from appointment
        Date dateForDialog = mUtils.convertStringToDate(time.getText().toString(), Constants.TIME_FORMAT);
        calendar.setTime(dateForDialog);
        return new TimePickerDialog(this, timePickerListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
    }

    private class UpdateAppointment extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            Appointment appointment = new Appointment(mAppointment.getId(), MyApplication.getInstance().getUser(),
                    mClientName, mClientPhone, mService, mInfo, mDateTime);
            AppointmentRepository appointmentRepo = new AppointmentRepository(mContext);
            return appointmentRepo.update(appointment);
        }

        @Override
        protected void onPostExecute(Integer updated) {
            mUtils.showUpdateResultMessage(updated, AppointmentReviewActivity.this);
            if (updated == Constants.UPDATED) finish();
        }
    }

    private class DeleteAppointment extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            AppointmentRepository appointmentRepo = new AppointmentRepository(mContext);
            return appointmentRepo.delete(mAppointment);
        }

        @Override
        protected void onPostExecute(Integer deleted) {
            mUtils.showDeleteResultMessage(deleted, AppointmentReviewActivity.this);
            if (deleted == Constants.DELETED) finish();
        }
    }
}
