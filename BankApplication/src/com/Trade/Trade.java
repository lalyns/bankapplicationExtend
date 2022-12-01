package com.Trade;

import java.util.Arrays;

public class Trade {
    
    public enum TradeType {
        Deposit(1),
        Withdraw(2);

        private final int label;

        TradeType(int label) {
            this.label = label;
        }
        
        public int label() {
            return label;
        }

        public static TradeType valueOfLabel(int label) {
            return Arrays.stream(values())
                        .filter(value -> value.label == label)
                        .findAny()
                        .orElse(null);
        }
    }

    private String date;
    private String time;
    private String accountNumber;
    private TradeType tradeType;
    private int fee;
    private String bankName;
    
    // 생성자
    public Trade() {
    }

    public Trade(String date, String time, String accountNumber, TradeType tradeType, int fee, String bankName) {
        this.date = date;
        this.time = time;
        this.accountNumber = accountNumber;
        this.tradeType = tradeType;
        this.fee = fee;
        this.bankName = bankName;
    }

    // 메소드
    // 게터
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getAccountNumber() { return accountNumber; }
    public TradeType getTradeType() { return tradeType; }
    public int getFee() { return fee; }
    public String getBankName() { return bankName; }
    
}
