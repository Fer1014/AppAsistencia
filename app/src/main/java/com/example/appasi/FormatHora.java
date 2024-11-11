package com.example.appasi;

import androidx.annotation.NonNull;

public class FormatHora {
    public String getFormatHora(String hora) {
        String format_hora="";
        if (hora.length()>3) {
            format_hora = String.valueOf(hora.charAt(0))+String.valueOf(hora.charAt(1))+":"+String.valueOf(hora.charAt(2))+String.valueOf(hora.charAt(3));
        }else{
            format_hora="0"+String.valueOf(hora.charAt(0))+":"+String.valueOf(hora.charAt(1))+String.valueOf(hora.charAt(2));
        }
        return format_hora;
    }
}
