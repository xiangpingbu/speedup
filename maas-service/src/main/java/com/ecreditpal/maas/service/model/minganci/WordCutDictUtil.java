package com.ecreditpal.maas.service.model.minganci;

import com.luhuiguo.chinese.ChineseUtils;
import com.luhuiguo.chinese.pinyin.PinyinFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xibu on 10/28/16.
 */
public class WordCutDictUtil {
    //populate all kinds of permutation of sensitive words into word cut dict
    //make sure the word cut will not cut sensitive words

    private static final String MAIN_DICT = "dict.txt";
    private static final String MGC_DICT = "mgc.txt";
    private static final String MGZ_DICT = "mgz.txt";
    private static final String MAIN_DICT_BASE = "dict_base.txt";
    private static final String configDir = "itsrobin"+"src/main/resources";
    private static final String configDirFenCi = configDir + "/minganci/fenCiDict/";
    private static final String configDirUserDict = configDir + "/minganci/userDict/";
    private static final String configDirMinGanCi = configDir + "/minganci/mgcDict/";
    private final static Logger logger = LoggerFactory.getLogger(WordCutDictUtil.class);
    String dictconfigPath = configDir + "/minganci/dictConfig.xml";


    /*
    Level Zero: 1. 简体， 2. 繁体， 3. 拼音，4. 拼音带声调， 5. 缩写, 6.全排列, (remove less than 3 letter only permutation)
    Level One : 1. 简体， 2. 繁体， 3. 拼音，4. 缩写, (5. 拼音带声调, disable) 6.全排列，(remove less than 3 letter only permutation)
    Level Two:  1. 简体， 2. 繁体， 3. 拼音，(4. 拼音带声调, disable)，5. 缩写

    Level Zero: 高敏感词会被拆分成高敏感字,存入文件,为 online regular expression 所用, 高敏感字不会加入分词
    NOTE: 1. 如果敏感词中是英文单词或者拼音，只能是level 2, 不能用全排列， 可转化为汉字， 然后再设置为l1或l0
          2. 全字母缩写,会产生很多overkill 如果输入是英文的话, 很多全字母缩写是很common 的组合, 暂定只有政治敏感词保留全字母缩写,其余情况,少于三个字母的排列将被remove
          3.

     */
    private Set<DictItem> DictLetterBase;
    private Set<DictItem> DictLevelZero;
    private Set<DictItem> DictLevelOne;
    private Set<DictItem> DictLevelTwo;
    private Set<DictItem> total;

    public WordCutDictUtil() {
        DictLetterBase = new HashSet<>();
        DictLevelZero = new HashSet<>();
        DictLevelOne = new HashSet<>();
        DictLevelTwo = new HashSet<>();
        total = new HashSet<>();
        initWordDict();
    }

    public static void main(String[] args) {
        WordCutDictUtil wd = new WordCutDictUtil();
        wd.populate();

    }

    public void populate() {
        try {

            FileWriter fwfc = new FileWriter(configDirFenCi + MAIN_DICT);
            FileWriter fwmgc = new FileWriter(configDirMinGanCi + MGC_DICT);
            FileWriter fwmgz = new FileWriter(configDirMinGanCi + MGZ_DICT);
            BufferedReader br = new BufferedReader(new FileReader(configDirFenCi + MAIN_DICT_BASE));
            String line;

            //add basic words
            while ((line = br.readLine()) != null) {
                fwfc.write(line.trim() + "\n");
            }

            //生成敏感字库

            Set<String> totalLetter = new HashSet<>();

            for (DictItem w : DictLetterBase){
                String wd = w.getWord();
                for(int i=0; i<wd.length();i++) {
                    if(isChinese(wd.charAt(i))) {
                        String simple = ChineseUtils.toSimplified(String.valueOf(wd.charAt(i))).replaceAll("\\s*", "").trim() ;
                        String tradition =ChineseUtils.toTraditional(simple).replaceAll("\\s*", "").trim() ;

                        if(simple != null && simple.length()>0)
                            totalLetter.add(simple);
                        if(tradition != null && tradition.length()>0)
                            totalLetter.add(tradition);
                    }
                }
            }

            for(String l: totalLetter){
                fwmgz.write(l + "\n");
            }
            fwmgz.close();



            //populate for 分词词典 and 敏感词词典
            // 从0 到2 load, 高 risk 级别先进入, 不会被低risk over write
            // level zero
            for (DictItem w : DictLevelZero) {
                //permutation population only for short word: length 1 to 6
                if (w.getWord().length() > 0 && w.getWord().length() < 7) {
                    Set<String> permutation = genPermutationHigh(w.getWord());
                    for (String per : permutation) {
                        total.add(new DictItem(per,w.getName(),w.getLevel()));
                    }
                } else {
                    //only put original word into dict
                    Set<String> simple = genSimple(w.getWord());
                    for (String s : simple) {
                        total.add(new DictItem(s,w.getName(),w.getLevel()));
                    }

                }

            }

            //level one
            for (DictItem w : DictLevelOne) {
                //permutation population only for short word: length 1 to 6
                if (w.getWord().length() > 0 && w.getWord().length() < 7) {
                    Set<String> permutation = genPermutationLow(w.getWord());
                    for (String per : permutation) {
                        total.add(new DictItem(per,w.getName(),w.getLevel()));
                    }
                } else {
                    //only put original word into dict
                    Set<String> simple = genSimple(w.getWord());
                    for (String s : simple) {
                        total.add(new DictItem(s,w.getName(),w.getLevel()));
                    }

                }
            }

            //level two
            for (DictItem w : DictLevelTwo) {
                //only put original word into dict
                Set<String> simple = genSimple(w.getWord());
                for (String s : simple) {
                    total.add(new DictItem(s,w.getName(),w.getLevel()));
                }
            }

            for(DictItem it:total){
                fwfc.write(it.getWord() + " 100 n \n");
                fwmgc.write(it.getWord() + "," + it.getLevel() + "," + it.getName() + "\n");
            }

            br.close();
            fwfc.close();
            fwmgc.close();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("error in word cut dict population");
        }

    }

    public HashSet<String> genSimple(String word) {
        HashSet<String> ret = new HashSet<>();
        String simple_style = ChineseUtils.toSimplified(word);
        String tradition_style = ChineseUtils.toTraditional(word);
        String pinyin_style = ChineseUtils.toPinyin(word, PinyinFormat.TONELESS_PINYIN_FORMAT);
        //String pinyinTone_style = ChineseUtils.toPinyin(word, PinyinFormat.UNICODE_PINYIN_FORMAT);
        //String abbr_style = ChineseUtils.toPinyin(word, PinyinFormat.ABBR_PINYIN_FORMAT);

        ret.add(simple_style.equals("") ? word : simple_style.replaceAll("\\s*", "").trim());
        ret.add(tradition_style.equals("") ? word : tradition_style.replaceAll("\\s*", "").trim());
        ret.add(pinyin_style.equals("") ? word : pinyin_style.replaceAll("\\s*", "").trim());
        //ret.add(pinyinTone_style.equals("")?word:pinyinTone_style);
        //ret.add(abbr_style.equals("") ? word : abbr_style);

        return ret;
    }

    public HashSet<String> genPermutationLow(String word) {
        ArrayList<String> ret = new ArrayList<>();
        HashSet<String> finalRet = new HashSet<>();
        HashSet<String> tmpRet = new HashSet<>();

        ArrayList<String> simplify = new ArrayList<>();
        ArrayList<String> traditional = new ArrayList<>();
        ArrayList<String> pinyin = new ArrayList<>();
        ArrayList<String> abbr = new ArrayList<>();
        //ArrayList<String> pinyinTone = new ArrayList<>();

        ArrayList<ArrayList<String>> total = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, i + 1);
            String simple_style = ChineseUtils.toSimplified(ch);
            String tradition_style = ChineseUtils.toTraditional(ch);
            String pinyin_style = ChineseUtils.toPinyin(ch, PinyinFormat.TONELESS_PINYIN_FORMAT);
            //String pinyinTone_style = ChineseUtils.toPinyin(ch, PinyinFormat.UNICODE_PINYIN_FORMAT);
            String abbr_style = ChineseUtils.toPinyin(ch, PinyinFormat.ABBR_PINYIN_FORMAT);

            simplify.add(simple_style.equals("") ? ch : simple_style);
            traditional.add(tradition_style.equals("") ? ch : tradition_style);
            pinyin.add(pinyin_style.equals("") ? ch : pinyin_style);
            abbr.add(abbr_style.equals("") ? ch : abbr_style);
            //pinyinTone.add(pinyinTone_style.equals("")?ch:pinyinTone_style);
        }

        total.add(simplify);
        total.add(traditional);
        total.add(pinyin);
        total.add(abbr);
        //total.add(pinyinTone);

        ret.add("");

        genPermutationHelper(tmpRet, ret, total, word.length(), 0);
        for (String item : tmpRet) {
            item = item.replaceAll("\\s*", "").trim();
            //keep the original word
            if (item.equals(word) || (!(item.matches("[a-zA-Z]+") && item.length() < 4))) {
                finalRet.add(item);
            }
        }

        return finalRet;
    }

    public HashSet<String> genPermutationHigh(String word) {
        ArrayList<String> ret = new ArrayList<>();
        HashSet<String> finalRet = new HashSet<>();
        HashSet<String> tmpRet = new HashSet<>();

        ArrayList<String> simplify = new ArrayList<>();
        ArrayList<String> traditional = new ArrayList<>();
        ArrayList<String> pinyin = new ArrayList<>();
        ArrayList<String> abbr = new ArrayList<>();
        ArrayList<String> pinyinTone = new ArrayList<>();

        ArrayList<ArrayList<String>> total = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            String ch = word.substring(i, i + 1);
            String simple_style = ChineseUtils.toSimplified(ch);
            String tradition_style = ChineseUtils.toTraditional(ch);
            String pinyin_style = ChineseUtils.toPinyin(ch, PinyinFormat.TONELESS_PINYIN_FORMAT);
            String pinyinTone_style = ChineseUtils.toPinyin(ch, PinyinFormat.UNICODE_PINYIN_FORMAT);
            String abbr_style = ChineseUtils.toPinyin(ch, PinyinFormat.ABBR_PINYIN_FORMAT);

            simplify.add(simple_style.equals("") ? ch : simple_style);
            traditional.add(tradition_style.equals("") ? ch : tradition_style);
            pinyin.add(pinyin_style.equals("") ? ch : pinyin_style);
            abbr.add(abbr_style.equals("") ? ch : abbr_style);
            pinyinTone.add(pinyinTone_style.equals("") ? ch : pinyinTone_style);
        }

        total.add(simplify);
        total.add(traditional);
        total.add(pinyin);
        total.add(abbr);
        total.add(pinyinTone);

        ret.add("");

        genPermutationHelper(tmpRet, ret, total, word.length(), 0);
        for (String item : tmpRet) {
            item = item.replaceAll("\\s*", "").trim();
            if (item.equals(word) || (!(item.matches("[a-zA-Z]+") && item.length() < 3))) {
                finalRet.add(item);
            }
        }

        return finalRet;
    }

    public void genPermutationHelper(HashSet<String> finalRet, ArrayList<String> ret, ArrayList<ArrayList<String>> total, int len, int index) {
        if (len == index) {
            for (String item : ret) {
                finalRet.add(item);
            }
            return;
        }

        ArrayList<String> localRet = new ArrayList<String>();

        for (int i = 0; i < total.size(); i++) {
            String st = total.get(i).get(index);
            for (int j = 0; j < ret.size(); j++) {
                localRet.add(ret.get(j) + st);
            }
        }
        genPermutationHelper(finalRet, localRet, total, len, index + 1);
    }

    public void initWordDict() {
        String dictPathPrefix = configDirUserDict;
        try {
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(dictconfigPath));
            NodeList nList = doc.getElementsByTagName("Dictionary");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                if (element.getElementsByTagName("Level").item(0).getTextContent().equals("-1")){
                    String dictPath = dictPathPrefix + element.getElementsByTagName("Path").item(0).getTextContent().trim();
                    String name = element.getElementsByTagName("Name").item(0).getTextContent().trim();
                    loadDict(dictPath, -1, name);
                }else if (element.getElementsByTagName("Level").item(0).getTextContent().equals("0")) {
                    String dictPath = dictPathPrefix + element.getElementsByTagName("Path").item(0).getTextContent().trim();
                    String name = element.getElementsByTagName("Name").item(0).getTextContent().trim();
                    loadDict(dictPath, 0, name);

                } else if (element.getElementsByTagName("Level").item(0).getTextContent().equals("1")) {
                    String dictPath = dictPathPrefix + element.getElementsByTagName("Path").item(0).getTextContent().trim();
                    String name = element.getElementsByTagName("Name").item(0).getTextContent().trim();
                    loadDict(dictPath, 1, name);
                } else {
                    String dictPath = dictPathPrefix + element.getElementsByTagName("Path").item(0).getTextContent().trim();
                    String name = element.getElementsByTagName("Name").item(0).getTextContent().trim();
                    loadDict(dictPath, 2, name);
                }
            }
            logger.info("Level Zero Sensitive Word Dict size: " + DictLevelZero.size());
            logger.info("Level One Sensitive Word Dict size: " + DictLevelOne.size());
            logger.info("Level Two Sensitive Word Dict size: " + DictLevelTwo.size());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("error in parsing dict configuration file ");
        }
    }

    public static boolean isChinese(char a) {
        int v = (int)a;
        return (v >=19968 && v <= 171941);
    }

    public void loadDict(String path, int level, String name) {
        Set<DictItem> dict;
        if (level == -1){
            dict = DictLetterBase;
        }else if (level == 0) {
            dict = DictLevelZero;
        } else if (level == 1) {
            dict = DictLevelOne;
        } else {
            dict = DictLevelTwo;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                dict.add(new DictItem(line.trim(), name, level));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("error in sensitive words dict loading ");
        }
    }
}
