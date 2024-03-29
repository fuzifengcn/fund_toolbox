package com.fzf.oop.classic;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.fzf.oop.base.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassicFundContext extends FundContext<ClassicFundInfo> {
    List<BaseFundHistoryNavInfo> QUARTERLY_DATA_LIST;
    List<BaseFundHistoryNavInfo> LAST_MONTH_DATA_LIST;
    List<BaseFundHistoryNavInfo> CURRENT_MONTH_DATA_LIST;


    public ClassicFundContext(FundContextRuntimeConfig contextRuntimeConfig, FundDataSource fundDataSource) {
        super(contextRuntimeConfig, fundDataSource);
    }

    @Override
    protected CellWriteHandler getCellWriteHandler() {
        return new MyCellStyleHandler();
    }

    @Override
    public Class<ClassicFundInfo> getExportEntityClass() {
        return ClassicFundInfo.class;
    }

    @Override
    protected String getHistoryNavStartDate() {
        return getLAST_QUARTERLY_START();
    }

    @Override
    protected String getHistoryNavEndDate() {
        return getSimpleDateFormat().format(new Date());
    }


    @Override
    protected List<ClassicFundInfo> processData(BaseFundInfo baseFundInfo, List<BaseFundHistoryNavInfo> navInfos) {
        CURRENT_MONTH_DATA_LIST = new ArrayList<>();
        LAST_MONTH_DATA_LIST = new ArrayList<>();
        QUARTERLY_DATA_LIST = new ArrayList<>();
        List<ClassicFundInfo> result = new ArrayList<>();
        spiltHistoryNavList(navInfos);
        ClassicFundInfo fundInfo = new ClassicFundInfo();
        try {
            fundInfo.setFundCode(baseFundInfo.getFundCode());
            fundInfo.setFundName(baseFundInfo.getFundName());
            fundInfo.setCreatAt(baseFundInfo.getFundCreatedAt());
            fundInfo.setCurrentNAV(baseFundInfo.getCurrentDayNav());
            fundInfo.setLastDayNAV(navInfos.get(0).getDailyGrowth());
            fundInfo.setCurrentMonthNAV(getNav(CURRENT_MONTH_DATA_LIST));
            fundInfo.setLastMonthNAV(getNav(LAST_MONTH_DATA_LIST));
            fundInfo.setLastQuarterlyNAV(getNav(QUARTERLY_DATA_LIST));
            fundInfo.setCurrentDate(baseFundInfo.getCurrentDate());
            result.add(fundInfo);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error111:" + baseFundInfo.getFundCode());
        }
        return result;
    }

    @Override
    protected void sort(List<ClassicFundInfo> data) {
        data.sort((o1, o2) -> {
            if (o1.getCurrentNAV() == null || "".equals(o1.getCurrentNAV())) {
                return -1;
            }
            if (o2.getCurrentNAV() == null || "".equals(o2.getCurrentNAV())) {
                return 1;
            }
            return -(new BigDecimal(o1.getCurrentNAV()).compareTo(new BigDecimal(o2.getCurrentNAV())));
        });
    }

    private void spiltHistoryNavList(List<BaseFundHistoryNavInfo> navInfos) {
        for (BaseFundHistoryNavInfo baseFundHistoryNavInfo : navInfos) {
            String dateTime = baseFundHistoryNavInfo.getDateTime();
            if (getLAST_QUARTERLY_START().compareTo(dateTime) <= 0
                    && getLAST_QUARTERLY_END().compareTo(dateTime) >= 0) {
                QUARTERLY_DATA_LIST.add(baseFundHistoryNavInfo);
            }
            if (getLAST_MONTH_START().compareTo(dateTime) <= 0
                    && getLAST_MONTH_END().compareTo(dateTime) >= 0) {
                LAST_MONTH_DATA_LIST.add(baseFundHistoryNavInfo);
            }

            if (getCURRENT_MONTH_START().compareTo(dateTime) <= 0) {
                CURRENT_MONTH_DATA_LIST.add(baseFundHistoryNavInfo);
            }
        }
    }

    private String getNav(List<BaseFundHistoryNavInfo> navInfos) {
        if (navInfos.size() > 0) {
            BaseFundHistoryNavInfo max = navInfos.get(0);
            BaseFundHistoryNavInfo min = navInfos.get(navInfos.size() - 1);
            BigDecimal maxNAV = new BigDecimal(max.getNavValue());
            BigDecimal minNAV = new BigDecimal(min.getNavValue());
            // 计算涨幅
            BigDecimal nav = maxNAV.subtract(minNAV)
                    .divide(minNAV, 5, BigDecimal.ROUND_DOWN)
                    .multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_DOWN);
            return nav.toString();
        } else {
            return "";
        }
    }

}
