package com.example.repulo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Jegy implements Serializable {
    enum JegyTipus{
        ELSO_OSZTALY,
        BUSINESS,
        FAPADOS
    }

    private String id;
    private  String honnan;
    private String hova;
    private int szekSzam;
    private JegyTipus jegyTipus;
    private LocalDateTime indulasiIdo;
    private int ar;
    private boolean megvett;
    private String vevoId;

    public Jegy(String id, String honnan, String hova, int szekSzam, String jegyTipus, LocalDateTime indulasiIdo, int ar, boolean megvett, String vevoId) {
        this.id = id;
        this.honnan = honnan;
        this.hova = hova;
        this.szekSzam = szekSzam;
        switch (jegyTipus){
            case "ELSO_OSZTALY":
                this.jegyTipus = JegyTipus.ELSO_OSZTALY;
                break;
            case "BUSINESS":
                this.jegyTipus = JegyTipus.BUSINESS;
                break;
            case "FAPADOS":
                this.jegyTipus = JegyTipus.FAPADOS;
                break;
        }
        this.indulasiIdo = indulasiIdo;
        this.ar = ar;
        this.megvett = megvett;
        this.vevoId = vevoId;
    }

    public boolean isMegvett() {
        return megvett;
    }

    public void setMegvett(boolean megvett) {
        this.megvett = megvett;
    }

    public String getVevoId() {
        return vevoId;
    }

    public void setVevoId(String vevoId) {
        this.vevoId = vevoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHonnan() {
        return honnan;
    }

    public void setHonnan(String honnan) {
        this.honnan = honnan;
    }

    public String getHova() {
        return hova;
    }

    public void setHova(String hova) {
        this.hova = hova;
    }

    public int getSzekSzam() {
        return szekSzam;
    }

    public void setSzekSzam(int szekSzam) {
        this.szekSzam = szekSzam;
    }

    public JegyTipus getJegyTipus() {
        return jegyTipus;
    }

    public void setJegyTipus(JegyTipus jegyTipus) {
        this.jegyTipus = jegyTipus;
    }

    public String getIndulasiIdoString(){
        return this.indulasiIdo.toString();
    }

    public String getIndulasiIdo() {
        String date = this.indulasiIdo.toString().split("T")[0];
        String time = this.indulasiIdo.toString().split("T")[1].split(":")[0]+":"+this.indulasiIdo.toString().split("T")[1].split(":")[1];
        return date + " " + time;
    }

    public void setIndulasiIdo(LocalDateTime indulasiIdo) {
        indulasiIdo = indulasiIdo;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }
}
