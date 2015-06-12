/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proteingraphs;

import proteinstructure.Residue;
import proteinstructure.SSE;

/**
 *
 * @author ts
 */
public class SSECreator {
    
    public SSE createDefaultSSE(Integer vClass, Integer numInChain) {
        SSE sse = new SSE(vClass);
        Residue r = new Residue(numInChain, numInChain);
            r.setAAName1("A");
            r.setChainID("A");
            r.setiCode("");
            sse.addResidue(r);
            if(vClass.equals(SSE.SSECLASS_LIGAND)) {
                r.setResName3("LIG");
            }
            sse.setSeqSseChainNum(numInChain);
        return sse;
    }
    
}
