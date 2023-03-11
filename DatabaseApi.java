
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.time.ZoneId;

public class DatabaseApi implements IDatabase {
    private Connection connection;
    private static Logger logger = LogManager.getLogger();
    public DatabaseApi() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://192.168.50.101:3306/1ik173-server", "Dorian", "Dorian1234");
            logger.debug("Databas ansluten ----->");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //----------------------------------------------------------------------------------------
    @Override
    //Hämtar en array av böcker som finns i Bok-tabellen och inte inte lån-tabellen
    public Book[] getAvailability() {
        logger.debug("hämtaTillgänglighet ----->");
        //arrayOfBooks used with .toArray to create the return array
        ArrayList<Book> arrayOfBooks = new ArrayList<>();

        //Getting an array of book with titel which is then returned
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "SELECT distinct bok.titel, bok.författare, bok.utgivningsår, samling.bid,samling.isbn FROM samling,lån,bok where bok.isbn = samling.isbn and\n" +
                    "samling.bid not in (select bid from lån) order by bid;";
            logger.debug("hämtar tillgängliga böcker ");
            ResultSet rS = stmt.executeQuery(getTitel);
            while (rS.next()) {
                logger.debug("lägger till: " + rS.getString("titel"),rS.getInt("bid"));
                arrayOfBooks.add(new Book(rS.getInt("bid"),rS.getInt("isbn"), rS.getString("titel"), rS.getString("författare"), rS.getInt("utgivningsår")));
            }
        } catch (SQLException e) {
            logger.debug("sql strular");
            return new Book[]{};
        }
        Book[] returBookArray = new Book[arrayOfBooks.size()];
        arrayOfBooks.toArray(returBookArray);
        logger.debug("<------ hämtaTillgänglighet");
        return returBookArray;
    }


    //----------------------------------------------------------------------------------------

    @Override
    public int createLoan(int accountID, int bookID /*bid från samling*/) {

        //Getting today's date, converting it to Date object, converting to MySQL Date format
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate dateNow = java.time.LocalDate.now();
        Date dateToday = Date.from(dateNow.atStartOfDay(defaultZoneId).toInstant());
        String datePattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        String dateOfLoan = formatter.format(dateToday);

        //Create MySQL query and try to execute it in order to create a loan in database table "lån"
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "insert into lån values(" + bookID + "," + accountID + ",\"" + dateOfLoan+"\")";
            int rS = stmt.executeUpdate(getTitel);
        } catch (SQLException e) {
            return 1;
        }

        return 0;
    }


    //-----------------------------------------------------------------------------------------------

    @Override
    public int addToBlacklist(long personNr) {
        logger.debug("läggTillSartlistade ----->");
        try {
            Statement stmt = connection.createStatement();
            String addBlacklist = "insert into svartlista values (" + personNr + ")";
            long rS = stmt.executeUpdate(addBlacklist);
            if (rS == 0) {
                logger.debug("inga fält uppdaterade");
                return 1;
            } else {
                logger.debug("<----- läggTillSartlistade ");
                return 0;
            }
        } catch (SQLException e) {
            return 2;
        }
    }


    //----------------------------------------------------------------------------------------

    @Override //Hämtar inte kontoId. ska fixas
    public int regAccount(String fname, String ename, long personNr, int role) {
        logger.debug("Skapa Konto ----->");
        int failOrSuccess = 0;

        try {
            Statement stmt = connection.createStatement();
            Statement getStmt = connection.createStatement();
            String addAccount = "insert into konto values (\"" + fname + "\",\"" + ename + "\"," + personNr + ",\"" + role + "\"," + 0 +", " + null + "," + 0 + "," + 0 + ")";
            String getAddedKontoId = "select kontoID from konto where personNr=" + personNr;
            int rS = stmt.executeUpdate(addAccount);
            if (rS == 0) {
                return 1;}
            ResultSet newRs = getStmt.executeQuery(getAddedKontoId);
            while(newRs.next()) {
                failOrSuccess = newRs.getInt("kontoid");
            }
        } catch (SQLException e) {
            failOrSuccess = 1;
        }
        logger.debug("<----- Skapa Konto "+ failOrSuccess);
        return failOrSuccess;
    }


    //----------------------------------------------------------------------------------------

    @Override
    public int terminateAccount(int accountID) {
        logger.debug("avsluta konto ------->");
        int failOrSuccess;
        try {
            logger.debug("skapar statement");
            Statement stmt = connection.createStatement();

            String deleteAccount = "delete from konto where kontoID =" + accountID+";";
            logger.debug("kör sql kommando");
            int rS = stmt.executeUpdate(deleteAccount);
            if(rS == 0){
                logger.debug("konto gick inte att avsluta");
                return 1;
            }
        } catch (SQLException e) {
            return 2;
        }
        logger.debug("<------- avsluta konto " + 0);
        return 0;
    }


    //----------------------------------------------------------------------------------------

    @Override
    public Account[] getAccount() {
        logger.debug("hämtaKonton ------->");
        ArrayList<Account> arrayOfAccounts = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            Statement stmt2 = connection.createStatement();
            logger.debug("Databas ansluten");
            String getAccount = "select * from konto";
            ResultSet rS = stmt.executeQuery(getAccount);
            logger.debug("konton hämtade till rs");
            while (rS.next()) {

                //-------Hämtar lån från databsen baserat på kontoID---------
                String getLån = "SELECT lån.bid,lån.kontoid,lån.låndatum FROM lån,konto where konto.kontoid= lån.kontoid and konto.kontoid ="
                        + rS.getInt("kontoID") + ";";

                ResultSet lRS = stmt2.executeQuery(getLån);

                logger.debug("kontos lån hämtade till lRS");
                ArrayList<Loan> listOfLoan = new ArrayList<>();
                Loan[] loanArray;

                while (lRS.next()){
                    listOfLoan.add(new Loan(lRS.getInt("bid"),lRS.getInt("kontoID"),lRS.getDate("lånDatum")));
                }

                logger.debug("lån lagda i Lånarray-list");
                loanArray = new Loan[listOfLoan.size()];
                logger.debug("Lånarray-list till array");
                listOfLoan.toArray(loanArray);
                logger.debug("skapar kontobjekt"+ rS.getInt("kontoID"));
                arrayOfAccounts.add(new Account(
                        rS.getString("fnamn"),
                        rS.getString("enamn"),
                        rS.getLong("personNr"),
                        rS.getInt("roll"),
                        rS.getInt("kontoID"),
                        rS.getDate("avstängd"),
                        loanArray,
                        rS.getInt("antalAvstängningar"),
                        rS.getInt("antalFörseningar")));

                logger.debug("kontoobjekt lagt till arrayOfAccounts"+rS.getInt("kontoID"));
                logger.debug("antal böcker lånade: "+loanArray.length);

            }
        }
        catch (SQLException e) {
            logger.error("Problem i databasen");
            throw new RuntimeException(e);
        }
        Account[] returAccountArray = new Account[arrayOfAccounts.size()];
        arrayOfAccounts.toArray(returAccountArray);
        logger.debug("Kont[] returned");
        logger.debug("<----- hämtaKonto()" + "antal konton = "+returAccountArray.length);

        return returAccountArray;
    }


    //----------------------------------------------------------------------------------------

    @Override
    public int regTempBan(int accountID, int datsOfBan) {
        logger.debug("registreraTempAvstänging ------->");
        int failOrSuccess = 0;

        //Hämtar dagens daturm, lägger på numOfDays och gör om till ett MySQL-vänligt Date objekt
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate todaysDate = java.time.LocalDate.now();
        LocalDate endOfBan = todaysDate.plusDays(datsOfBan);
        Date inputDate = Date.from(endOfBan.atStartOfDay(defaultZoneId).toInstant());
        String datePattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        String sqlInputDate = formatter.format(inputDate);

        //Ökar antalet avstängningar med 1
        DatabaseApi createBan = new DatabaseApi();
        createBan.updateAmountOfBans(accountID);

        //Uppdaterar kontots kolumn "avstängd" med date-objektet sqlInputDate
        try {
            Statement stmt = connection.createStatement();
            String getAccount = "update konto set avstängd=\"" + sqlInputDate + "\" where kontoID=" + accountID;
            int rS = stmt.executeUpdate(getAccount);

        } catch (SQLException e) {
            //return 5 symboliserar problem med databasApi
            return 5;
        }

        logger.debug("<------- registreraTempAvstänging ");
        //0 representerar att allt gått som det ska
        return 0;
    }


    //----------------------------------------------------------------------------------------

    @Override
    public int removeLoan(int bookID){
        logger.debug("taBortLån ------->");
        try {
            Statement stmt = connection.createStatement();
            String getAccount = "delete from lån where bid="+bookID+";";
            int rS = stmt.executeUpdate(getAccount);
            logger.debug("<------- taBortLån " + "lånet fanns inte");
            if(rS<1){return 1;}

        } catch (SQLException e) {
            logger.error("sql strul!!!");
             return 1;
        }
        logger.debug("<------- taBortLån ");
        return 0;
    }


    //----------------------------------------------------------------------------------------

    @Override
    public int updateReturnDelays(int accountID) {
        logger.debug("updateAntalAvstängningar -----> ");

        int amountOfDelays = 0;
        DatabaseApi accessAccount = new DatabaseApi();
        for (Account account : accessAccount.getAccount()) {
            if (account.getAccountID() == accountID) {
                amountOfDelays = account.getAmountOfBookDelays();
                amountOfDelays++;
            }
        }

        try {
            Statement stmt = connection.createStatement();
            String getAccount = "update konto set antalFörseningar="+amountOfDelays + " where kontoID="+accountID;
            int rS = stmt.executeUpdate(getAccount);

        } catch (SQLException e) {
            return 1;
        }
        logger.debug("<------- updateAntalAvstängningar");
        return 0;
    }



    //----------------------------------------------------------------------------------------

    @Override
    public int updateAmountOfBans(int accountID) {
        logger.debug("updateAntalAvstängningar -----> ");
        int failOrSuccess = 0;

        int amountOfBan = 0;
        DatabaseApi accessAccount = new DatabaseApi();
        for (Account account : accessAccount.getAccount()) {
            if (account.getAccountID() == accountID) {
                amountOfBan = account.getAmountOfBans();
                amountOfBan++;
            }
        }

        try {
            Statement stmt = connection.createStatement();
            String getAccount = "update konto set antalAvstängningar="+amountOfBan + " where kontoID="+accountID;
            int rS = stmt.executeUpdate(getAccount);

        } catch (SQLException e) {
            failOrSuccess=1;
        }
        logger.debug("<------- updateAntalAvstängningar " + failOrSuccess);
        return failOrSuccess;
    }


    //----------------------------------------------------------------------------------------

    @Override
    // Klar tack vare Z
    public long[] getBlacklisted() {
        logger.debug("hämtaSvartlistade -----> ");
        //arrayOfBooks used with .toArray to create the return array
        ArrayList<Long> arrayOfBlacklist = new ArrayList<>();

        //Getting an array of book with titel which is then returned
        try {
            Statement stmt = connection.createStatement();
            String getPersonNr = "select personNr from svartlista";
            ResultSet rS = stmt.executeQuery(getPersonNr);
            while (rS.next()) {
                arrayOfBlacklist.add(rS.getLong("personNr"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        long[] returnBlacklistArray = new long[arrayOfBlacklist.size()];
        int i = 0;
        for (Long item : arrayOfBlacklist) {
            returnBlacklistArray[i] = item;
            i++;
        }
        logger.debug("<------- hämtaSvartlistade (listas längd = " + returnBlacklistArray.length);
        return returnBlacklistArray;
    }


    //----------------------------------------------------------------------------------------

    public Loan[] getLoans(){
        logger.debug("hämtaLån -----> ");
        ArrayList<Loan> loans = new ArrayList<>();

        //Getting an array of book with titel which is then returned
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "select * from lån";
            ResultSet rS = stmt.executeQuery(getTitel);
            while (rS.next()) {
                loans.add(new Loan(rS.getInt("bid"), rS.getInt("kontoid"), rS.getDate("lånDatum")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Loan[] arrayOfLoans = new Loan[loans.size()];
        loans.toArray(arrayOfLoans);
        logger.debug("<------- hämtaLån (listas längd = " + arrayOfLoans.length);
        return arrayOfLoans;
    }

}
