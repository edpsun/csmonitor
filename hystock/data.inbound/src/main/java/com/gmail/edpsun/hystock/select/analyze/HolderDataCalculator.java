/**
 * JavaTools_declaration
 */
package com.gmail.edpsun.hystock.select.analyze;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.model.HolderStat;
import com.gmail.edpsun.hystock.util.MathTool;
import com.gmail.edpsun.hystock.util.MathTool.VO;

/**
 * @author esun
 * @version JavaTools_version Create Date:May 1, 2011 4:40:32 PM
 */
public class HolderDataCalculator {
    static float THRESHOLDER_HOLDER_INCREASE_RATE_IGNORE = 0.05f;
    static float THRESHOLDER_AVG_AMOUNT_INCREASE_RATE_AS_INCREASE = -6f;
    static int THRESHOLDER_AVG_AMOUNT_DECREASE_TIMES = 2;

    public void analyze(InboundContext ctx) throws Exception {
        calcHolderAmount(ctx);
        calcHolderNumber(ctx);
    }

    private void calcHolderAmount(InboundContext ctx) {
        List<HolderStat> holderStats = ctx.getStock().getHolderStats();

        Float[] amountChanges = new Float[holderStats.size()];
        for (int i = 0; i < holderStats.size(); i++) {
            amountChanges[i] = new Float(NumberUtils.createFloat(holderStats.get(i).getDelta()));
        }

        VO vo = MathTool.calcDataSeriesMTThresholder(amountChanges, THRESHOLDER_AVG_AMOUNT_INCREASE_RATE_AS_INCREASE,
                THRESHOLDER_AVG_AMOUNT_DECREASE_TIMES);
        ctx.getAnalyzeVO().setAverageAmountChange(vo.getChange());
        ctx.getAnalyzeVO().setAverageAmountUpQNum(vo.getQNum());
    }

    private void calcHolderNumber(InboundContext ctx) {
        List<HolderStat> holderStats = ctx.getStock().getHolderStats();
        Float[] holderNums = new Float[holderStats.size()];
        for (int i = 0; i < holderStats.size(); i++) {
            holderNums[i] = new Float(holderStats.get(i).getHolderNum());
        }

        VO vo = MathTool.calcDataSeriesUp(holderNums, THRESHOLDER_HOLDER_INCREASE_RATE_IGNORE);
        ctx.getAnalyzeVO().setHolderChange(vo.getChange());
        ctx.getAnalyzeVO().setHolderUpQNum(vo.getQNum());
    }

}
