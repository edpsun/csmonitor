/**
 * JavaTools_declaration
 */
package com.gmail.edpsun.hystock.select.analyze;

import java.util.List;

import com.gmail.edpsun.hystock.inbound.InboundContext;
import com.gmail.edpsun.hystock.model.AnalyzeVO;
import com.gmail.edpsun.hystock.model.HolderStat;

public class HolderDataAnalyzer {
    static int LEVEL_BASE = 5;
    static int LEVEL_STEP = -1;

    // static float THRESHOLDER_HOLDER_CHANGE_RATE = 0.85f;
    // static int THRESHOLDER_HOLDER_QNUM = 2;
    // static int THRESHOLDER_AVG_AMOUNT_QNUM = 5;
    // static float TRESHOLDER_CANNON = 30f;

    static float THRESHOLDER_HOLDER_CHANGE_RATE = Float.parseFloat(System.getProperty("THRESHOLDER_HOLDER_CHANGE_RATE",
            "0.85f"));
    static int THRESHOLDER_HOLDER_QNUM = Integer.parseInt(System.getProperty("THRESHOLDER_HOLDER_QNUM", "3"));
    static int THRESHOLDER_AVG_AMOUNT_QNUM = Integer.parseInt(System.getProperty("THRESHOLDER_AVG_AMOUNT_QNUM", "8"));
    static float TRESHOLDER_CANNON = Float.parseFloat(System.getProperty("TRESHOLDER_CANNON", "30f"));

    static {
        System.out.println(String.format("Analyzer   - %-50s" + " - Value(%s): %s", "THRESHOLDER_HOLDER_CHANGE_RATE",
                "0.85f", THRESHOLDER_HOLDER_CHANGE_RATE));
        System.out.println(String.format("Analyzer   - %-50s" + " - Value(%s): %s", "THRESHOLDER_HOLDER_QNUM", "2",
                THRESHOLDER_HOLDER_QNUM));
        System.out.println(String.format("Analyzer   - %-50s" + " - Value(%s): %s", "THRESHOLDER_AVG_AMOUNT_QNUM", "5",
                THRESHOLDER_AVG_AMOUNT_QNUM));
        System.out.println(String.format("Analyzer   - %-50s" + " - Value(%s): %s", "TRESHOLDER_CANNON", "30f",
                TRESHOLDER_CANNON));
    }

    public boolean analyze(InboundContext ctx) throws Exception {
        AnalyzeVO avo = ctx.getAnalyzeVO();

        boolean b1 = checkHolderNum(ctx);
        boolean b2 = checkAvgAmount(ctx);
        findCannon(ctx);

        if (b1 || b2) {
            return true;
        } else if (ctx.getKeepAll()) {
            avo.addTag("By_ALL");
            avo.setLevel("" + (LEVEL_BASE - 4 * LEVEL_STEP));
            return true;
        } else {
            return false;
        }
    }

    private void findCannon(InboundContext ctx) {
        List<HolderStat> holderStats = ctx.getStock().getHolderStats();
        AnalyzeVO avo_origin = ctx.getAnalyzeVO();

        if (avo_origin.getLevel() == null) {
            return;
        }

        if (holderStats.get(0).getDeltaInFloat() >= 100) {
            avo_origin.addTag("CANNON_1");
        } else if (holderStats.get(0).getDeltaInFloat() >= TRESHOLDER_CANNON
                && holderStats.get(1).getDeltaInFloat() >= TRESHOLDER_CANNON) {
            avo_origin.addTag("CANNON_2x30");
        } else if (holderStats.get(0).getDeltaInFloat() >= 10 && holderStats.get(1).getDeltaInFloat() >= 10
                && holderStats.get(2).getDeltaInFloat() >= 10) {
            avo_origin.addTag("CANNON_3x10");
        }
    }

    private boolean checkHolderNum(InboundContext ctx) {
        AnalyzeVO avo = ctx.getAnalyzeVO();
        if (avo.getHolderChange() < THRESHOLDER_HOLDER_CHANGE_RATE && avo.getHolderChange() > 0) {
            avo.setLevel("" + (LEVEL_BASE + 3 * LEVEL_STEP));

            int quarterNum = avo.getHolderUpQNum();
            if (quarterNum <= 1) {
                return false;
            } else {
                avo.addTag("(HolderNum--)=Q" + avo.getHolderUpQNum());
                avo.setSublevel("" + (LEVEL_BASE * 2 + avo.getHolderUpQNum() * LEVEL_STEP));
                return true;
            }
        }
        return false;
    }

    private boolean checkAvgAmount(InboundContext ctx) {
        AnalyzeVO avo = ctx.getAnalyzeVO();

        boolean isInclude = false;
        int l = 0;
        if (avo.getHolderUpQNum() >= THRESHOLDER_HOLDER_QNUM) {
            isInclude = true;
            avo.addTag("(HolderNum--)=Q" + avo.getHolderUpQNum());
            avo.setSublevel("" + (LEVEL_BASE * 2 + avo.getHolderUpQNum() * LEVEL_STEP));
            l++;
        }

        if (avo.getAverageAmountUpQNum() >= THRESHOLDER_AVG_AMOUNT_QNUM) {
            isInclude = true;
            avo.addTag("(AverageAmount++)=Q" + avo.getAverageAmountUpQNum());
            avo.setSublevel("" + (LEVEL_BASE * 2 + avo.getAverageAmountUpQNum() * LEVEL_STEP));
            l++;
        }

        if (isInclude) {
            if (avo.getLevel() == null)
                avo.setLevel("" + (LEVEL_BASE + l * LEVEL_STEP));
            return true;
        }
        return false;
    }
}
