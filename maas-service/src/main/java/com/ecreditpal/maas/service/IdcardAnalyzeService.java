package com.ecreditpal.maas.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.model.bean.IdCardInfoBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/5.
 */
@Slf4j
public class IdcardAnalyzeService {
    private Map<Integer, String> regionMap = new HashMap<>();
    public int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    private static IdcardAnalyzeService instance = new IdcardAnalyzeService();

    public static IdcardAnalyzeService getInstance() {
        return instance;
    }

    private IdcardAnalyzeService() {
        String fileName = "region_code.csv";
        //Get file from resources folder
        File file = new File(ConfigurationManager.getConfiguration().getString(fileName));
        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" ");
                Integer regionCode = Integer.valueOf(parts[0]);
                String regionName = parts[1];
                regionMap.put(regionCode, regionName);
            }
            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IdCardInfoBean parseToBean(String id)  {
        if (id.length() != 18 && id.length() != 15) {
            return null;
        }


        IdCardInfoBean idcardInfoBean = new IdCardInfoBean();

        Integer regionCode = Integer.valueOf(id.substring(0, 6));
        if (id.length() == 18) {
            if (!getVerificationCode(id).equals(id.substring(17))) {
                return null;
            }

            String dob = id.substring(6, 14);
            Integer yyyy = Integer.valueOf(dob.substring(0, 4));
            Integer mm = Integer.valueOf(dob.substring(4, 6));
            Integer dd = Integer.valueOf(dob.substring(6, 8));
            Integer sequenceCode = Integer.valueOf(id.substring(14, 17));

            idcardInfoBean.setYear(yyyy);
            idcardInfoBean.setMonth(mm);
            idcardInfoBean.setDay(dd);
            idcardInfoBean.setGender(sequenceCode % 2 == 0 ? "female" : "male");
        } else if (id.length() == 15) {
            String dob = id.substring(6, 12);
            Integer yy = Integer.valueOf(dob.substring(0, 2));
            Integer mm = Integer.valueOf(dob.substring(2, 4));
            Integer dd = Integer.valueOf(dob.substring(4, 6));
            Integer sequenceCode = Integer.valueOf(id.substring(12, 15));

            idcardInfoBean.setYear(yy);
            idcardInfoBean.setMonth(mm);
            idcardInfoBean.setDay(dd);
            idcardInfoBean.setGender(sequenceCode % 2 == 0 ? "female" : "male");
        }
        if (!regionMap.containsKey(regionCode)) {
            regionCode /= 10000;
            regionCode *= 10000;
        }
        idcardInfoBean.setRegion(regionMap.get(regionCode));
        if (idcardInfoBean.getYear() != null) {
            idcardInfoBean.setAge(Calendar.getInstance().get(Calendar.YEAR) - idcardInfoBean.getYear());
        }
        return idcardInfoBean;
    }

    public boolean check(String id) {
        if ((id.length() != 18 && id.length() != 15)) {
            return false;
        }
        if (id.length() == 15 && !StringUtils.isNumeric(id)) {
            return false;
        }
        if (id.length() == 18 && !StringUtils.isNumeric(id.substring(0, 17))) {
            return false;
        }

        JSONObject result = JSON.parseObject(parse(id));
        if (result.getString("verification") != null && result.getString("verification").equals("fail")) {
            return false;
        }
        int year = result.getInteger("year");
        int month = result.getInteger("month");
        int day = result.getInteger("day");
        if (year >= 1000 && (year < 1930 || year > currentYear)) {
            return false;
        }
        if (month < 1 || month > 12) {
            return false;
        }
        int numDays = 0;
        switch (month) {
            case 1: case 3: case 5:
            case 7: case 8: case 10:
            case 12:
                numDays = 31;
                break;
            case 4: case 6:
            case 9: case 11:
                numDays = 30;
                break;
            case 2:
                if (((year % 4 == 0) &&
                        !(year % 100 == 0))
                        || (year % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
            default:
                System.out.println("Invalid month.");
                break;
        }
        if (day > numDays) {
            return false;
        }
        return true;
    }

    public String getVerificationCode(String id) {
        if (id.length() != 18) {
            return null;
        }
        int s = (char2int(id.charAt(0)) + char2int(id.charAt(10))) * 7 +
                (char2int(id.charAt(1)) + char2int(id.charAt(11))) * 9 +
                (char2int(id.charAt(2)) + char2int(id.charAt(12))) * 10 +
                (char2int(id.charAt(3)) + char2int(id.charAt(13))) * 5 +
                (char2int(id.charAt(4)) + char2int(id.charAt(14))) * 8 +
                (char2int(id.charAt(5)) + char2int(id.charAt(15))) * 4 +
                (char2int(id.charAt(6)) + char2int(id.charAt(16))) * 2 +
                char2int(id.charAt(7)) * 1 +
                char2int(id.charAt(8)) * 6 +
                char2int(id.charAt(9)) * 3;
        int y = s % 11;
        String JYM = "10X98765432";
        return JYM.substring(y, (y + 1));
    }

    public String parse(String id) throws JSONException {
        if (id.length() != 18 && id.length() != 15) {
            return null;
        }

        JSONObject result = new JSONObject();
        Integer regionCode = Integer.valueOf(id.substring(0, 6));
        if (id.length() == 18) {
            if (!getVerificationCode(id).equals(id.substring(17))) {
                result.put("verification", "fail");
                return result.toString();
            }

            String dob = id.substring(6, 14);
            Integer yyyy = Integer.valueOf(dob.substring(0, 4));
            Integer mm = Integer.valueOf(dob.substring(4, 6));
            Integer dd = Integer.valueOf(dob.substring(6, 8));
            Integer sequenceCode = Integer.valueOf(id.substring(14, 17));
            result.put("year", yyyy);
            result.put("month", mm);
            result.put("day", dd);
            result.put("gender", sequenceCode % 2 == 0 ? "female" : "male");
        }
        else if (id.length() == 15) {
            String dob = id.substring(6, 12);
            Integer yy = Integer.valueOf(dob.substring(0, 2));
            Integer mm = Integer.valueOf(dob.substring(2, 4));
            Integer dd = Integer.valueOf(dob.substring(4, 6));
            Integer sequenceCode = Integer.valueOf(id.substring(12, 15));
            result.put("year", yy);
            result.put("month", mm);
            result.put("day", dd);
            result.put("gender", sequenceCode % 2 == 0 ? "female" : "male");
        }
        result.put("region", regionMap.get(regionCode));
        return result.toString();
    }

    public int char2int(char c) {
        return c - '0';
    }


    public static void main(String[] args) throws Exception {
        IdcardAnalyzeService analyzer = new IdcardAnalyzeService();
        IdCardInfoBean bean = analyzer.parseToBean("330521198907230018");
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(analyzer.getVerificationCode("330521198907230018"));
//        System.out.println(mapper.writeValueAsString(IdcardAnalyzer.getInstance().parseToBean("440507198908220020")));
    }
}
