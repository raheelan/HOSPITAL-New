/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CardiacRegistry.Controller;

import CardiacRegistry.BO.PerfusionistBO;
import CardiacRegistry.Handler.PerfusionistHandler;
import java.util.List;
import org.bridj.demangling.Demangler;
import utilities.Constants;
import utilities.GenerateKeys;
import utilities.Keys;

/**
 *
 * @author admin
 */
public class PerfusionistController {
    
    GenerateKeys key = new GenerateKeys();
    PerfusionistHandler hdlPerfusionist = new PerfusionistHandler();
    
    public boolean insertBloodGases(PerfusionistBO insert) {
        insert.setBloodGasesId(key.generatePrimaryKey(Keys.cardiacPerfusionIdPk, true));
        boolean ret = hdlPerfusionist.insertBloodGases(insert);
        if(ret){
            Constants.dao.commitTransaction();
        }
        if(!ret){
            Constants.dao.rollBack();
        }
        return ret;
    }
    
    public List<PerfusionistBO> selectBloodGases(String cardiacId) {
        return hdlPerfusionist.selectBloodGases(cardiacId);
    }
}