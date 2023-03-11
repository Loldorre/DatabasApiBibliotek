

import java.util.Date;

public class Loan {

    private int bookID;
    private int accountID;
    private Date dateOfLoan;


    public Loan(int bookID, int accountID, Date dateOfLoan){
        this.bookID = bookID;
        this.accountID = accountID;
        this.dateOfLoan = dateOfLoan;
    }

    public int getBid() {
        return bookID;
    }

    public void setBid(int bookID) {
        this.bookID = bookID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public Date getDateOfLoan() {
        return dateOfLoan;
    }

    public void setDateOfLoan(Date dateOfLoan) {
        this.dateOfLoan = dateOfLoan;
    }
}
