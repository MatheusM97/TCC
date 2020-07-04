package br.ufms.nafmanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class CustomDatePickerFragment extends DialogFragment {

    private TextView tvData;
    private TextView tvHora;
    private int q;

    public CustomDatePickerFragment(TextView tvData, TextView tvHora, int q) {
        this.tvData = tvData;
        this.tvHora = tvHora;
        this.q = q;
    }

    public CustomDatePickerFragment(TextView tvData, int q) {
        this.tvData = tvData;
        this.q = q;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        TimePickerDialog dialog2 = new TimePickerDialog(getActivity(), timeSetListener, hora, minuto, true);

        if(q == 0){
            return  dialog;
        }

        return dialog2;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    tvData.setText((day < 10 ? "0" + day : day) + "/" + (month < 10 ? "0" + month : month) + "/" + year);
                }
            };

    private TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hora, int minuto) {
            tvHora.setText((hora < 10 ? "0" + hora : hora) + ":" + (minuto < 10 ? "0" + minuto : minuto) + ":" + "00");
                }
            };
}

