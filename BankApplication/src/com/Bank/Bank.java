package com.Bank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.IOmanager;
import com.Account.Account;

import Main.UserInterface;

public class Bank {
    // 상수
    final String ACCOUNTPATH = "src/com/account.csv";

    // 멤버변수
    List<Account> accounts;
    public List<Account> getAccounts() {
        return accounts;
    }

    IOmanager io;

    // 싱글톤 선언
    private static Bank instance;
    public static Bank getInstance() {
        if (instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    // -> 하나의 계좌만 찾게 될거같고
    HashMap<String, Account> number = new HashMap<>();
    // -> 사용자 하나가 여러개 계좌 만들수 있을수 있으니깐 여러개 반환될 가능성이 높아요
    HashMap<String, List<Account>> user = new HashMap<>();

    Scanner sc = new Scanner(System.in);

    // 생성자
    // 어카운트의 리스트 동적 배열 할당
    // 생성될 때, csv에 저장된 내용들을 한번에 불러와서 어카운트에 저장해준다.
    public Bank() {
        accounts = new ArrayList<>();
        io = new IOmanager();

        // 계좌 정보를 저장하는 리스트 [계좌주, 계좌번호, 잔고, 은행명]
        List<List<String>> accountLists = new ArrayList<>();
        accountLists = io.readCSV(ACCOUNTPATH);

        for (int i = 1; i < accountLists.size(); i++) {
            String user = accountLists.get(i).get(0);
            String accountNumber = accountLists.get(i).get(1);
            int balance = Integer.valueOf(accountLists.get(i).get(2));
            String bankName = accountLists.get(i).get(3);
            String trade = accountLists.get(i).get(4);
            Account account = new Account(user, accountNumber, balance, bankName, trade);

            accounts.add(account);
        }

        // 정상적으로 작동하는지 확인하기 위한 콘솔 코드
        for (int i = 0; i < this.accounts.size(); i++) {

            String user = accounts.get(i).getUser();
            String accountNumber = accounts.get(i).getAccountNumber();
            int balance = accounts.get(i).getBalance();
            String bankName = accounts.get(i).getBankName();

            System.out.println("계좌주(" + user + ")" +
                    "  계좌번호(" + accountNumber + ")" +
                    "  잔고(" + balance + ")" +
                    "  은행명(" + bankName + ")");
        }
    }

    // 입력받은 값이 12~14자리 인지 확인
    // 유효성검사 메소드
    public boolean checkAccount(String number) {
        String tmp = number.replaceAll("[- .]","");    //1)-, , . 을 ""으로 변환
        Pattern pattern = Pattern.compile("^\\d{12,14}$");       //2)문자, 특수문자가 입력될 경우 -> 계좌번호가 올바르지 않습니다.
        Matcher matcher = pattern.matcher(tmp);
        if (matcher.matches()) {
            System.out.println("계좌번호가 확인 되었습니다.");
            return true;
        } else {
            System.out.println("계좌번호가 올바르지 않습니다." + number);
            return false;
        }
    }

    // 계좌 등록하기
    public void register() {
        // 예금주, 계좌번호, 잔고, 은행명을 기입한다.
        String user = "";
        String account = "";
        int balance = 0;
        String bankName = "";
        String trade = "";

        System.out.println("새로운 계좌를 등록합니다.");
        System.out.print("계좌주 : ");
        user = sc.nextLine();

        while (true) {
            System.out.print("계좌번호 : ");
            account = sc.nextLine();
            boolean isCorrect = checkAccount(account);
            // 정규표현식 조사할 것 만약 아니면 올바른 입력값이 들어오기전까지 반복

            //입력받은 값에서 숫자를 제외한 모든 문자열을 공백처리
            if (isCorrect) {
                break;
            }
        }

        while (true) {
            System.out.print("현재 잔고 : ");
            try {
                balance = Integer.valueOf(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("숫자만 입력해 주세요.");
            }
        }

        System.out.print("은행명 : ");
        bankName = sc.nextLine();
        System.out.println("정상적으로 처리 되었습니다.");

        // 거래내역 파일 생성후, 거래내역 파일의 주소를 저장해서 넣어준다.
        trade = io.createCSV(Account.TRADEPATH, accounts.size() + 1);
        Account newAccount = new Account(user, account, balance, bankName, trade);

        // 현재 계좌 리스트에 새로운 계좌 정보를 추가해준다
        accounts.add(newAccount);
        String temp[] = {user, account, String.valueOf(balance), bankName, trade};
        List<String> accountInfo = Arrays.asList(temp);

        // 해당 계좌를 csv 파일에 입력해주기
        io.writeCSV(ACCOUNTPATH, accountInfo, true);
    }

    

// 계좌 관리하기 (수정 or 삭제)
    public void manage(int i) {
        if (i == 1){
            modifyAccount();
        }
        else if (i == 2) {
            deleteAccount();
        }
    }

    private void modifyAccount() {
        int index = UserInterface.STANDARDVALUE;

        // 초기값에 해당하면 계속 루프시켜줌
        while (index == UserInterface.STANDARDVALUE) {

            searchAll();

            System.out.println("바꿀 계좌를 선택해주세요");
            
            index = UserInterface.checkInputInteger();

            if (index < 1 || index > accounts.size()) {
                index = UserInterface.STANDARDVALUE;
            }
        }


        // 수정되는 항목? 일단 사용자, 계좌번호만
        System.out.println("바꿀 사용자: ");
        String user = sc.nextLine();

        boolean isQuit = false;
        String accountNumber = "";
        while (!isQuit) {
            System.out.println("바꿀 계좌번호: ");
            accountNumber = sc.nextLine();
            isQuit = checkAccount(accountNumber);
        }

        accounts.get(index -1).setUser(user);
        String preAccountNumber = accounts.get(index -1).getAccountNumber();
        accounts.get(index -1).setAccountNumber(accountNumber);

        String temp[] = toArray(accounts.get(index-1));

        io.rewriteCSV(ACCOUNTPATH, preAccountNumber, Arrays.asList(temp));
    }

    private void deleteAccount() {
        int index = UserInterface.STANDARDVALUE;

        while (index == UserInterface.STANDARDVALUE) {
            searchAll(); //등록된 계좌 목록 조회하기.

            System.out.println("등록된 계좌를 삭제합니다.");
            System.out.println("삭제할 계좌목록 : ");
            index = UserInterface.checkInputInteger();


            if (index < 1 || index > accounts.size()) {
                System.out.println("잘못된 입력입니다.");
                continue;
            }
            System.out.println("계좌삭제 완료.");
            break;
        }

        Account tempaccount  = accounts.get(index - 1);
        accounts.remove(index - 1);      //0보다 작거나 계좌리스트인덱스보다 큰 수 입력할 경우 잘못된입력 출력하고싶음

        for (int i=-1; i<accounts.size(); i++) {
            if (i == -1) {
                String temp[] = {"예금주","계좌번호","잔고","은행명","거래내역"};
                io.writeCSV(ACCOUNTPATH, Arrays.asList(temp) , false);
                continue;
            }
            String temp[] = toArray(accounts.get(i));
            io.writeCSV(ACCOUNTPATH, Arrays.asList(temp) , true);
            io.deleteCSV(tempaccount.getFileName());
        }
    }

    public String[] toArray(Account account) {
        String temp[] = { account.getUser(),
            account.getAccountNumber(),
            String.valueOf(account.getBalance()),
            account.getBankName(),
            account.getFileName() };

        return temp;
    }
    
    // 계좌 검색하기
    public Account search() {
        int input = UserInterface.STANDARDVALUE;
        String checkNumber;             // 입력 받을 계좌번호
        String name;

        while (input == UserInterface.STANDARDVALUE) {
            System.out.println("계좌를 찾을 방법을 선택하세요.");
            System.out.println("1. 이름");
            System.out.println("2. 계좌번호");

            input = UserInterface.checkInputInteger();
        }

        // 동명이인 또는 한사람이 여러개의 계좌를 가지고있을 경우는?
        if (input == 1) {
            while (true) {
                System.out.println("조회하실 이름을 입력해주세요: ");
                name = sc.nextLine();

                for (int i = 0; i < this.accounts.size(); i++) {
                    Account account = accounts.get(i);
                    if (account.getUser().equals(name)) {
                        System.out.println("계좌주(" + account.getUser() + ")" +
                                "  계좌번호(" + account.getAccountNumber() + ")" +
                                "  잔고(" + account.getBalance() + ")" +
                                "  은행명(" + account.getBankName() + ")");
                        return account;
                    }
                }
                
                System.out.println("해당되는 계좌번호가 존재하지 않습니다.");
                break;
            }
        }
        else if (input == 2){
            while (true) {
                System.out.println("조회하실 계좌번호를 입력해주세요: ");
                checkNumber = sc.nextLine();
                
                // 계좌 정규표현식으로 포멧이 맞는지 확인
                boolean isCorrect = checkAccount(checkNumber);

                if (isCorrect == false) {
                    System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                    continue;
                }

                for (int i = 0; i < this.accounts.size(); i++) {
                    Account account = accounts.get(i);
                    if (account.getAccountNumber().equals(checkNumber)) {
                        System.out.println("계좌주(" + account.getUser() + ")" +
                                "  계좌번호(" + account.getAccountNumber() + ")" +
                                "  잔고(" + account.getBalance() + ")" +
                                "  은행명(" + account.getBankName() + ")");
                        return account;
                    }
                }
                
                System.out.println("해당되는 계좌번호가 존재하지 않습니다.");
                break;
            }
    }
        return null;
    }


    // 계좌 검색하기
    public void searchAll() {
        System.out.println("---------");
        System.out.println("계좌목록");
        System.out.println("---------");
        for (int i = 0; i < this.accounts.size(); i++) {
            String user = accounts.get(i).getUser();
            String accountNumber = accounts.get(i).getAccountNumber();
            int balance = accounts.get(i).getBalance();
            String bankName = accounts.get(i).getBankName();

            System.out.println("["+ (i + 1) +"] "+"계좌주(" + user + ")" +
                    "  계좌번호(" + accountNumber + ")" +
                    "  잔고(" + balance + ")" +
                    "  은행명(" + bankName + ")");
        }

    }


    public void notifyAccountInfoChange(String accountNumber, List<String> account) {
        // key 1 = 어카운트로 탐색(명시할것)
        io.rewriteCSV(ACCOUNTPATH, accountNumber, account);
        
    }

}
