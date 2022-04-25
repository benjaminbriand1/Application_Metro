package com.example.android.bluetoothchat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class ProgrammationMode {
    Button bProgrammation, bParametres, bTest, bFlash, bValider;
    TextView info, textView2, length, width, amplitude, way;
    Switch Sallumer, Scapteur;
    ImageView retour;
    Spinner Sseuil, artworkAmplitude, flashWidth;

    /* les differents etats possible sont : test, parametres, programmation */
    private String state;
    String tab_state[] = new String[5];
    int numstate = 0;

    public ProgrammationMode(Button bProgrammation, Button bParametres, Button bTest, Button bFlash, Button bValider,
                             Spinner Sseuil, Spinner artworkAmplitude, Spinner flashWidth, TextView info, TextView textView2,
                             TextView length, TextView width, TextView amplitude, TextView way,
                             Switch Sallumer, Switch Scapteur,
                             ImageView retour){

        this.bProgrammation = bProgrammation;
        this.bTest = bTest;
        this.bParametres = bParametres;
        this.bFlash = bFlash;
        this.bValider = bValider;
        this.Sseuil = Sseuil;
        this.artworkAmplitude = artworkAmplitude;
        this.flashWidth = flashWidth;
        this.info = info;
        this.textView2 = textView2;
        this.length = length;
        this.width = width;
        this.amplitude = amplitude;
        this.way = way;
        this.Sallumer = Sallumer;
        this.Scapteur = Scapteur;
        this.retour = retour;

        state = "programmation";
        setWay(state);
        bProgrammation.setVisibility(View.INVISIBLE);
        info.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);

        bTest.setVisibility(View.VISIBLE);
        bParametres.setVisibility(View.VISIBLE);
        retour.setVisibility(View.VISIBLE);
        way.setVisibility(View.VISIBLE);

        bTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTest();
                setWay(state);
                }
        });

        bParametres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setParametres();
                setWay(state);
            }
        });
    }

    public void setScreen(String state){
        System.out.println(numstate+ " "+ state);
        if (state == "programmation") setProgammation();
        else if (state == "test") setTest();
        else if (state == "parametres") setParametres();
        else if (state == "connect") setConnect();

    }

    public void setWay(String state){
        this.numstate++;
        tab_state[numstate] = state;

        way.append(" > "+state);
        System.out.println(numstate+ " set way : "+ state);
    }

    public void setTest(){
        this.state = "test";
        bTest.setClickable(false);
        bParametres.setVisibility(View.INVISIBLE);

        bFlash.setVisibility(View.VISIBLE);
        Sallumer.setVisibility(View.VISIBLE);
        Scapteur.setVisibility(View.VISIBLE);
    }

    public void setParametres(){
        bParametres.setClickable(false);
        bTest.setVisibility(View.INVISIBLE);

        Sseuil.setVisibility(View.VISIBLE);
        artworkAmplitude.setVisibility(View.VISIBLE);
        flashWidth.setVisibility(View.VISIBLE);
        width.setVisibility(View.VISIBLE);
        length.setVisibility(View.VISIBLE);
        amplitude.setVisibility(View.VISIBLE);
        bValider.setVisibility(View.VISIBLE);
        this.state = "parametres";
    }

    public void setProgammation(){
        this.state="programmation";
        bTest.setVisibility(View.VISIBLE);
        bParametres.setVisibility(View.VISIBLE);

        bParametres.setClickable(true);
        bTest.setClickable(true);
        Sseuil.setVisibility(View.INVISIBLE);
        artworkAmplitude.setVisibility(View.INVISIBLE);
        flashWidth.setVisibility(View.INVISIBLE);
        width.setVisibility(View.INVISIBLE);
        length.setVisibility(View.INVISIBLE);
        amplitude.setVisibility(View.INVISIBLE);
        bValider.setVisibility(View.INVISIBLE);

        bFlash.setVisibility(View.INVISIBLE);
        Scapteur.setVisibility(View.INVISIBLE);
        Sallumer.setVisibility(View.INVISIBLE);

        way.setText(" > programmation");
    }

    public void setConnect(){
        this.state="connect";
        bProgrammation.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);
        textView2.setVisibility(View.VISIBLE);

        bTest.setVisibility(View.INVISIBLE);
        bParametres.setVisibility(View.INVISIBLE);
        retour.setVisibility(View.INVISIBLE);
        way.setVisibility(View.INVISIBLE);

        Sseuil.setVisibility(View.INVISIBLE);

        way.setText("");
    }

    public String getState(){
        return this.state;
    }

    public void setState(String state){
        this.state=state;
    }

}