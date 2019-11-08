package br.ufms.nafmanager.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class customDatePickerFragment extends DialogFragment {

    public TextView textView;
    public customDatePickerFragment(TextView view){
        this.textView = view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = new GregorianCalendar();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        if(this.textView.getText() != null && this.textView.getText().toString().length() >0){
            String[] separada = this.textView.getText().toString().split("/");
            dia = Integer.parseInt(separada[0]);
            mes = Integer.parseInt(separada[1]);
            ano = Integer.parseInt(separada[2]);
        }

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), dateSetListener, ano, mes, dia);
        return dpd;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                    textView.setText(dia + "/" + mes + "/" + ano);
                }
            };

}

