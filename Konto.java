

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class Konto {
    private String fNamn;
    private String eNamn;
    /* -- undergraduate,postgraduate,candidate,teacher (måste vara definierat så man vet vad man ska sätta in vid registrering... borde egentligen vara en int tycker jag)
    Max lån:
    undergraduate = 3
    postgraduate = 5
    candidate = 7
    teacher = 10
    * */
    private int roll;
    private long personNr;


    private int kontoID;
    private Date avstangd;
    Lån[]  lånadeBöcker;
    private int antalAvstangningar;
    private int antalForseningar;

    public Konto (String fNamn, String eNamn, long personNr, int roll, int kontoID, Date avstangd,Lån[] lån, int antalAvstangningar, int antalForseningar) {

        this.fNamn = fNamn;
        this.eNamn = eNamn;
        this.roll = roll;
        this.personNr = personNr;
        this.kontoID = kontoID;
        this.avstangd = avstangd;
        this.lånadeBöcker = lån;
        this.antalAvstangningar = antalAvstangningar;
        this.antalForseningar = antalForseningar;
    }
    public Date getAvstangd(){
        return this.avstangd;
    }
    public Lån[] getLånadeBöcker() {
        return lånadeBöcker;
    }

    public String getfNamn() {
        return fNamn;
    }

    public void setfNamn(String fNamn) {
        this.fNamn = fNamn;
    }

    public String geteNamn() {
        return eNamn;
    }

    public void seteNamn(String eNamn) {
        this.eNamn = eNamn;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public long getPersonNr() {
        return personNr;
    }

    public void setPersonNr(long personNr) {
        this.personNr = personNr;
    }
    public int getKontoID() {
        return kontoID;
    }

    public void setKontoID(int kontoID) {
        this.kontoID = kontoID;
    }

    public Date isAvstangd() {
        return avstangd;
    }

    public void setAvstangd(Date avstangd) {
        this.avstangd = avstangd;
    }

    public int getAntalAvstangningar() {
        return antalAvstangningar;
    }

    public void setAntalAvstangningar(int antalAvstangningar) {
        this.antalAvstangningar = antalAvstangningar;
    }

    public int getAntalForseningar() {
        return antalForseningar;
    }

    public void setAntalForseningar(int antalForseningar) {
        this.antalForseningar = antalForseningar;
    }
}