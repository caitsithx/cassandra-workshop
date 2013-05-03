package com.cassandraguide.hotel;

import java.nio.charset.Charset;
import java.util.HashMap;

import lixl.workshop.cassandra.client.jpalike.EntityDao;
import lixl.workshop.cassandra.client.simple.EncodedVO;
import lixl.workshop.cassandra.client.simple.SimpleDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class Prepopulate {

    private static final Log LOG = LogFactory.getLog(Prepopulate.class);
//    private Cassandra.Client client;
//    private TTransport m_tTransport = null;
//    private Connector connector;
    
    private SimpleDao sDao;
    private EntityDao eDao;

    //constructor opens a connection so we don't have to 
    //constantly recreate it
    public Prepopulate() throws Exception {
//        connector = new Connector();
//        client = connector.connect();
    }

    void prepopulate() throws Exception {
        //pre-populate the DB with Hotels
        insertAllHotels();

        //also add all hotels to index to help searches
        insertByCityIndexes();

        //pre-populate the DB with POIs
        insertAllPointsOfInterest();

//        connector.close();
    }

    //also add hotels to lookup by city index
    public void insertByCityIndexes() throws Exception {

        String scottsdaleKey = "Scottsdale:AZ";
        String sfKey = "San Francisco:CA";
        String newYorkKey = "New York:NY";

        insertByCityIndex(scottsdaleKey, Constants.CAMBRIA_NAME);
        insertByCityIndex(scottsdaleKey, Constants.CLARION_NAME);
        insertByCityIndex(sfKey, Constants.W_NAME);
        insertByCityIndex(newYorkKey, Constants.WALDORF_NAME);
    }
    
    //use Valueless Column_A pattern
    private void insertByCityIndex(String rowKey, String hotelName)
            throws Exception {
//        VOFactoryBase voFactory = VOFactoryBase.getVOFactory("HotelByCity");

        HashMap<String, Object> columnVals = new HashMap<>();
        columnVals.put(hotelName, null);
        
//        EncodedVO hotelByCityVO = voFactory.newVO(rowKey, columnVals);
        EncodedVO hotelByCityVO = new EncodedVO();
        hotelByCityVO.setKey(Charset.forName("UTF-8").encode(rowKey).array());
        hotelByCityVO.addColumn(Charset.forName("UTF-8").encode(hotelName).array(), new byte[]{0x00});
        
        sDao.insert(hotelByCityVO);
        
        
//        long timestamp1 = System.currentTimeMillis();
//        Column_A nameCol = new Column_A(Charset.forName(Constants.UTF8).encode(hotelName));
//        nameCol.setValue(new byte[0]);
//        nameCol.setTimestamp(timestamp1);
//
//        ColumnOrSuperColumn nameCosc = new ColumnOrSuperColumn();
//        nameCosc.column = nameCol;
//
//        Mutation nameMut = new Mutation();
//        nameMut.column_or_supercolumn = nameCosc;
//
//        //set up the batch
//        Map<String, Map<String, List<Mutation>>> mutationMap =
//                new HashMap<String, Map<String, List<Mutation>>>();
//
//        Map<String, List<Mutation>> muts =
//                new HashMap<String, List<Mutation>>();
//        List<Mutation> cols = new ArrayList<Mutation>();
//        cols.add(nameMut);
//
//        String columnFamily = "HotelByCity";
//        muts.put(columnFamily, cols);
//
//        //outer map key is a row key
//        //inner map key is the column family name
//        mutationMap.put(rowKey, muts);
//
//
//        //create representation of the column
//        ColumnPath cp = new ColumnPath(columnFamily);
//        cp.setColumn(hotelName.getBytes(Constants.UTF8));
//
//        ColumnParent parent = new ColumnParent(columnFamily);
//        //here, the column name IS the value (there's no value)
//        Column_A col = new Column_A(hotelName.getBytes(Constants.UTF8), new byte[0], clock);
//
//        client.insert(rowKey.getBytes(), parent, col, CL);

        LOG.debug("Inserted HotelByCity index for " + hotelName);

    } //end inserting ByCity index

    //POI
    public void insertAllPointsOfInterest() throws Exception {

        LOG.debug("Inserting POIs.");

        insertPOIEmpireState();
        insertPOICentralPark();
        insertPOIPhoenixZoo();
        insertPOISpringTraining();

        LOG.debug("Done inserting POIs.");
    }

    private void insertPOISpringTraining() throws Exception {
    	PointOfInterest l_poi = new PointOfInterest();
        l_poi.setPoiName("Spring Training");
        
        PointOfInterest.Hotel l_hotel = l_poi.new Hotel();
        l_hotel.setDescription("Fun for baseball fans.");
        l_hotel.setPhone("623-333-3333");
        l_hotel.setHotelName(Constants.CAMBRIA_NAME);
        
        l_poi.setHotel(l_hotel);
        
        eDao.insert(l_poi);
    	
    	
//        Map<byte[], Map<String, List<Mutation>>> outerMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//        List<Mutation> columnsToAdd = new ArrayList<Mutation>();
//        Clock clock = new Clock(System.nanoTime());
//        String keyName = "Spring Training";
//        Column descCol = new Column("desc".getBytes(Constants.UTF8),
//                "Fun for baseball fans.".getBytes("UTF-8"), clock);
//        Column phoneCol = new Column("phone".getBytes(Constants.UTF8),
//                "623-333-3333".getBytes(Constants.UTF8), clock);
//
//        List<Column> cols = new ArrayList<Column>();
//        cols.add(descCol);
//        cols.add(phoneCol);
//
//        Map<String, List<Mutation>> innerMap =
//                new HashMap<String, List<Mutation>>();
//
//        Mutation columns = new Mutation();
//        ColumnOrSuperColumn descCosc = new ColumnOrSuperColumn();
//        SuperColumn sc = new SuperColumn();
//        sc.name = Constants.CAMBRIA_NAME.getBytes();
//        sc.columns = cols;
//
//        descCosc.super_column = sc;
//        columns.setColumn_or_supercolumn(descCosc);
//
//        columnsToAdd.add(columns);
//
//        String superCFName = "PointOfInterest";
//        ColumnPath cp = new ColumnPath();
//        cp.column_family = superCFName;
//        cp.setSuper_column(Constants.CAMBRIA_NAME.getBytes());
//        cp.setSuper_columnIsSet(true);
//
//        innerMap.put(superCFName, columnsToAdd);
//        outerMap.put(keyName.getBytes(), innerMap);
//
//        client.batch_mutate(outerMap, CL);

        LOG.debug("Done inserting Spring Training.");
    }

    private void insertPOIPhoenixZoo() throws Exception {
    	PointOfInterest l_poi = new PointOfInterest();
        l_poi.setPoiName("Phoenix Zoo");
        
        PointOfInterest.Hotel l_hotel = l_poi.new Hotel();
        l_hotel.setDescription("They have animals here.");
        l_hotel.setPhone("480-555-9999");
        l_hotel.setHotelName(Constants.CAMBRIA_NAME);
        
        l_poi.setHotel(l_hotel);
        
        eDao.insert(l_poi);
    	
    	
//        Map<byte[], Map<String, List<Mutation>>> outerMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//        List<Mutation> columnsToAdd = new ArrayList<Mutation>();
//
//        long ts = System.currentTimeMillis();
//        String keyName = "Phoenix Zoo";
//        Column descCol = new Column("desc".getBytes(UTF8),
//                "They have animals here.".getBytes("UTF-8"), new Clock(ts));
//
//        Column phoneCol = new Column("phone".getBytes(UTF8),
//                "480-555-9999".getBytes(UTF8), new Clock(ts));
//
//        List<Column> cols = new ArrayList<Column>();
//        cols.add(descCol);
//        cols.add(phoneCol);
//
//        Map<String, List<Mutation>> innerMap =
//                new HashMap<String, List<Mutation>>();
//
//        String cambriaName = "Cambria Suites Hayden";
//
//        Mutation columns = new Mutation();
//        ColumnOrSuperColumn descCosc = new ColumnOrSuperColumn();
//        SuperColumn sc = new SuperColumn();
//        sc.name = cambriaName.getBytes();
//        sc.columns = cols;
//
//        descCosc.super_column = sc;
//        columns.setColumn_or_supercolumn(descCosc);
//
//        columnsToAdd.add(columns);
//
//        String superCFName = "PointOfInterest";
//        ColumnPath cp = new ColumnPath();
//        cp.column_family = superCFName;
//        cp.setSuper_column(cambriaName.getBytes());
//        cp.setSuper_columnIsSet(true);
//
//        innerMap.put(superCFName, columnsToAdd);
//        outerMap.put(keyName.getBytes(), innerMap);
//
//        client.batch_mutate(outerMap, CL);

        LOG.debug("Done inserting Phoenix Zoo.");
    }

    private void insertPOICentralPark() throws Exception {
    	PointOfInterest l_poi = new PointOfInterest();
        l_poi.setPoiName("Central Park");
        
        PointOfInterest.Hotel l_hotel = l_poi.new Hotel();
        l_hotel.setDescription("Walk around in the park. It's pretty.");
//        l_hotel.setPhone("480-555-9999");
        l_hotel.setHotelName(Constants.WALDORF_NAME);
        
        l_poi.setHotel(l_hotel);
        
        eDao.insert(l_poi);
    	
    	
//        Map<byte[], Map<String, List<Mutation>>> outerMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//        List<Mutation> columnsToAdd = new ArrayList<Mutation>();
//
//        Clock clock = new Clock(System.nanoTime());
//        String keyName = "Central Park";
//        Column descCol = new Column("desc".getBytes(UTF8),
//                "Walk around in the park. It's pretty.".getBytes("UTF-8"), clock);
//
//        //no phone column for park
//
//        List<Column> cols = new ArrayList<Column>();
//        cols.add(descCol);
//
//        Map<String, List<Mutation>> innerMap =
//                new HashMap<String, List<Mutation>>();
//
//        Mutation columns = new Mutation();
//        ColumnOrSuperColumn descCosc = new ColumnOrSuperColumn();
//        SuperColumn waldorfSC = new SuperColumn();
//        waldorfSC.name = WALDORF_NAME.getBytes();
//        waldorfSC.columns = cols;
//
//        descCosc.super_column = waldorfSC;
//        columns.setColumn_or_supercolumn(descCosc);
//
//        columnsToAdd.add(columns);
//
//        String superCFName = "PointOfInterest";
//        ColumnPath cp = new ColumnPath();
//        cp.column_family = superCFName;
//        cp.setSuper_column(WALDORF_NAME.getBytes());
//        cp.setSuper_columnIsSet(true);
//
//        innerMap.put(superCFName, columnsToAdd);
//        outerMap.put(keyName.getBytes(), innerMap);
//
//        client.batch_mutate(outerMap, CL);

        LOG.debug("Done inserting Central Park.");
    }

    private void insertPOIEmpireState() throws Exception {
    	PointOfInterest l_poi = new PointOfInterest();
        l_poi.setPoiName("Empire State Building");
        
        PointOfInterest.Hotel l_hotel = l_poi.new Hotel();
        l_hotel.setDescription("Great view from 102nd floor.");
        l_hotel.setPhone("212-777-7777");
        l_hotel.setHotelName(Constants.WALDORF_NAME);
        
        l_poi.setHotel(l_hotel);
        
        eDao.insert(l_poi);
        

//        Map<byte[], Map<String, List<Mutation>>> outerMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//
//        List<Mutation> columnsToAdd = new ArrayList<Mutation>();
//
//        Clock clock = new Clock(System.nanoTime());
//        String esbName = "Empire State Building";
//        Column descCol = new Column("desc".getBytes(UTF8),
//                "Great view from 102nd floor.".getBytes("UTF-8"), clock);
//        Column phoneCol = new Column("phone".getBytes(UTF8),
//                "212-777-7777".getBytes(UTF8), clock);
//
//        List<Column> esbCols = new ArrayList<Column>();
//        esbCols.add(descCol);
//        esbCols.add(phoneCol);
//
//        Map<String, List<Mutation>> innerMap = new HashMap<String, List<Mutation>>();
//
//        Mutation columns = new Mutation();
//        ColumnOrSuperColumn descCosc = new ColumnOrSuperColumn();
//        SuperColumn waldorfSC = new SuperColumn();
//        waldorfSC.name = WALDORF_NAME.getBytes();
//        waldorfSC.columns = esbCols;
//
//        descCosc.super_column = waldorfSC;
//        columns.setColumn_or_supercolumn(descCosc);
//
//        columnsToAdd.add(columns);
//
//        String superCFName = "PointOfInterest";
//        ColumnPath cp = new ColumnPath();
//        cp.column_family = superCFName;
//        cp.setSuper_column(WALDORF_NAME.getBytes());
//        cp.setSuper_columnIsSet(true);
//
//        innerMap.put(superCFName, columnsToAdd);
//        outerMap.put(esbName.getBytes(), innerMap);
//
//        client.batch_mutate(outerMap, CL);

        LOG.debug("Done inserting Empire State.");
    }

    //convenience method runs all of the individual inserts
    public void insertAllHotels() throws Exception {

        String columnFamily = "Hotel";

        //row keys
        String cambriaKey = "AZC_043";
        String clarionKey = "AZS_011";
        String wKey = "CAS_021";
        String waldorfKey = "NYN_042";

        Hotel cambria = new Hotel();
        cambria.setId(cambriaKey);
        cambria.setZip(cambriaKey);
        cambria.setName(Constants.CAMBRIA_NAME);
        cambria.setPhone("480-444-4444");
        cambria.setAddress("400 N. Hayden");
        cambria.setCity("Scottsdale");
        cambria.setState("AZ");
        cambria.setZip("85255");
        
        Hotel waldor = new Hotel();
        waldor.setId(waldorfKey);
        waldor.setName(Constants.WALDORF_NAME);
        waldor.setPhone("212-555-5555");
        waldor.setAddress("301 Park Ave");
        waldor.setCity("New York");
        waldor.setState("NY");
        waldor.setZip("10019");
        
        
        Hotel clarion = new Hotel();
        clarion.setId(clarionKey);
        clarion.setName(Constants.CLARION_NAME);
        clarion.setPhone("480-333-3333");
        clarion.setAddress("3000 N. Scottsdale Rd");
        clarion.setCity("Scottsdale");
        clarion.setState("AZ");
        clarion.setZip("85255");
        
        
        Hotel wsf = new Hotel();
        wsf.setId(clarionKey);
        wsf.setName(Constants.W_NAME);
        wsf.setPhone("415-222-2222");
        wsf.setAddress("181 3rd Street");
        wsf.setCity("San Francisco");
        wsf.setState("CA");
        wsf.setZip("94103");
        
        
        Hotel[] l_hotels = new Hotel[]{cambria, waldor, clarion, wsf};
        eDao.insert(l_hotels);
        
        //conveniences
//        Map<byte[], Map<String, List<Mutation>>> cambriaMutationMap =
//                createCambriaMutation(columnFamily, cambriaKey);
//        Map<byte[], Map<String, List<Mutation>>> clarionMutationMap =
//                createClarionMutation(columnFamily, clarionKey);
//        Map<byte[], Map<String, List<Mutation>>> waldorfMutationMap =
//                createWaldorfMutation(columnFamily, waldorfKey);
//        Map<byte[], Map<String, List<Mutation>>> wMutationMap =
//                createWMutation(columnFamily, wKey);
//        client.batch_mutate(cambriaMutationMap, CL);
//        LOG.debug("Inserted " + cambriaKey);
//        client.batch_mutate(clarionMutationMap, CL);
//        LOG.debug("Inserted " + clarionKey);
//        client.batch_mutate(wMutationMap, CL);
//        LOG.debug("Inserted " + wKey);
//        client.batch_mutate(waldorfMutationMap, CL);
//        LOG.debug("Inserted " + waldorfKey);

        LOG.debug("Done inserting at " + System.nanoTime());
    }

    //set up columns to insert for W
//    private Map<byte[], Map<String, List<Mutation>>> createWMutation(
//            String columnFamily, String rowKey)
//            throws UnsupportedEncodingException {
//
//        Clock clock = new Clock(System.nanoTime());
//
//        Column nameCol = new Column("name".getBytes(UTF8),
//                W_NAME.getBytes("UTF-8"), clock);
//        Column phoneCol = new Column("phone".getBytes(UTF8),
//                "415-222-2222".getBytes(UTF8), clock);
//        Column addressCol = new Column("address".getBytes(UTF8),
//                "181 3rd Street".getBytes(UTF8), clock);
//        Column cityCol = new Column("city".getBytes(UTF8),
//                "San Francisco".getBytes(UTF8), clock);
//        Column stateCol = new Column("state".getBytes(UTF8),
//                "CA".getBytes("UTF-8"), clock);
//        Column zipCol = new Column("zip".getBytes(UTF8),
//                "94103".getBytes(UTF8), clock);
//
//        ColumnOrSuperColumn nameCosc = new ColumnOrSuperColumn();
//        nameCosc.column = nameCol;
//
//        ColumnOrSuperColumn phoneCosc = new ColumnOrSuperColumn();
//        phoneCosc.column = phoneCol;
//
//        ColumnOrSuperColumn addressCosc = new ColumnOrSuperColumn();
//        addressCosc.column = addressCol;
//
//        ColumnOrSuperColumn cityCosc = new ColumnOrSuperColumn();
//        cityCosc.column = cityCol;
//
//        ColumnOrSuperColumn stateCosc = new ColumnOrSuperColumn();
//        stateCosc.column = stateCol;
//
//        ColumnOrSuperColumn zipCosc = new ColumnOrSuperColumn();
//        zipCosc.column = zipCol;
//
//        Mutation nameMut = new Mutation();
//        nameMut.column_or_supercolumn = nameCosc;
//        Mutation phoneMut = new Mutation();
//        phoneMut.column_or_supercolumn = phoneCosc;
//        Mutation addressMut = new Mutation();
//        addressMut.column_or_supercolumn = addressCosc;
//        Mutation cityMut = new Mutation();
//        cityMut.column_or_supercolumn = cityCosc;
//        Mutation stateMut = new Mutation();
//        stateMut.column_or_supercolumn = stateCosc;
//        Mutation zipMut = new Mutation();
//        zipMut.column_or_supercolumn = zipCosc;
//
//        //set up the batch
//        Map<byte[], Map<String, List<Mutation>>> mutationMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//
//        Map<String, List<Mutation>> muts =
//                new HashMap<String, List<Mutation>>();
//        List<Mutation> cols = new ArrayList<Mutation>();
//        cols.add(nameMut);
//        cols.add(phoneMut);
//        cols.add(addressMut);
//        cols.add(cityMut);
//        cols.add(stateMut);
//        cols.add(zipMut);
//
//        muts.put(columnFamily, cols);
//
//        //outer map key is a row key
//        //inner map key is the column family name
//        mutationMap.put(rowKey.getBytes(), muts);
//        return mutationMap;
//    }

    //add Waldorf hotel to Hotel CF
//    private Map<byte[], Map<String, List<Mutation>>> createWaldorfMutation(
//            String columnFamily, String rowKey)
//            throws UnsupportedEncodingException {
//
//        Clock clock = new Clock(System.nanoTime());
//
//        Column nameCol = new Column("name".getBytes(UTF8),
//                WALDORF_NAME.getBytes("UTF-8"), clock);
//        Column phoneCol = new Column("phone".getBytes(UTF8),
//                "212-555-5555".getBytes(UTF8), clock);
//        Column addressCol = new Column("address".getBytes(UTF8),
//                "301 Park Ave".getBytes(UTF8), clock);
//        Column cityCol = new Column("city".getBytes(UTF8),
//                "New York".getBytes(UTF8), clock);
//        Column stateCol = new Column("state".getBytes(UTF8),
//                "NY".getBytes("UTF-8"), clock);
//        Column zipCol = new Column("zip".getBytes(UTF8),
//                "10019".getBytes(UTF8), clock);
//
//        ColumnOrSuperColumn nameCosc = new ColumnOrSuperColumn();
//        nameCosc.column = nameCol;
//
//        ColumnOrSuperColumn phoneCosc = new ColumnOrSuperColumn();
//        phoneCosc.column = phoneCol;
//
//        ColumnOrSuperColumn addressCosc = new ColumnOrSuperColumn();
//        addressCosc.column = addressCol;
//
//        ColumnOrSuperColumn cityCosc = new ColumnOrSuperColumn();
//        cityCosc.column = cityCol;
//
//        ColumnOrSuperColumn stateCosc = new ColumnOrSuperColumn();
//        stateCosc.column = stateCol;
//
//        ColumnOrSuperColumn zipCosc = new ColumnOrSuperColumn();
//        zipCosc.column = zipCol;
//
//        Mutation nameMut = new Mutation();
//        nameMut.column_or_supercolumn = nameCosc;
//        Mutation phoneMut = new Mutation();
//        phoneMut.column_or_supercolumn = phoneCosc;
//        Mutation addressMut = new Mutation();
//        addressMut.column_or_supercolumn = addressCosc;
//        Mutation cityMut = new Mutation();
//        cityMut.column_or_supercolumn = cityCosc;
//        Mutation stateMut = new Mutation();
//        stateMut.column_or_supercolumn = stateCosc;
//        Mutation zipMut = new Mutation();
//        zipMut.column_or_supercolumn = zipCosc;
//
//        //set up the batch
//        Map<byte[], Map<String, List<Mutation>>> mutationMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//
//        Map<String, List<Mutation>> muts =
//                new HashMap<String, List<Mutation>>();
//        List<Mutation> cols = new ArrayList<Mutation>();
//        cols.add(nameMut);
//        cols.add(phoneMut);
//        cols.add(addressMut);
//        cols.add(cityMut);
//        cols.add(stateMut);
//        cols.add(zipMut);
//
//        muts.put(columnFamily, cols);
//
//        //outer map key is a row key
//        //inner map key is the column family name
//        mutationMap.put(rowKey.getBytes(), muts);
//        return mutationMap;
//    }

    //set up columns to insert for Clarion
//    private Map<byte[], Map<String, List<Mutation>>> createClarionMutation(
//            String columnFamily, String rowKey)
//            throws UnsupportedEncodingException {
//
//        Clock clock = new Clock(System.nanoTime());
//
//        Column nameCol = new Column("name".getBytes(UTF8),
//                CLARION_NAME.getBytes("UTF-8"), clock);
//        Column phoneCol = new Column("phone".getBytes(UTF8),
//                "480-333-3333".getBytes(UTF8), clock);
//        Column addressCol = new Column("address".getBytes(UTF8),
//                "3000 N. Scottsdale Rd".getBytes(UTF8), clock);
//        Column cityCol = new Column("city".getBytes(UTF8),
//                "Scottsdale".getBytes(UTF8), clock);
//        Column stateCol = new Column("state".getBytes(UTF8),
//                "AZ".getBytes("UTF-8"), clock);
//        Column zipCol = new Column("zip".getBytes(UTF8),
//                "85255".getBytes(UTF8), clock);
//
//        ColumnOrSuperColumn nameCosc = new ColumnOrSuperColumn();
//        nameCosc.column = nameCol;
//
//        ColumnOrSuperColumn phoneCosc = new ColumnOrSuperColumn();
//        phoneCosc.column = phoneCol;
//
//        ColumnOrSuperColumn addressCosc = new ColumnOrSuperColumn();
//        addressCosc.column = addressCol;
//
//        ColumnOrSuperColumn cityCosc = new ColumnOrSuperColumn();
//        cityCosc.column = cityCol;
//
//        ColumnOrSuperColumn stateCosc = new ColumnOrSuperColumn();
//        stateCosc.column = stateCol;
//
//        ColumnOrSuperColumn zipCosc = new ColumnOrSuperColumn();
//        zipCosc.column = zipCol;
//
//        Mutation nameMut = new Mutation();
//        nameMut.column_or_supercolumn = nameCosc;
//        Mutation phoneMut = new Mutation();
//        phoneMut.column_or_supercolumn = phoneCosc;
//        Mutation addressMut = new Mutation();
//        addressMut.column_or_supercolumn = addressCosc;
//        Mutation cityMut = new Mutation();
//        cityMut.column_or_supercolumn = cityCosc;
//        Mutation stateMut = new Mutation();
//        stateMut.column_or_supercolumn = stateCosc;
//        Mutation zipMut = new Mutation();
//        zipMut.column_or_supercolumn = zipCosc;
//
//        //set up the batch
//        Map<byte[], Map<String, List<Mutation>>> mutationMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//
//        Map<String, List<Mutation>> muts =
//                new HashMap<String, List<Mutation>>();
//        List<Mutation> cols = new ArrayList<Mutation>();
//        cols.add(nameMut);
//        cols.add(phoneMut);
//        cols.add(addressMut);
//        cols.add(cityMut);
//        cols.add(stateMut);
//        cols.add(zipMut);
//
//        muts.put(columnFamily, cols);
//
//        //outer map key is a row key
//        //inner map key is the column family name
//        mutationMap.put(rowKey.getBytes(), muts);
//        return mutationMap;
//    }

    //set up columns to insert for Cambria
//    private Map<byte[], Map<String, List<Mutation>>> createCambriaMutation(
//            String columnFamily, String cambriaKey)
//            throws UnsupportedEncodingException {
//
//        //set up columns for Cambria
//        Clock clock = new Clock(System.nanoTime());
//
//        Column cambriaNameCol = new Column("name".getBytes(UTF8),
//                "Cambria Suites Hayden".getBytes("UTF-8"), clock);
//        Column cambriaPhoneCol = new Column("phone".getBytes(UTF8),
//                "480-444-4444".getBytes(UTF8), clock);
//        Column cambriaAddressCol = new Column("address".getBytes(UTF8),
//                "400 N. Hayden".getBytes(UTF8), clock);
//        Column cambriaCityCol = new Column("city".getBytes(UTF8),
//                "Scottsdale".getBytes(UTF8), clock);
//        Column cambriaStateCol = new Column("state".getBytes(UTF8),
//                "AZ".getBytes("UTF-8"), clock);
//        Column cambriaZipCol = new Column("zip".getBytes(UTF8),
//                "85255".getBytes(UTF8), clock);
//
//        ColumnOrSuperColumn nameCosc = new ColumnOrSuperColumn();
//        nameCosc.column = cambriaNameCol;
//
//        ColumnOrSuperColumn phoneCosc = new ColumnOrSuperColumn();
//        phoneCosc.column = cambriaPhoneCol;
//
//        ColumnOrSuperColumn addressCosc = new ColumnOrSuperColumn();
//        addressCosc.column = cambriaAddressCol;
//
//        ColumnOrSuperColumn cityCosc = new ColumnOrSuperColumn();
//        cityCosc.column = cambriaCityCol;
//
//        ColumnOrSuperColumn stateCosc = new ColumnOrSuperColumn();
//        stateCosc.column = cambriaStateCol;
//
//        ColumnOrSuperColumn zipCosc = new ColumnOrSuperColumn();
//        zipCosc.column = cambriaZipCol;
//
//        Mutation nameMut = new Mutation();
//        nameMut.column_or_supercolumn = nameCosc;
//        Mutation phoneMut = new Mutation();
//        phoneMut.column_or_supercolumn = phoneCosc;
//        Mutation addressMut = new Mutation();
//        addressMut.column_or_supercolumn = addressCosc;
//        Mutation cityMut = new Mutation();
//        cityMut.column_or_supercolumn = cityCosc;
//        Mutation stateMut = new Mutation();
//        stateMut.column_or_supercolumn = stateCosc;
//        Mutation zipMut = new Mutation();
//        zipMut.column_or_supercolumn = zipCosc;
//
//        //set up the batch
//        Map<byte[], Map<String, List<Mutation>>> cambriaMutationMap =
//                new HashMap<byte[], Map<String, List<Mutation>>>();
//
//        Map<String, List<Mutation>> cambriaMuts =
//                new HashMap<String, List<Mutation>>();
//        List<Mutation> cambriaCols = new ArrayList<Mutation>();
//        cambriaCols.add(nameMut);
//        cambriaCols.add(phoneMut);
//        cambriaCols.add(addressMut);
//        cambriaCols.add(cityMut);
//        cambriaCols.add(stateMut);
//        cambriaCols.add(zipMut);
//
//        cambriaMuts.put(columnFamily, cambriaCols);
//
//        //outer map key is a row key
//        //inner map key is the column family name
//        cambriaMutationMap.put(cambriaKey.getBytes(), cambriaMuts);
//        return cambriaMutationMap;
//    }
}