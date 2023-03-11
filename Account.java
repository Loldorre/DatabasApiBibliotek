
import java.util.Date;

public class Account {
    private String fName;
    private String eName;

    /*
    Kontots roll avgör hur många aktiva lån som får finnas samtidigt
    Max lån:
    undergraduate = 3
    postgraduate = 5
    candidate = 7
    teacher = 10
    */
    private int role;
    private long personNr;


    private int accountID;
    private Date endOfBan;
    Loan[]  borrowedBooks;
    private int amountOfBans;
    private int amountOfBookDelays;

    public Account (String fName, String eName, long personNr, int role, int accountID, Date endOfBan,Loan[] borrowedBooks, int amountOfBans, int amountOfBookDelays) {

        this.fName = fName;
        this.eName = eName;
        this.role = role;
        this.personNr = personNr;
        this.accountID = accountID;
        this.endOfBan = endOfBan;
        this.borrowedBooks = borrowedBooks;
        this.amountOfBans = amountOfBans;
        this.amountOfBookDelays = amountOfBookDelays;
    }
    public Date getEndOfBan(){
        return this.endOfBan;
    }
    public Loan[] getBorrowedBooks() {
        return borrowedBooks;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public long getPersonNr() {
        return personNr;
    }

    public void setPersonNr(long personNr) {
        this.personNr = personNr;
    }
    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public Date isBanned() {
        return endOfBan;
    }

    public void setEndOfBan(Date endOfBan) {
        this.endOfBan = endOfBan;
    }

    public int getAmountOfBans() {
        return amountOfBans;
    }

    public void setAmountOfBans(int amountOfBans) {
        this.amountOfBans = amountOfBans;
    }

    public int getAmountOfBookDelays() {
        return amountOfBookDelays;
    }

    public void setAmountOfBookDelays(int amountOfBookDelays) {
        this.amountOfBookDelays = amountOfBookDelays;
    }
}