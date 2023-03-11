public interface IDatabase {

    public Book[] getAvailability ();
    public int createLoan (int accountID, int bookID);
    public int addToBlacklist(long personNr);
    public int regAccount(String fname, String  ename, long personNr, int role);
    public int terminateAccount(int accountID);
    public Account[] getAccount();
    public int regTempBan(int accountID, int daysOfBan);
    public int removeLoan(int bookID);
    public int updateReturnDelays(int accountID);
    public int updateAmountOfBans(int accountID);
    public long[] getBlacklisted();
    public Loan[] getLoans();

}