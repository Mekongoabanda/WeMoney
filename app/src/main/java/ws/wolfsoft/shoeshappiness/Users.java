package ws.wolfsoft.shoeshappiness;



/**
 * Created by HP on 24/10/2017.
 */

public class Users {
    public String mnom;
    public String mimage;
    public String mstatus;

  public Users (){


}

    public Users(String nom, String image, String status) {
        mnom = nom;
        mimage = image;
        mstatus = status;
    }

    public String getNom() {
        return mnom;
    }

    public void setNom(String nom) {
        mnom = nom;
    }

    public String getImage() {
        return mimage;
    }

    public void setImage(String image) {
        mimage = image;
    }

    public String getStatus() {
        return mstatus;
    }

    public void setStatus(String status) {
        mstatus = status;
    }
}