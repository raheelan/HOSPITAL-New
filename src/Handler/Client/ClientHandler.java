package Handler.Client;

import BO.Client.Client;
import BO.CPT.CPT;
import BO.SetupColumnDetail;
import Handler.Setup.CPTHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import utilities.Constants;
import utilities.Database;
import utilities.DefinitionTypes;
import utilities.GenerateKeys;
import utilities.Keys;

public class ClientHandler implements java.io.Serializable {

    GenerateKeys key = new GenerateKeys();
    CPTHandler cpt = new CPTHandler();

    public List<SetupColumnDetail> selectClientProprties(String clientId) {

        String[] cols = {"-", "ID", "PROPERTY", "TABLE_ROW_ID", "TABLE_COLUMN_ID",
            "DESCRIPTION"};

        String query
                = "SELECT STC.ID, STC.PROPERTY, SCD.TABLE_ROW_ID, SCD.TABLE_COLUMN_ID,"
                + "\n NVL(SCD.DESCRIPTION, ' ') DESCRIPTION FROM "
                + "\n " + Database.DCMS.setupColumnDetail + " SCD,"
                + "\n " + Database.DCMS.setupTableColums + " STC"
                + "\n WHERE SCD.TABLE_ROW_ID = '" + clientId + "'"
                + "\n AND STC.ID = SCD.TABLE_COLUMN_ID"
                + "\n AND STC.ACTIVE = 'Y'";

        List<HashMap> list = Constants.dao.selectDatainList(query, cols);

        List<SetupColumnDetail> listItems = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            HashMap map = list.get(i);

            SetupColumnDetail setupProperties = new SetupColumnDetail();
            setupProperties.setId(map.get("ID").toString());
            setupProperties.setProperty(map.get("PROPERTY").toString());
            setupProperties.setTableRowId(map.get("TABLE_ROW_ID").toString());
            setupProperties.setTableColumnId(map.get("TABLE_COLUMN_ID").toString());
            setupProperties.setDefaultValue(map.get("DESCRIPTION").toString());
            listItems.add(setupProperties);
        }
        return listItems;
    }

    public boolean updateClientSetupProprties(List<SetupColumnDetail> listProperties) {
        boolean ret = true;
        for (int i = 0; i < listProperties.size(); i++) {
            SetupColumnDetail property = listProperties.get(i);

            String query
                    = " UPDATE " + Database.DCMS.setupColumnDetail
                    + "\n SET DESCRIPTION = '" + property.getDefaultValue() + "'"
                    + "\n WHERE TABLE_ROW_ID = '" + property.getTableRowId() + "'"
                    + "\n AND TABLE_COLUMN_ID = " + property.getTableColumnId();
            ret = Constants.dao.executeUpdate(query, false);
        }
        return ret;
    }

    public boolean insertClientPropertyHist(SetupColumnDetail property) {

        String query
                = " INSERT INTO " + Database.DCMS.setupColumnDetailHistory
                + "\n SELECT '" + property.getTableRowId() + "', '"
                + property.getTableColumnId() + "', '" + property.getDefaultValue()
                + "', '" + Constants.userId + "', " + Constants.today + ", '"
                + Constants.terminalId + "' FROM " + Database.DCMS.setupColumnDetail
                + " WHERE TABLE_ROW_ID = '" + property.getTableRowId() + "'";
        return Constants.dao.executeUpdate(query, false);
    }

    public boolean registerClient(Client client) {

        String[] columns = {Database.DCMS.client, "ID", "DESCRIPTION", "ACTIVE",
            "CRTD_BY", "CRTD_DATE", "CRTD_TERMINAL_ID", "TRANSACTION_TYPE"};

        HashMap clientMap = new HashMap();
        clientMap.put("ID", client.getClientId()); //"(SELECT MAX(ID) +1 FROM " + Database.DCMS.client + ")"
        clientMap.put("DESCRIPTION", "'" + client.getClientName() + "'");
        clientMap.put("ACTIVE", "'" + client.getActive() + "'");
        clientMap.put("TRANSACTION_TYPE", "'" + client.getTransactionType() + "'");
        clientMap.put("CRTD_BY", "'" + Constants.userId + "'");
        clientMap.put("CRTD_DATE", "SYSDATE");
        clientMap.put("CRTD_TERMINAL_ID", "'" + Constants.terminalId + "'");
        Vector vec = new Vector();
        vec.add(clientMap);
        return Constants.dao.insertData(vec, columns);
    }

    public boolean insertClientProperties(String clientId, String refClientId) {

        String query
                = " INSERT INTO " + Database.DCMS.setupColumnDetail
                + "\n SELECT '" + clientId + "', TABLE_COLUMN_ID, "
                + "DESCRIPTION, '" + Constants.userId + "', SYSDATE,"
                + "'" + Constants.terminalId + "' FROM "
                + Database.DCMS.setupColumnDetail
                + "\n WHERE TABLE_ROW_ID = '" + refClientId + "'";

        return Constants.dao.executeUpdate(query, false);
    }

    public Client searchClientById(String clientId) {

        String[] columns = {Database.DCMS.client, "ID", "DESCRIPTION", "ACTIVE",
            "TRANSACTION_TYPE", "ACTIVE"};

        String query
                = " SELECT "
                + " CLT.ID, CLT.DESCRIPTION ,                               \n"
                + " NVL(CLT.TRANSACTION_TYPE , 'CASH')TRANSACTION_TYPE,     \n"
                + " ACTIVE                                                  \n"
                + " FROM "
                + Database.DCMS.client + " CLT "
                + " WHERE CLT.ID = '" + clientId + "'";

        return (Client) (setStudyVector(Constants.dao.selectData(query, columns))).get(0);
    }

    public Vector searchClientByName(String clientName) {

        String[] columns = {Database.DCMS.client, "ID", "DESCRIPTION",
            "TRANSACTION_TYPE", "ACTIVE"};

        String query
                = " SELECT CLT.ID, CLT.DESCRIPTION,"
                + "\n NVL(CLT.TRANSACTION_TYPE , 'CASH')TRANSACTION_TYPE,"
                + "\n ACTIVE FROM"
                + "\n" + Database.DCMS.client + " CLT"
                + "\n WHERE UPPER(CLT.DESCRIPTION) LIKE '%" + clientName.toUpperCase() + "%'";
        System.out.println(query);
        return setStudyVector(Constants.dao.selectData(query, columns));
    }

    public Vector selectClientAttachedPackage(String packageDescription,
            String clientId) {
        String[] cols = {"", "ID", "DESCRIPTION",};
        String query = " SELECT PKG.ID ID ,     DESCRIPTION "
                + " FROM " + Database.DCMS.definitionTypeDetail + " PKG, \n"
                + Database.DCMS.clientWisePackage + "   CWP \n"
                + " WHERE DEF_TYPE_ID = " + DefinitionTypes.cptPackage + "\n"
                + " AND PKG.ID = CWP.PACKAGE_ID "
                + " AND CWP.CLIENT_ID = '" + clientId + "' \n"
                + " AND UPPER(DESCRIPTION) LIKE '%"
                + packageDescription.toUpperCase() + "%' \n"
                + " AND PKG.ACTIVE = 'Y' ";

        Vector vec = Constants.dao.selectData(query, cols);
        Vector vecTypeDetail = new Vector();
        for (int i = 0; i < vec.size(); i++) {
            HashMap map = (HashMap) vec.get(i);
            BO.Package typeDetail = new BO.Package();
            typeDetail.id = (String) map.get("ID");
            typeDetail.description = (String) map.get("DESCRIPTION");
            typeDetail.clientWisePackageId = (String) map.get("CWP_ID");
            typeDetail.clientId = clientId;
            vecTypeDetail.add(typeDetail);
        }
        return vecTypeDetail;
    }

    public Vector selectClientUnAttachedPackage(String packageDescription,
            String clientId) {
        String[] cols = {"", "ID", "DESCRIPTION"};
        String query = " SELECT   ID ,    DESCRIPTION "
                + " FROM " + Database.DCMS.definitionTypeDetail + " PKG \n"
                + " WHERE DEF_TYPE_ID = " + DefinitionTypes.cptPackage + "\n"
                + " AND UPPER(DESCRIPTION) LIKE '%"
                + packageDescription.toUpperCase() + "%' \n"
                + " AND PKG.ACTIVE = 'Y' \n"
                + " AND ID NOT IN (SELECT PACKAGE_ID FROM "
                + Database.DCMS.clientWisePackage + " "
                + " WHERE CLIENT_ID = '" + clientId + "') \n";

        Vector vec = Constants.dao.selectData(query, cols);
        Vector vecTypeDetail = new Vector();
        for (int i = 0; i < vec.size(); i++) {
            HashMap map = (HashMap) vec.get(i);
            BO.Package typeDetail = new BO.Package();
            typeDetail.id = (String) map.get("ID");
            typeDetail.description = (String) map.get("DESCRIPTION");
            typeDetail.clientWisePackageId = (String) map.get("CWP_ID");
            typeDetail.clientId = clientId;
            vecTypeDetail.add(typeDetail);
        }
        return vecTypeDetail;
    }

    public Vector setStudyVector(Vector clients) {

        Vector vec = new Vector();
        for (int i = 0; i < clients.size(); i++) {
            HashMap hashClient = (HashMap) clients.get(i);
            Client client = new Client();
            client.setActive((String) hashClient.get("ACTIVE"));
            client.setClientId((String) hashClient.get("ID"));
            client.setClientName((String) hashClient.get("DESCRIPTION"));
            client.setTransactionType((String) hashClient.get("TRANSACTION_TYPE"));
            vec.add(client);
        }//
        return vec;
    }

    public boolean updateClient(Client client) {

        String query
                = " UPDATE " + Database.DCMS.client + " \n SET "
                + " ACTIVE  = '" + client.getActive().trim() + "' ,"
                + " DESCRIPTION  = '" + client.getClientName().trim() + "',"
                + " TRANSACTION_TYPE  = '" + client.getTransactionType() + "'"
                + " WHERE ID = '" + client.getClientId() + "'";
        boolean ret = Constants.dao.executeUpdate(query, false);

        if (ret) {

            String hist
                    = " INSERT INTO " + Database.DCMS.updateClientHistory
                    + "( SELECT ID, DESCRIPTION, TRANSACTION_TYPE, ACTIVE,"
                    + " CRTD_BY, CRTD_DATE, CRTD_TERMINAL_ID, \n"
                    + " SYSDATE,'" + Constants.userId + "','"
                    + Constants.terminalId + "' \n"
                    + " FROM " + Database.DCMS.client + " WHERE "
                    + " ID = " + client.getClientId() + " )";
            ret = Constants.dao.executeUpdate(hist, ret);
        }
        if (!ret) {
            Constants.dao.rollBack();
        }
        return ret;
    }//

    public boolean updateClientCptTestLimit(int limit, String clientId, String cptId) {

        boolean ret = true;
        String query
                = " UPDATE " + Database.DCMS.clientWiseCPT + " \n SET "
                + " TEST_LIMIT  = '" + limit + "' \n"
                + " WHERE CLIENT_ID = '" + clientId + "' \n"
                + " AND CPT_ID = '" + cptId + "'";
        return Constants.dao.executeUpdate(query, ret);
    }

    public String[] selectActiveClient() {

        String[] cols = {"", "ID"};

        String query = " SELECT ID FROM  " + Database.DCMS.client
                + " WHERE ACTIVE = 'Y' ";

        Vector vec = Constants.dao.selectData(query, cols);
        String[] client = new String[vec.size()];

        for (int i = 0; i < vec.size(); i++) {

            HashMap map = (HashMap) vec.get(i);
            client[i] = map.get("ID").toString();
        }

        return client;
    }

    public Vector selectClientLocations(String clientId, String locationName) {

        String[] cols = {"", "LOC_ID", "LOCATION_DESCRIPTION"};
        String query
                = "SELECT CWL.LOCATION_ID LOC_ID,"
                + " LOC.DESCRIPTION LOCATION_DESCRIPTION        \n"
                + " FROM " + Database.DCMS.location + " LOC,    \n"
                + Database.DCMS.clientWiseLocation + " CWL      \n"
                + " WHERE CWL.CLIENT_ID = " + clientId + "      \n"
                + " AND CWL.LOCATION_ID = LOC.ID \n"
                + " AND UPPER(LOC.DESCRIPTION) LIKE UPPER('%" + locationName + "%')";

        Vector vec = Constants.dao.selectData(query, cols);
        Vector vecLocations = new Vector();
        for (int i = 0; i < vec.size(); i++) {
            Client client = new Client();
            HashMap map = (HashMap) vec.get(i);
            client.setLocationDescription(map.get("LOCATION_DESCRIPTION").toString());
            client.setLocationId(map.get("LOC_ID").toString());
            client.setClientId(clientId);
            vecLocations.add(client);
        }

        return vecLocations;
    }

    public Vector selectUnattachedClientLocations(String clientId, String locationName) {

        String[] cols = {"", "LOCATION_ID", "LOCATION_DESCRIPTION"};
        String query
                = " SELECT LOC.ID LOCATION_ID,LOC.DESCRIPTION LOCATION_DESCRIPTION  \n"
                + " FROM " + Database.DCMS.location + " LOC                         \n"
                + " WHERE LOC.ID NOT IN (SELECT CWL.LOCATION_ID FROM                \n"
                + Database.DCMS.clientWiseLocation + " CWL "
                + " WHERE CWL.CLIENT_ID = '" + clientId + "') \n"
                + " AND UPPER(LOC.DESCRIPTION) LIKE UPPER('%" + locationName + "%')";

        Vector vec = Constants.dao.selectData(query, cols);
        Vector vecLocations = new Vector();
        for (int i = 0; i < vec.size(); i++) {
            Client client = new Client();
            HashMap map = (HashMap) vec.get(i);
            client.setLocationId(map.get("LOCATION_ID").toString());
            client.setLocationDescription(map.get("LOCATION_DESCRIPTION").toString());
            client.setClientId(clientId);
            vecLocations.add(client);
        }

        return vecLocations;
    }

    public boolean insertClientWiseCPT(String fromClientId, String toClientId) {

        List<Client> listClientCPT = selectAllClientCPT(fromClientId);
        List<HashMap> listData = new ArrayList<>();

        String[] columns = {Database.DCMS.clientWiseCPT, "CPT_ID",
            "CLIENT_ID", "CONTRACT_PRICE", "CONTRACT_STATUS"};

        for (int i = 0; i < listClientCPT.size(); i++) {
            Client cpt = listClientCPT.get(i);
            HashMap CPTMap = new HashMap();
            CPTMap.put("CPT_ID", "'" + cpt.getCptId() + "'");
            CPTMap.put("CLIENT_ID", "'" + toClientId + "'");
            CPTMap.put("CONTRACT_PRICE", "'" + cpt.getContactPrice() + "'");
            CPTMap.put("CONTRACT_STATUS", "'" + cpt.getContactStatus() + "'"); //At Start there is not contract in current logic
            listData.add(CPTMap);
        }
        return Constants.dao.insertData(listData, columns);
    }

    public boolean insertClientLocation(String locationId, String clientId) {
        String[] cols = {Database.DCMS.clientWiseLocation, "LOCATION_ID", "CLIENT_ID", "ID"};

        HashMap clientMap = new HashMap();
        clientMap.put("ID", "'" + key.generatePrimaryKey(Keys.clientWiseLocation, false) + "'");
        clientMap.put("CLIENT_ID", "'" + clientId + "'");
        clientMap.put("LOCATION_ID", "'" + locationId + "'");

        Vector vec = new Vector();
        vec.add(clientMap);
        return Constants.dao.insertData(vec, cols);
    }

    public boolean deleteClientLocation(String locId, String clientId) {

        return true;
    }

    public boolean insertClientWiseBOCpt(Vector vec) {
        String[] cols = {Database.DCMS.clientWiseCPT,
            "CPT_ID", "CLIENT_ID", "CONTRACT_PRICE", "ACTIVE"};
        Vector inserts = new Vector();

        for (int i = 0; i < vec.size(); i++) {
            HashMap cptMap = new HashMap();
            CPT cpt = (CPT) vec.get(i);
            cptMap.put("CPT_ID", "'" + cpt.cptId + "'");
            cptMap.put("CLIENT_ID", "'" + cpt.clientId + "'");
            cptMap.put("CONTRACT_PRICE", "'" + cpt.price + "'");
            cptMap.put("ACTIVE", "'Y'");
            inserts.add(cptMap);
        }
        return Constants.dao.insertData(inserts, cols);
    }

    public boolean insertClientWiseCpt(List<Client> vec) {
        String[] cols = {Database.DCMS.clientWiseCPT,
            "CPT_ID", "CLIENT_ID", "CONTRACT_PRICE", "ACTIVE"};
        Vector inserts = new Vector();

        for (int i = 0; i < vec.size(); i++) {
            HashMap cptMap = new HashMap();
            Client clt = (Client) vec.get(i);
            cptMap.put("CPT_ID", "'" + clt.getCptId() + "'");
            cptMap.put("CLIENT_ID", "'" + clt.getClientId() + "'");
            cptMap.put("CONTRACT_PRICE", "'" + clt.getContactPrice() + "'");
            cptMap.put("ACTIVE", "'Y'");

            inserts.add(cptMap);
        }
        return Constants.dao.insertData(inserts, cols);
    }

    public boolean insertClientWisePackages(Vector vec) {
        String[] cols = {Database.DCMS.clientWisePackage,
            "CLIENT_ID", "PACKAGE_ID"};
        Vector inserts = new Vector();

        for (int i = 0; i < vec.size(); i++) {
            HashMap cptMap = new HashMap();
            BO.Package lov = (BO.Package) vec.get(i);
            cptMap.put("PACKAGE_ID", "'" + lov.id + "'");
            cptMap.put("CLIENT_ID", "'" + lov.clientId + "'");
            inserts.add(cptMap);
        }
        return Constants.dao.insertData(inserts, cols);
    }

    public boolean checkIfAlreadyClientWiseCptInserted(CPT cpt) {
        String[] cols = {"CPT_ID"};

        String query
                = "SELECT CPT_ID FROM \n"
                + Database.DCMS.clientWiseCPT + " \n"
                + " WHERE CLIENT_ID = '" + cpt.clientId + "' \n"
                + " AND CPT_ID ='" + cpt.cptId + "'";

        Vector vec = Constants.dao.selectData(query, cols);

        if (vec.size() < 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean insertClientCPTUpdateHistory(CPT cpt) {
        String[] cols = {Database.DCMS.clientCptUpdateHistory,
            "CPT_ID", "CLIENT_ID", "CONTRACT_PRICE", "TEST_LIMIT", "STATUS",
            "PERCENTAGE_DISCOUNT", "UPTD_BY", "UPTD_TERMINAL", "UPTD_DATE", "ID",
            "REMARKS"};

        HashMap cptMap = new HashMap();
        cptMap.put("CPT_ID", "'" + cpt.cptId + "'");
        cptMap.put("CLIENT_ID", "'" + cpt.clientId + "'");
        cptMap.put("CONTRACT_PRICE", "'" + cpt.price + "'");
        cptMap.put("TEST_LIMIT", "'" + cpt.previousTestLimit + "'");
        cptMap.put("STATUS", "'" + cpt.status + "'");
        cptMap.put("REMARKS", "'" + cpt.comments + "'");
        cptMap.put("PERCENTAGE_DISCOUNT", "'" + cpt.percentageDiscount + "'");
        cptMap.put("UPTD_BY", "'" + Constants.userId + "'");
        cptMap.put("UPTD_TERMINAL", "'" + Constants.terminalId + "'");
        cptMap.put("UPTD_DATE", "SYSDATE");
        cptMap.put("ID", "'" + key.generatePrimaryKey(Keys.clientCPTUpdateHistory, false) + "'");

        Vector vec = new Vector();
        vec.add(cptMap);
        return Constants.dao.insertData(vec, cols);
    }

    public void updateInvoiceMasterClientId() {

        String[] cols = {"", "COMPLETE_ORDER_NO", "CLIENT_ID"};

        String query = " SELECT COMPLETE_ORDER_NO , CLIENT_ID FROM "
                + Database.DCMS.orderMaster + " WHERE CLIENT_UPDATED = 'N' ";

        List list = Constants.dao.selectDatainList(query, cols);
        String completeOrderNo = "";
        String clientId = "";
        boolean ret = true;
        for (int i = 0; i < list.size(); i++) {

            System.out.println("Current Record in Progress " + (i + 1) + " out of " + list.size());

            HashMap map = (HashMap) list.get(i);
            completeOrderNo = map.get("COMPLETE_ORDER_NO").toString();
            clientId = map.get("CLIENT_ID").toString();

            String update = " UPDATE INVOICE_MASTER "
                    + " SET CLIENT_ID = '" + clientId + "' "
                    + " WHERE COMPLETE_ORDER_NO = '" + completeOrderNo + "'";

            ret = Constants.dao.executeUpdate(update, false);

            if (ret) {
                update = " UPDATE ORDER_MASTER "
                        + " SET CLIENT_UPDATED = 'Y' "
                        + " WHERE COMPLETE_ORDER_NO = '" + completeOrderNo + "'";
                Constants.dao.executeUpdate(update, ret);
            }
        }
    }

    private List<Client> selectAllClientCPT(String clientId) {

        String[] colums = {Database.DCMS.clientWiseCPT, "CPT_ID",
            "CONTRACT_PRICE", "CONTRACT_STATUS"};

        String query
                = " SELECT                                                 \n"
                + " CWC.CPT_ID CPT_ID,                                     \n"
                + " CWC.CONTRACT_PRICE      CONTRACT_PRICE,                \n"
                + " CWC.CONTRACT_STATUS    CONTRACT_STATUS                 \n"
                + " FROM                                                   \n"
                + Database.DCMS.clientWiseCPT + "   CWC                    \n"
                + " WHERE CLIENT_ID = '" + clientId + "'";

        Vector vec = Constants.dao.selectData(query, colums);
        List<Client> listCPT = new ArrayList();

        for (int i = 0; i < vec.size(); i++) {
            HashMap map = (HashMap) vec.get(i);
            Client cpt = new Client();
            cpt.setCptId((String) map.get("CPT_ID"));
            cpt.setContactPrice((String) map.get("CONTRACT_PRICE"));
            cpt.setContactStatus((String) map.get("CONTRACT_STATUS"));
            listCPT.add(cpt);
        }
        return listCPT;
    }
}
