package com.jcxy.MobileSafe.bean;

/**
 * Created by hp on 2016/10/11.
 */

public class Virus {

    private String Pname;
    private String isVirus;
    private String packName;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getIsVirus() {
        return isVirus;
    }

    public void setIsVirus(String isVirus) {
        this.isVirus = isVirus;
    }
}
