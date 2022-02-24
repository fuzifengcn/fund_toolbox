package com.fzf.fund;

public class Launcher {


    public static void main(String[] args) {
        run();
    }





    private static void run() {

        FundContext fundContext = new FundContext(new EastMoneyDataSource());

    }


}
