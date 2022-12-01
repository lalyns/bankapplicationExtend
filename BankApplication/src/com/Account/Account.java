package com.Account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.IOmanager;
import com.Bank.Bank;
import com.Trade.Trade;
import com.Trade.Trade.TradeType;

public class Account {
    // 상수
    public static final String TRADEPATH = "src/com/Trade/";

    // 멤버 변수
    private String user;
    private String accountNumber;
    private int balance;
    private String bankName;
    private String fileName;
    private List<Trade> trades;

    // 생성자
    public Account() {
    }

    
    public Account(String user, String accountNumber, int balance, String bankName, String fileName) {
        this.user           = user;
        this.accountNumber  = accountNumber;
        this.balance        = balance;
        this.bankName       = bankName;
        this.fileName       = fileName;

        IOmanager io = new IOmanager();
        List<List<String>> tradeLists = io.readCSV(TRADEPATH + fileName);
        trades = new ArrayList<>();
        for (int i=1; i<tradeLists.size(); i++)
        {
            String date = tradeLists.get(i).get(0);
            String time = tradeLists.get(i).get(1);
            int type = Integer.valueOf(tradeLists.get(i).get(2));
            TradeType tradeType = TradeType.valueOfLabel(type);
            int fee =  Integer.valueOf(tradeLists.get(i).get(3));
            String tradeBankName = tradeLists.get(i).get(4);

            Trade newtrade = new Trade(date, time, this.accountNumber, tradeType, fee, tradeBankName);
            trades.add(newtrade);
        }
    }

    // 매소드
    // 입금
    public void deposit(int fee) {
        this.balance += fee;
        record(fee, TradeType.Deposit);
    }
    
    // 출금
    public void withdraw(int fee) {
        this.balance -= fee;
        record(fee, TradeType.Withdraw);
    }

    // 입출금을 기록하는 매소드
    void record(int fee, TradeType tradeType) {
        LocalDateTime dt = LocalDateTime.now();
        String date = dt.format((DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String time = dt.format((DateTimeFormatter.ofPattern("HH:mm:ss")));
        int type = tradeType.label();
        String tradeBankName = this.bankName;

        Trade newTrade = new Trade(date, time, accountNumber, tradeType, fee, tradeBankName);
        trades.add(newTrade);
        String temp[] = {date, time, String.valueOf(type), String.valueOf(fee), tradeBankName};
        List<String> list = Arrays.asList(temp);
        

        IOmanager io = new IOmanager();
        io.writeCSV(TRADEPATH + this.fileName, list, true);
        Bank.getInstance().notifyAccountInfoChange(accountNumber, infotoList(this));
    }

    List<String> infotoList(Account account) {
        String temp[] = {this.user, this.accountNumber, String.valueOf(this.balance), this.bankName, this.fileName };
        return Arrays.asList(temp);
    }

    // 계좌 수정시 사용될 매소드
    public void setUser(String user) {
        this.user = user;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    // 거래 내역 조회
    public void searchAllTrades() {
        for (int i = 0; i < this.trades.size(); i++) {
            Trade trade = trades.get(i);
            String tradeType = trade.getTradeType() == TradeType.Deposit ? "입금" : "출금";
            System.out.println("일자: " + trade.getDate() + " " +
                    "  시간: " + trade.getTime() + " " +
                    "  입/출금 여부:" + tradeType + " " +
                    "  금액:" + trade.getFee() + " " +
                    "  은행명: " + trade.getBankName());
        }
    }

    // 잔고확인
    public String getUser() { return user; }
    public String getAccountNumber() { return accountNumber; }
    public int getBalance() { return balance; }
    public List<Trade> getTrades() { return trades; }
    public String getBankName() { return bankName; }
    public String getFileName() { return fileName; }
    
}
