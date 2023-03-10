public interface IDatabas {

    /* Hämta alla böcker med riktig titel som inte är utlånade (alltså i samling men inte i lån).*/
    Bok[] hämtaTillgänglighet (String titel);

    /*skapa en Lån och returnera en String
     *
     * behöver definiera vad databasen kan tänkas svara... för tester
     *
     */
    int skapaLån (int kontoID, int bibID);


    /*ta bort lån enligt medlems personnummer eller Bokens ISBN
     *
     * behöver definiera vad databasen kan tänkas svara... för tester
     *
     */


    /*lägga till en medlem på svartlista*/
    /*svartlista medlem genom medlems personnummer
     *
     * behöver definiera vad databasen kan tänkas svara... för tester
     *
     * */
    int läggTillSvartlistade(long personNr);


    /*returnera String att Konto är skapad*/
    /*skapa Konto med medlemens namn, efternamn, personnummer och roll
     *
     * behöver definiera vad databasen kan tänkas svara... för tester
     *
     * */
    int skapaKonto(String fnamn, String  enamn, long personNr, int roll);


    /* returnera en String efter att Konto är avslutad.
     *
     * behöver definiera vad databasen kan tänkas svara... för tester
     *
     */
    int avslutaKonto(int kontoID);

    /*  returnera en Konto[] objekt med alla konton på servern.
     *
     * */
    public databasAPI.Konto[] hämtaKonton();

    public int registreraTempAvstänging(int kontoID, int numOfDays);

    public int taBortLån(int bibID);
    public int updateAntalFörseningar(int kontoID);
    public int updateAntalAvstängningar(int kontoID);
    public long[] hämtaSvarlistade();
    public databasAPI.Lån[] hämtaLån();

}