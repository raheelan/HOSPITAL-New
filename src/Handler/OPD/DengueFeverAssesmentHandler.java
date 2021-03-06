/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handler.OPD;

import BO.OPD.DengueFeverAssesmentBO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utilities.Constants;
import utilities.Database;
import utilities.DefinitionTypes;

/**
 *
 * @author admin
 */
public class DengueFeverAssesmentHandler {

    public List<DengueFeverAssesmentBO> selectDengueAssetmentMaster(String con,
            String odi) {

        String colums[] = {"-", "CON", "ODI", "EXAM_ID"};

        String query
                = " SELECT DAM.CON, DAM.ODI, DAM.EXAM_ID FROM \n"
                + Database.DCMS.dengueAssestmentMaster + " DAM \n"
                + " WHERE DAM.CON = '" + con + "'         \n"
                + " AND DAM.ODI = " + odi + "             \n";

        List<HashMap> lis = Constants.dao.selectDatainList(query, colums);
        List<DengueFeverAssesmentBO> listParameter = new ArrayList<>();
        for (HashMap map : lis) {

            DengueFeverAssesmentBO objParameter = new DengueFeverAssesmentBO();

            objParameter.setCON(map.get("CON").toString());
            objParameter.setODI(map.get("ODI").toString());
            objParameter.setDetailId(map.get("EXAM_ID").toString());

            listParameter.add(objParameter);
        }
        return listParameter;
    }

    public boolean insertDefinitionsInDengueMaster(String con, String odi) {
        String query
                = " INSERT INTO " + Database.DCMS.dengueAssestmentMaster + "    \n"
                + "(CON, ODI, EXAM_ID) SELECT '" + con + "', '" + odi + "', ID FROM \n"
                + Database.DCMS.definitionTypeDetail
                + " WHERE DEF_TYPE_ID >= " + DefinitionTypes.dengMedicalHistory + "\n"
                + " AND DEF_TYPE_ID <= " + DefinitionTypes.dengueDisposal + "\n";

        return Constants.dao.executeUpdate(query, false);
    }

    public List<DengueFeverAssesmentBO> selectDengueDefinitions(String defTypeId,
            String con, String odi) {

        String colums[] = {"-", "CON", "ODI", "EXAM_ID", "DEF_TYPE_ID", "DESCRIPTION",
            "ADDITIONAL_INFO", "SELECTION", "REMARKS"};

        String query
                = " SELECT DAM.CON, DAM.ODI, DAM.EXAM_ID, DTD.DEF_TYPE_ID, DTD.DESCRIPTION,\n"
                + "DTD.ADDITIONAL_INFO, DAM.SELECTION, NVL(DAM.REMARKS, ' ') REMARKS\n"
                + " FROM " + Database.DCMS.definitionTypeDetail + " DTD,\n"
                + Database.DCMS.dengueAssestmentMaster + " DAM          \n"
                + " WHERE CON = '"+ con +"'                             \n"
                + " AND ODI = "+ odi +"                                 \n"
                + "AND DTD.DEF_TYPE_ID = " + defTypeId + "              \n"
                + " AND DAM.EXAM_ID = DTD.ID                            \n";

        List<HashMap> lis = Constants.dao.selectDatainList(query, colums);
        List<DengueFeverAssesmentBO> listParameter = new ArrayList<>();
        for (HashMap map : lis) {

            DengueFeverAssesmentBO objParameter = new DengueFeverAssesmentBO();

            objParameter.setCON(map.get("CON").toString());
            objParameter.setODI(map.get("ODI").toString());
            objParameter.setDetailId(map.get("EXAM_ID").toString());
            objParameter.setDefTypeId(map.get("DEF_TYPE_ID").toString());
            objParameter.setDetailDescription(map.get("DESCRIPTION").toString());
            objParameter.setAdditionlaInfo(map.get("ADDITIONAL_INFO").toString());
            objParameter.setSelection(map.get("SELECTION").toString());
            objParameter.setRemarks(map.get("REMARKS").toString());

            listParameter.add(objParameter);
        }
        return listParameter;
    }

    public boolean updateDengueAsst(DengueFeverAssesmentBO objUpdt) {
        String query
                = " UPDATE " + Database.DCMS.dengueAssestmentMaster + "\n"
                + " SET SELECTION = '" + objUpdt.getSelection() + "'\n"
                + " WHERE CON = '" + objUpdt.getCON() + "'\n"
                + " AND ODI =  " + objUpdt.getODI() + "  \n"
                + " AND EXAM_ID = '" + objUpdt.getDetailId() + "'\n";

        return Constants.dao.executeUpdate(query, false);
    }

    public boolean updateRemarks(DengueFeverAssesmentBO objUpdt) {
        String query
                = " UPDATE " + Database.DCMS.dengueAssestmentMaster + "\n"
                + " SET REMARKS = '" + objUpdt.getRemarks() + "'\n"
                + " WHERE CON = '" + objUpdt.getCON() + "'\n"
                + " AND ODI = " + objUpdt.getODI() + "    \n"
                + " AND EXAM_ID = '" + objUpdt.getDetailId() + "'\n";

        return Constants.dao.executeUpdate(query, false);
    }
}
