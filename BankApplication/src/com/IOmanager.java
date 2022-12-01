package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.Account.Account;


// csv파일 파싱을 위한 클래스
public class IOmanager {
    List<List<String>> ret = new ArrayList<>();
    BufferedReader br = null;
    
    public IOmanager() {
    }

    // 새로운 계좌 정보가 추가될 경우, 거래내역을 담을 csv파일 생성
    public String createCSV(String path, int index) {

        String fileName = "trade" + index + ".csv";
        File csv = new File(path + fileName);

        FileOutputStream output = null;
        BufferedWriter bw = null;
        
        try {
            output = new FileOutputStream(csv, true);
            bw = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
            String aData = "일자,시간,I/O,금액,은행명";
            bw.write(aData);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if (bw!=null) {
                    bw.flush();
                    bw.close();
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 

        return fileName;
    }

    // csv 파일 파싱
    public List<List<String>> readCSV(String path) {
        try {
            ret.clear();
            // 입력 스트림 생성
            FileInputStream input = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(input, "UTF-8");
            br = new BufferedReader(reader);
            String line = "";

            while((line = br.readLine()) != null) {
                // CSV 1행을 저장하는 리스트
                List<String> tmpList = new ArrayList<>();
                String array[] = line.split(",");
                // 배열에서 리스트 반환
                tmpList = Arrays.asList(array);
                ret.add(tmpList);
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(br != null) {
                    br.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return ret;
    }

    // 새로운 정보 쓰기(계좌 정보 생성 및 새로운 거래내역 추가)
    public void writeCSV(String path, List<String> list, boolean append) {

        File csv = new File(path);
        FileOutputStream output = null;
        BufferedWriter bw = null;

        try {
            // 출력 스트림 생성
            output = new FileOutputStream(csv, append);
            bw = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
            String aData = "";
            for(int i=0; i<list.size(); i++){
                aData += list.get(i);
                aData += ",";
            }
            bw.write(aData);
            bw.newLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw!=null) {
                    bw.flush();
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 계좌 정보가 변경될 경우 해당되는 정보 변경을 위한 매소드
    // 사용자, 계좌번호, 등등 변경되는 값이 여러개일경우는 함수 콜을 여러번할 것
    public void rewriteCSV(String path, String value, List<String> rewrite) {
        List<List<String>> fileText = readCSV(path);
        List<List<String>> newFileText = new ArrayList<>();

        for (int i=0; i<fileText.size(); i++) {
            if(fileText.get(i).get(1).equals(value)) {
                newFileText.add(rewrite);
            }
            else {
                newFileText.add(fileText.get(i));
            }
        }

        for (int i=0; i<newFileText.size();i++){
            if(i == 0) {
                writeCSV(path, newFileText.get(i), false);
                continue;
            }
            writeCSV(path, newFileText.get(i), true);
        }
    }

    public void deleteCSV(String fileName) {
        File file = new File(Account.TRADEPATH + fileName);

        if (file.exists()) {
            if(file.delete()) {
    			System.out.println("파일삭제 성공");
            }
            else {
    			System.out.println("파일삭제 실패");
            }
        }
        else {
    		System.out.println("파일이 존재하지 않습니다.");
        }
    }
}
