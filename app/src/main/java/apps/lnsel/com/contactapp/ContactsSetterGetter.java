package apps.lnsel.com.contactapp;

/**
 * Created by apps2 on 7/17/2017.
 */
public class ContactsSetterGetter {
    private String cntId;
    private String cntPersonName;
    private String cntContactNo;
    private String cntEmail;
    private String cntAddress;
    private String cntImage;
    private String cntImageStatus;
    private String cntStatus;


    public ContactsSetterGetter(String cntId, String cntPersonName, String cntContactNo, String cntEmail, String cntAddress, String cntImage, String cntImageStatus, String cntStatus) {
        this.cntId = cntId;
        this.cntPersonName = cntPersonName;
        this.cntContactNo = cntContactNo;
        this.cntEmail = cntEmail;
        this.cntAddress = cntAddress;
        this.cntImage = cntImage;
        this.cntImageStatus = cntImageStatus;
        this.cntStatus = cntStatus;
    }

    public String getCntId() {
        return this.cntId;
    }

    public String getCntPersonName() {
        return this.cntPersonName;
    }

    public String getCntContactNo() {
        return this.cntContactNo;
    }

    public String getCntEmail() {
        return this.cntEmail;
    }

    public String getCntAddress() {
        return this.cntAddress;
    }

    public String getCntImage() {
        return this.cntImage;
    }

    public String getCntImageStatus() {
        return this.cntImageStatus;
    }

    public String getCntStatus() {
        return this.cntStatus;
    }
}
