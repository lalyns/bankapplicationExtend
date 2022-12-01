package Main;

import com.Account.Account;
import com.Bank.Bank;

import java.util.Scanner;
import java.util.Arrays;

public class UserInterface {

    // 상수
    public static final int STANDARDVALUE = -1;

    // enum 타입
    enum MenuType {
        BANK,
        ACCOUNT
    }

    enum BankMenu {
        REGISTER    (1),
        MANAGE      (2),
        SEARCH      (3),
        SEARCHALL   (4),
        QUIT        (5);

        private final int label;

        BankMenu(int label) {
            this.label = label;
        }

        public int label() {
            return label;
        }

        public static BankMenu valueOfLabel(int label) {
            return Arrays.stream(values())
                        .filter(value -> value.label == label)
                        .findAny()
                        .orElse(null);
        }
    }

    enum AccountMenu {
        DEPOSIT     (1),
        WITHDRAW    (2),
        SEARCH      (3),
        VEIWTRADES  (4),
        RETURN      (5);
        
        private final int label;

        AccountMenu(int label) {
            this.label = label;
        }

        public int label() {
            return label;
        }

        public static AccountMenu valueOfLabel(int label) {
            return Arrays.stream(values())
                        .filter(value -> value.label == label)
                        .findAny()
                        .orElse(null);
        }
    }

    // 멤버변수
    private Scanner sc;
    private MenuType curType;
    private Bank bank;
    private Account curAccount;
    private boolean isQuit = false;

    // 싱글턴 선언
    private static UserInterface instance;
    public static UserInterface getInstance() {
        if(instance == null) {
            instance = new UserInterface();
        }

        return instance;
    }

    // 게터 및 세터
    public void setCurType(MenuType curType) { this.curType = curType; }

    // 생성자
    public UserInterface() {
        sc = new Scanner(System.in);
        curType = MenuType.BANK;
        bank = Bank.getInstance();
    }


    // 메소드
    public void run() {   
        int sellection = STANDARDVALUE;
        while(!isQuit) {
            // 현재 메뉴를 출력하는 창
            switch (curType) {
                case BANK :
                    bankMenuPrinter();
                    break;
                case ACCOUNT :
                    accountMenuPrinter();
                    break;
                default :
                    System.out.println("잘못된 접근입니다.");
            }

            sellection = UserInterface.checkInputInteger();
            if (sellection == STANDARDVALUE) continue;
            
            if (sellection < 1 && sellection > 5) {
                System.out.println("존재하지 않는 메뉴 입니다.");
                continue;
            }

            if (curType == MenuType.BANK) {
                bankMenu(sellection);
                continue;
            }

            if (curType == MenuType.ACCOUNT) {
                accountMenu(sellection);
                continue;
            }
        }
    }

    // 입력값이 정수인지 판별해주는 메소드
    public static int checkInputInteger() {
        int input = UserInterface.STANDARDVALUE;
        try {
            input = Integer.valueOf(UserInterface.getInstance().sc.nextLine());
        } catch (Exception e) {
            input = UserInterface.STANDARDVALUE;
            System.out.println("잘못된 입력입니다. 다시입력해주세요.");
        }
        return input;
    }

    // 은행 메뉴 선택시 해당 메뉴를 수행하는 메소드
    private void bankMenu(int sellection) {
        BankMenu menu = BankMenu.valueOfLabel(sellection);
        switch (menu) {
            case REGISTER :
                bank.register();
                break;

            case MANAGE :
                accountManagePrinter();
                int tempSelection = checkInputInteger();

                if (tempSelection == 1 || tempSelection == 2) {
                    bank.manage(tempSelection);
                }
                else {
                    System.out.println("잘못된 입력입니다. 처음으로 돌아갑니다.");
                }
                break;

            case SEARCH :
                System.out.println("계좌를 찾습니다.");
                curAccount = bank.search();
                if (curAccount != null)
                    setCurType(MenuType.ACCOUNT);
                break;

            case SEARCHALL :
                System.out.println("전체 계좌를 조회합니다.");
                bank.searchAll();
                break;
                
            case QUIT:
                isQuit = true;
                System.out.println("은행 프로그램을 종료합니다.");
                break;

            default :
                System.out.println("잘못된 메뉴입니다.");
        }
    }

    // 계좌 메뉴 선택시 해당 메뉴를 수행하는 메소드
    private void accountMenu(int sellection) {
        AccountMenu menu = AccountMenu.valueOfLabel(sellection);

        int fee = STANDARDVALUE;

        switch (menu) {
            case DEPOSIT:
                System.out.println("이전 잔액 : "+ curAccount.getBalance());
                fee = checkInputInteger();
                curAccount.deposit(fee);
                System.out.println("현재 잔액 : "+ curAccount.getBalance());
                break;
            case WITHDRAW:
                System.out.println("이전 잔액 : "+ curAccount.getBalance());
                fee = checkInputInteger();
                curAccount.withdraw(fee);
                System.out.println("현재 잔액 : "+ curAccount.getBalance());
                break;
            case SEARCH:
                System.out.println("잔고를 확인합니다");
                System.out.println("현재 잔고 : "+ curAccount.getBalance() +"원");
                break;
            case VEIWTRADES:
                System.out.println("거래 내역을 조회합니다");
                curAccount.searchAllTrades();
                break;
            case RETURN:
                System.out.println("은행 메뉴로 돌아갑니다.");
                curAccount = null;
                setCurType(MenuType.BANK);
                break;
            default :
                System.out.println("잘못된 메뉴입니다.");
        }
    }
    
    private void accountManagePrinter() {
        System.out.println("계좌를 수정/제거 합니다.");
        System.out.println("----계좌 관리----");
        System.out.println("1. 수정");
        System.out.println("2. 삭제");
    }

    private void accountMenuPrinter() {
        System.out.println("----계좌 메뉴----");
        System.out.println("1. 입금");
        System.out.println("2. 출금");
        System.out.println("3. 잔고 확인");
        System.out.println("4. 거래 내역 조회");
        System.out.println("5. 은행 메뉴로 돌아가기");
    }

    private void bankMenuPrinter() {
        System.out.println("----은행 어플리케이션----");
        System.out.println("1. 계좌 등록");
        System.out.println("2. 계좌 관리"); 
        System.out.println("3. 계좌 찾기"); // 계좌를 찾을땐 2가지 방법 -> 계좌 찾게되면 어떤 동작? 계좌메뉴로 넘겨준다
        System.out.println("4. 계좌 목록조회");
        System.out.println("5. 종료하기");
    }
}
