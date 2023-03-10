
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import processlagerAPI.TestProcess;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.time.ZoneId;

public class Databas implements databasAPI.IDatabas {
    private Connection connection;
    private static Logger logger = LogManager.getLogger(TestProcess.class.getName());
    public Databas() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://192.168.50.101:3306/1ik173-server", "Dorian", "Dorian1234");
            logger.debug("Databas ansluten ----->");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    //Hämtar en array av böcker som finns i Bok-tabellen och inte inte lån-tabellen
    public Bok[] hämtaTillgänglighet(String titel) {
        logger.debug("hämtaTillgänglighet ----->");
        //arrayOfBooks used with .toArray to create the return array
        ArrayList<Bok> arrayOfBooks = new ArrayList<>();

        //Getting an array of book with titel which is then returned
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "SELECT distinct bok.titel, bok.författare, bok.utgivningsår, samling.bid,samling.isbn FROM samling,lån,bok where bok.isbn = samling.isbn and\n" +
                    "samling.bid not in (select bid from lån) order by bid;";
            logger.debug("hämtar tillgängliga böcker ");
            ResultSet rS = stmt.executeQuery(getTitel);
            while (rS.next()) {
                logger.debug("lägger till: " + rS.getString("titel"),rS.getInt("bid"));
                arrayOfBooks.add(new Bok(rS.getInt("bid"),rS.getInt("isbn"), rS.getString("titel"), rS.getString("författare"), rS.getInt("utgivningsår")));
            }
        } catch (SQLException e) {
            logger.debug("sql strular");
            return new Bok[]{};
        }
        Bok[] returBookArray = new Bok[arrayOfBooks.size()];
        arrayOfBooks.toArray(returBookArray);
        logger.debug("<------ hämtaTillgänglighet");
        return returBookArray;
    }

    //Hämtar en array av böcker som finns i Bok-tabellen och inte inte lån-tabellen
    public Bok[] hämtaTillgänglighet() {
        logger.debug("hämtaTillgänglighet ----->");
        //arrayOfBooks used with .toArray to create the return array
        ArrayList<Bok> arrayOfBooks = new ArrayList<>();

        //Getting an array of book with titel which is then returned
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "SELECT distinct bok.titel, bok.författare, bok.utgivningsår, samling.bid,samling.isbn FROM samling,lån,bok where bok.isbn = samling.isbn and\n" +
                    "samling.bid not in (select bid from lån) order by bid;";
            logger.debug("hämtar tillgängliga böcker ");
            ResultSet rS = stmt.executeQuery(getTitel);
            while (rS.next()) {
                logger.debug("lägger till: " + rS.getString("titel"),rS.getInt("bid"));
                arrayOfBooks.add(new Bok(rS.getInt("bid"),rS.getInt("isbn"), rS.getString("titel"), rS.getString("författare"), rS.getInt("utgivningsår")));
            }
        } catch (SQLException e) {
            logger.debug("sql strular");
            return new Bok[]{};
        }
        Bok[] returBookArray = new Bok[arrayOfBooks.size()];
        arrayOfBooks.toArray(returBookArray);
        logger.debug("<------ hämtaTillgänglighet");
        return returBookArray;
    }
    @Override
    public int skapaLån(int kontoID, int bid /*bid från samling*/) {

        int returnValue = 0; //1 = fail, everything else is a KontoID which mean success

        //Getting todays date, converting it to Date object, converting to MySQL Date format
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate dateNow = java.time.LocalDate.now();
        Date dateToday = Date.from(dateNow.atStartOfDay(defaultZoneId).toInstant());
        String datePattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        String dateOfLoan = formatter.format(dateToday);

        //Create MySQL query and try to execute it in order to create a loan in database table "lån"
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "insert into lån values(" + bid + "," + kontoID + ",\"" + dateOfLoan+"\")";
            int rS = stmt.executeUpdate(getTitel);

        } catch (SQLException e) {
            returnValue = 1;
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return returnValue;
    }
    @Override
    public int läggTillSvartlistade(long personNr) {
        logger.debug("läggTillSartlistade ----->");
        int failOrSuccess;
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



    @Override //Hämtar inte kontoId. ska fixas
    public int skapaKonto(String fnamn, String enamn, long personNr, int roll) {
        logger.debug("Skapa Konto ----->");
        int failOrSuccess = 0;

        try {
            Statement stmt = connection.createStatement();
            Statement getStmt = connection.createStatement();
            String addAccount = "insert into konto values (\"" + fnamn + "\",\"" + enamn + "\"," + personNr + ",\"" + roll + "\"," + 0 +", " + null + "," + 0 + "," + 0 + ")";
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

    @Override
    public int avslutaKonto(int kontoID) {
        logger.debug("avsluta konto ------->");
        int failOrSuccess;
        try {
            logger.debug("skapar statement");
            Statement stmt = connection.createStatement();

            String deleteAccount = "delete from konto where kontoID =" + kontoID+";";
            logger.debug("kör sql kommando");
            int rS = stmt.executeUpdate(deleteAccount);
            if(rS == 0){
                logger.debug("konto gick inte att avsluta");
                failOrSuccess = 1;
                return failOrSuccess;}
        } catch (SQLException e) {
            failOrSuccess = 2;
            return failOrSuccess;
        }
        logger.debug("<------- avsluta konto " + 0);
        failOrSuccess = 0;
        return failOrSuccess;
    }


    @Override
    public Konto[] hämtaKonton() {
        logger.debug("hämtaKonton ------->");
        ArrayList<Konto> arrayOfAccounts = new ArrayList<>();
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
                ArrayList<Lån> arrayOfLån = new ArrayList<>();
                Lån[] lånArray;

                while (lRS.next()){
                    arrayOfLån.add(new Lån(lRS.getInt("bid"),lRS.getInt("kontoID"),lRS.getDate("lånDatum")));
                }

                logger.debug("lån lagda i Lånarray-list");
                lånArray = new Lån[arrayOfLån.size()];
                logger.debug("Lånarray-list till array");
                arrayOfLån.toArray(lånArray);
                logger.debug("skapar kontobjekt"+ rS.getInt("kontoID"));
                arrayOfAccounts.add(new Konto(
                        rS.getString("fnamn"),
                        rS.getString("enamn"),
                        rS.getLong("personNr"),
                        rS.getInt("roll"),
                        rS.getInt("kontoID"),
                        rS.getDate("avstängd"),
                        lånArray,
                        rS.getInt("antalAvstängningar"),
                        rS.getInt("antalFörseningar")));

                logger.debug("kontoobjekt lagt till arrayOfAccounts"+rS.getInt("kontoID"));
                logger.debug("antal böcker lånade: "+lånArray.length);

            }
        }
        catch (SQLException e) {
            logger.error("det sket sig me sql...");
            throw new RuntimeException(e);
        }
        Konto[] returKontoArray = new Konto[arrayOfAccounts.size()];
        arrayOfAccounts.toArray(returKontoArray);
        logger.debug("Kont[] returned");
        logger.debug("<----- hämtaKonto()" + "antal konton = "+returKontoArray.length);

        return returKontoArray;
    }

    @Override
    public int registreraTempAvstänging(int kontoID, int numOfDays) {
        logger.debug("registreraTempAvstänging ------->");
        int failOrSuccess = 0;

        //Hämtar dagens daturm, lägger på numOfDays och gör om till ett MySQL-vänligt Date objekt
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate todaysDate = java.time.LocalDate.now();
        LocalDate endOfBan = todaysDate.plusDays(numOfDays);
        Date inputDate = Date.from(endOfBan.atStartOfDay(defaultZoneId).toInstant());
        String datePattern = "yyyy-MM-dd";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        String sqlInputDate = formatter.format(inputDate);

        //Ökar antalet avstängningar med 1
        Databas x = new Databas();
        x.updateAntalAvstängningar(kontoID);

        //Uppdaterar kontots kolumn "avstängd" med date-objektet sqlInputDate
        try {
            Statement stmt = connection.createStatement();
            String getAccount = "update konto set avstängd=\"" + sqlInputDate + "\" where kontoID=" + kontoID;
            int rS = stmt.executeUpdate(getAccount);

        } catch (SQLException e) {
            failOrSuccess = 5;
        }

        logger.debug("<------- registreraTempAvstänging ");
        return failOrSuccess;
    }

    @Override
    public int taBortLån(int bid){
        logger.debug("taBortLån ------->");
        int failOrSuccess;
        try {
            Statement stmt = connection.createStatement();
            String getAccount = "delete from lån where bid="+bid+";";
            int rS = stmt.executeUpdate(getAccount);
            logger.debug("<------- taBortLån " + "lånet fanns inte");
            if(rS<1){return 1;}
            failOrSuccess = 0;
        } catch (SQLException e) {
            logger.error("sql strul!!!");
            failOrSuccess = 1;
        }
        logger.debug("<------- taBortLån ");
        return failOrSuccess;
    }


    @Override
    public int updateAntalAvstängningar(int kontoID) {
        logger.debug("updateAntalAvstängningar -----> ");
        int failOrSuccess = 0;

        int amountOfBan = 0;
        Databas accessKonto = new Databas();
        for (Konto kon : accessKonto.hämtaKonton()) {
            if (kon.getKontoID() == kontoID) {
                amountOfBan = kon.getAntalAvstangningar();
                amountOfBan++;
            }
        }

        try {
            Statement stmt = connection.createStatement();
            String getAccount = "update konto set antalAvstängningar="+amountOfBan + " where kontoID="+kontoID;
            int rS = stmt.executeUpdate(getAccount);

        } catch (SQLException e) {
            failOrSuccess=1;
        }
        logger.debug("<------- updateAntalAvstängningar " + failOrSuccess);
        return failOrSuccess;
    }

    @Override
    public int updateAntalFörseningar(int kontoID) {
        logger.debug("updateAntalFörseningar -----> ");
        int failOrSuccess = 0;
        int amountOfLateReturns = 0;
        Databas accessKonto = new Databas();
        for (Konto kon : accessKonto.hämtaKonton()) {
            if (kon.getKontoID() == kontoID) {
                amountOfLateReturns = kon.getAntalForseningar();
                amountOfLateReturns++;
            }
        }

        try {
            Statement stmt = connection.createStatement();
            String getAccount = "update konto set antalFörseningar="+amountOfLateReturns+" where kontoID="+kontoID;
            int rS = stmt.executeUpdate(getAccount);

        } catch (SQLException e) {
            failOrSuccess = 1;
        }
        logger.debug("<------- updateAntalFörseningar " + failOrSuccess);
        return failOrSuccess;
    }


    @Override
    // Klar tack vare Z
    public long[] hämtaSvarlistade() {
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

    public Lån[] hämtaLån(){
        logger.debug("hämtaLån -----> ");
        ArrayList<Lån> loans = new ArrayList<>();

        //Getting an array of book with titel which is then returned
        try {
            Statement stmt = connection.createStatement();
            String getTitel = "select * from lån";
            ResultSet rS = stmt.executeQuery(getTitel);
            while (rS.next()) {
                loans.add(new Lån(rS.getInt("bid"), rS.getInt("kontoid"), rS.getDate("lånDatum")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Lån[] arrayOfLoans = new Lån[loans.size()];
        loans.toArray(arrayOfLoans);
        logger.debug("<------- hämtaLån (listas längd = " + arrayOfLoans.length);
        return arrayOfLoans;
    }

}
