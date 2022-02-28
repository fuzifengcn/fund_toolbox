package com.fzf.oop.base;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextFileFundCodeLoader {


    public static List<FundCodes> load(FundContext fundContext) {

        try {
            return readFundCodeFromFile(fundContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 读取基金代码从文件中
    private static List<FundCodes> readFundCodeFromFile(FundContext fundContext) throws IOException {

        FundContextRuntimeConfig contextRuntimeConfig = fundContext.getContextRuntimeConfig();
        String fundCodeFilePathStr = contextRuntimeConfig.getFundCodeFilePath();
        File fundCodeFilePath = new File(fundCodeFilePathStr);
        if (!fundCodeFilePath.exists()) {
            System.err.println("fund code file path not exist!");
        }

        File[] files = fundCodeFilePath.listFiles(
                (dir, name) -> name.startsWith(contextRuntimeConfig.getFundCodeFilePrefix()));
        List<FundCodes> result = new ArrayList<>();
        if(files != null && files.length>0){

            for (File file : files) {
                StringBuilder stringBuilder = new StringBuilder();
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String strTmp = "";
                while ((strTmp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(strTmp);
                }

                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String s = stringBuilder.toString()
                        .replaceAll("\\\n", "")
                        .replaceAll("\\r\\n", "")
                        .replaceAll("，", ",").trim();
                String[] codes = s.split(",");
                FundCodes fundCodes = new FundCodes();
                fundCodes.setFileName(file.getName());
                fundCodes.setFundCodes(codes);
                result.add(fundCodes);
            }
        }
        return result;
    }

}
