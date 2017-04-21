package com.ecreditpal.maas.common.offline;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @CreateTime 2017/4/14.
 */
public class XybJsonConvert {
    public static List<List<String>> transfer4_0() throws FileNotFoundException {
        JsonParser parser = new JsonParser();
//        String path = "/Users/lifeng/Desktop/xinyongbao/4.0/\\263\\302\\210\\220\\263\\366_231084197402014011_15045332240.json";
        String folder = "/Users/lifeng/Desktop/xinyongbao/4.0/";
        File file = new File(folder);
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        List<List<String>> rowList = Lists.newArrayList();
        int i=0;
        for (File f : files) {
            System.out.println(++i+"个文件");
            Reader reader = new FileReader(f);
            JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
            Map<String, String> map = Maps.newLinkedHashMap();
            List<String> list = Lists.newArrayList();
            rowList.add(list);
            JsonObject person = jsonObject.getAsJsonObject("person");
            setRootprop(list, person, "real_name");
            setRootprop(list, person, "id_card_num");
            setRootprop(list, person, "gender");
            setRootprop(list, person, "age");
            setRootprop(list, person, "province");
            setRootprop(list, person, "city");
            setRootprop(list, person, "region");
            setRootprop(list, person, "state");


            JsonArray applicationCheck = jsonObject.getAsJsonArray("application_check");
            setRootprop(list, applicationCheck.get(2).getAsJsonObject(), "result", "reliability");
            setRootprop(list, applicationCheck.get(3).getAsJsonObject(), "result", "cid_auth");

            JsonArray behavior_check = jsonObject.getAsJsonArray("behavior_check");
            setRootprop(list, behavior_check.get(0).getAsJsonObject(), "result", "friendster");
            setRootprop(list, behavior_check.get(12).getAsJsonObject(), "result", "credit_apply");
            setRootprop(list, behavior_check.get(14).getAsJsonObject(), "result", "call_aomen");
            setRootprop(list, behavior_check.get(17).getAsJsonObject(), "result", "call_lawyer");
            setRootprop(list, behavior_check.get(18).getAsJsonObject(), "result", "call_court");

        }
        return rowList;
    }


    public static List<List<String>> transfer4_2() throws FileNotFoundException {
        JsonParser parser = new JsonParser();
//        String path = "/Users/lifeng/Desktop/xinyongbao/4.0/\\263\\302\\210\\220\\263\\366_231084197402014011_15045332240.json";
        String folder = "/Users/lifeng/Desktop/xinyongbao/4.2/";
        File file = new File(folder);
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }

        List<List<String>> rowList = Lists.newArrayList();
        int i =0;
        for (File f : files) {
            System.out.println(++i+"个文件完成");
            Reader reader = new FileReader(f);
            JsonObject result = parser.parse(reader).getAsJsonObject();
            List<String> list = Lists.newArrayList();
            rowList.add(list);

           JsonObject checkBlackInfo = result.getAsJsonObject("user_info_check");
            checkBlackInfo = checkBlackInfo .getAsJsonObject("check_black_info");
            setRootprop(list, checkBlackInfo, "phone_gray_score");
            setRootprop(list, checkBlackInfo, "contacts_class1_blacklist_cnt");
            setRootprop(list, checkBlackInfo, "contacts_class2_blacklist_cnt");
            setRootprop(list, checkBlackInfo, "contacts_class1_cnt");
            setRootprop(list, checkBlackInfo, "contacts_router_cnt");
            setRootprop(list, checkBlackInfo, "contacts_router_ratio");

            JsonArray checkLists = result.getAsJsonArray("application_check");
            setRootprop(list, checkLists.get(0).getAsJsonObject().getAsJsonObject("check_points"), "key_value", "user_name");

            JsonObject jsonObject = checkLists.get(1).getAsJsonObject().getAsJsonObject("check_points");
            setRootprop(list, jsonObject, "key_value", "id_card_num");
            setRootprop(list, jsonObject, "gender");
            setRootprop(list, jsonObject, "age");
            setRootprop(list, jsonObject, "province");
            setRootprop(list, jsonObject, "city");
            setRootprop(list, jsonObject, "region");

            jsonObject = checkLists.get(2).getAsJsonObject().getAsJsonObject("check_points");
            setRootprop(list, jsonObject, "reliability"); //实名认证

            //朋友圈
            checkLists = result.getAsJsonArray("behavior_check");
            jsonObject = checkLists.get(0).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "friendster");

            jsonObject = checkLists.get(1).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "phone_used_time");

            jsonObject = checkLists.get(2).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "phone_silent");

            jsonObject = checkLists.get(3).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_each_other");

            jsonObject = checkLists.get(4).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_macao");

            jsonObject = checkLists.get(7).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_lawyer");

            jsonObject = checkLists.get(8).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_court");

            jsonObject = checkLists.get(9).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_night");

            jsonObject = checkLists.get(10).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_loan");

            jsonObject = checkLists.get(11).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_bank");

            jsonObject = checkLists.get(12).getAsJsonObject();
            setRootprop(list, jsonObject, "result", "contact_credit_card");

            JsonArray a  =result.getAsJsonArray("cell_behavior");
            if (a.size() >0) {
                JsonObject cellBehavior = a.get(0).getAsJsonObject();
                JsonArray behaviorList = cellBehavior.getAsJsonArray("behavior");
                JsonObject behavior = behaviorList.get(0).getAsJsonObject();
                setRootprop(list, behavior, "cell_operator");
                setRootprop(list, behavior, "cell_operator_zh");
                setRootprop(list, behavior, "cell_phone_num");
                setRootprop(list, behavior, "cell_loc");

                int callCnt = 0;
                int callOutCnt = 0;
                float callOutTime = 0;
                int callInCnt = 0;
                float callInTime = 0;
                float netFlow = 0;
                int smsCnt = 0;
                float totalAmount = 0;
                int count = 0;
                for (JsonElement element : behaviorList) {
                    count++;
                    JsonObject o = element.getAsJsonObject();
                    callCnt += o.getAsJsonPrimitive("call_cnt").getAsInt();//呼叫次数
                    callOutCnt += o.getAsJsonPrimitive("call_out_cnt").getAsInt();//呼叫次数
                    callOutTime += o.getAsJsonPrimitive("call_out_time").getAsFloat(); //呼出时间
                    callInCnt += o.getAsJsonPrimitive("call_in_cnt").getAsInt(); //被叫次数
                    callInTime += o.getAsJsonPrimitive("call_in_time").getAsFloat();  //被叫时间
                    netFlow += o.getAsJsonPrimitive("net_flow").getAsFloat();
                    smsCnt += o.getAsJsonPrimitive("sms_cnt").getAsInt();
                    totalAmount += o.getAsJsonPrimitive("total_amount").getAsInt();
                }
                if (count == 0) count++;
                list.add(callCnt /count+"");
                list.add(callOutCnt /count+"");
                list.add(callOutTime /count+"");
                list.add(callInCnt /count+"");
                list.add(callInTime /count+"");
                list.add(netFlow /count+"");
                list.add(smsCnt /count+"");
                list.add(totalAmount /count+"");
            } else{
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
                list.add(" ");
            }
        }
        return rowList;
    }


    private static void setRootprop(List<String> list, JsonObject jsonObject, String name) {
        setRootprop(list, jsonObject, name, name);
    }

    private static void setRootprop(List<String> list, JsonObject jsonObject, String name, String specific) {
        String s="";
        try {
         s =jsonObject.get(name).getAsString();}
        catch (UnsupportedOperationException ignored) {

        }
        list.add(s);
    }

    private static void getAverage() {

    }

    public static void main(String[] args) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
//        String path = "/Users/lifeng/Desktop/xinyongbao/4.0/\\263\\302\\210\\220\\263\\366_231084197402014011_15045332240.json";
        String path = "/Users/lifeng/Desktop/xinyongbao/4.2/··_141026198403150014_13753546669.json";
        FileReader reader = new FileReader(path);

        JsonObject obj = parser.parse(reader).getAsJsonObject();
        System.out.println(obj);

    }


}
