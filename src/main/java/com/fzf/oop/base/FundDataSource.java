package com.fzf.oop.base;

import java.util.List;

public interface FundDataSource {

    BaseFundInfo getBaseFundInfo(String code);

    List<BaseFundHistoryNavInfo> getHistoryNavList(String startDate,String endDate, String code);
}
