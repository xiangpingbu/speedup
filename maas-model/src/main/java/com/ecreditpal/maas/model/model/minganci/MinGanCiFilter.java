package com.ecreditpal.maas.model.model.minganci;


import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import com.ecreditpal.maas.common.schedule.Register;
import com.ecreditpal.maas.model.model.minganci.wordcut.JiebaSegmenter;
import com.ecreditpal.maas.model.model.minganci.wordcut.SegToken;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.automaton.CharacterRunAutomaton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by xibu on 10/26/16.
 */

public class MinGanCiFilter implements Register {

    private final static Logger logger = LoggerFactory.getLogger(MinGanCiFilter.class);
    private static final String MGC_DICT = ConfigurationManager.getConfiguration().getString("mgc.txt");
    private static final String MGZ_DICT = ConfigurationManager.getConfiguration().getString("mgz.txt");
    private static MinGanCiFilter instance = null;
    private String post;
    private Set<String> mgzDict;
    private HashMap<String, DictItem> infoDict;
    private long mgcModifyTime;
    private long mgzModifyTime;

    public MinGanCiFilter() {
        mgzDict = new HashSet<String>();
        infoDict = new HashMap<String, DictItem>();
        loadDict();
    }

    public static MinGanCiFilter getInstance() {
        if (instance == null) {
            instance = new MinGanCiFilter();
        }
        return instance;
    }

    public static String StringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字和汉字
        // 清除掉所有特殊字符,包括空格
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s*]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim().replace("和谐", "").replace("河蟹", "").replace("百度", "").replace("你懂的", "");
    }


    public Set<String> run(String _post) {
        this.post = StringFilter(_post);
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> words = segmenter.process(post, JiebaSegmenter.SegMode.INDEX);
        List<String> wordList = new ArrayList<>();
        for (SegToken w : words) {
            wordList.add(w.word);
        }

        //System.out.println(wordList);
        Set<String> SensitiveWords = new HashSet<>();

        for (String postWord : wordList) {
            if (infoDict.containsKey(postWord)) {
                DictItem target = infoDict.get(postWord);
                SensitiveWords.add(target.getName() + "|" + target.getLevel() + "|" + postWord);
            }
        }

        //只有当没有发现敏感词的时候,才启动正则检查. 忽略英文查找,单个英文字母无意义
        //注意,敏感字字典须严格要求,否则消耗资源过大
        //为了节省资源,找到一种敏感词就返回,不会穷举所有可能匹配
        if (SensitiveWords.size() == 0) {
            DictItem REret = REcheck(post);
            if (REret != null) {
                SensitiveWords.add(REret.getName() + "|" + REret.getLevel() + "|" + REret.getWord());
            }
        }
        return SensitiveWords;
    }

    //highly ad-hoc process to avoid overkilling
    public DictItem REcheck(String post) {
        for (int i = 0; i < post.length(); i++) {
            char letter = post.charAt(i);
            if (isChinese(letter) && mgzDict.contains(String.valueOf(letter))) {
                //1个字符, 例如 共产*
                int j = 1;
                if (i + j < post.length() && isChinese(post.charAt(i + j)) && mgzDict.contains(post.substring(i + j, i + j + 1))) {
                    CharacterRunAutomaton p1 = new CharacterRunAutomaton(WildcardQuery.toAutomaton(new Term("", post.substring(i, i + 2) + "*")));
                    CharacterRunAutomaton p2 = new CharacterRunAutomaton(WildcardQuery.toAutomaton(new Term("", "*" + post.substring(i, i + 2))));
                    for (DictItem item : infoDict.values()) {
                        if (p1.run(item.getWord())) {
                            return new DictItem(post.substring(i, i + 2), item.getName(), item.getLevel());
                        } else if (p2.run(item.getWord())) {
                            return new DictItem(post.substring(i, i + 2), item.getName(), item.getLevel());
                        }
                    }
                }

                for (j = 2; j <= 3; j++) {
                    if (i + j < post.length() && isChinese(post.charAt(i + j)) && mgzDict.contains(post.substring(i + j, i + j + 1))) {
                        CharacterRunAutomaton p3 = new CharacterRunAutomaton(WildcardQuery.toAutomaton(new Term("", post.charAt(i) + "*" + post.charAt(i + j))));
                        for (DictItem item : infoDict.values()) {
                            if (p3.run(item.getWord())) {
                                return new DictItem(post.substring(i, i + j + 1), item.getName(), item.getLevel());
                            } else if (p3.run(item.getWord())) {
                                return new DictItem(post.substring(i, i + j + 1), item.getName(), item.getLevel());
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void loadDict() {
        long total = Runtime.getRuntime().totalMemory(); // byte
        long m1 = Runtime.getRuntime().freeMemory();
        loadMgc(new File( MGC_DICT));
        loadMgz(new File( MGZ_DICT));
        logger.info("before:{}", total - m1);

        long total1 = Runtime.getRuntime().totalMemory();
        long m2 = Runtime.getRuntime().freeMemory();
        System.out.println("after:" + (total1 - m2));
    }

    public void loadMgc(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String[] cnt = line.split(",");
                String word = cnt[0];
                String level = cnt[1];
                String name = cnt[2];

                DictItem t = new DictItem(word, name, Integer.parseInt(level.trim()));
                //mgcDict.add(word);
                infoDict.put(word, t);
            }
            mgcModifyTime = file.lastModified();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("error in sensitive words dict loading ");
        }
    }

    public void loadMgz(File file) {
        file = new File( MGZ_DICT);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                String word = line.trim().replace("\\n", "");
                mgzDict.add(word);
            }
            mgzModifyTime = file.lastModified();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("error in sensitive letter dict loading ");
        }
    }


    /**
     * 输入的字符是否是汉字
     *
     * @param a char
     * @return boolean
     */
    public static boolean isChinese(char a) {
        int v = (int) a;
        return (v >= 19968 && v <= 171941);
    }


    public static void main(String[] args) {

        MinGanCiFilter mgc = new MinGanCiFilter();
        try {
            FileWriter ret = new FileWriter("src/main/resources/minganci/test/" + "result.txt");
            BufferedReader br = new BufferedReader(new FileReader("src/main/resources/minganci/test/" + "test.txt"));
            String line;
            System.out.println();
            while ((line = br.readLine()) != null) {

                ret.write("原帖: " + line.trim() + "\n");
                long s = System.currentTimeMillis();
                ret.write("敏感词为: " + mgc.run(line.trim()) + "\n");
                ret.write("用时(ms): " + (System.currentTimeMillis() - s) + "\n");
                ret.write("\n");

            }
            ret.close();
            br.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void work() {
        File file = new File( MGC_DICT);
        if (file.lastModified() > mgcModifyTime) {
            loadMgc(file);
        }

        file = new File( MGZ_DICT);
        if (file.lastModified() > mgzModifyTime) {
            loadMgz(file);
        }
    }

    public String toString() {
        return "MinGanCiFilter";
    }
}
