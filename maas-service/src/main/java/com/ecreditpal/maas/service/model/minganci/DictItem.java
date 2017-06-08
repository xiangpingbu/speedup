package com.ecreditpal.maas.service.model.minganci;

/**
 * Created by xibu on 11/2/16.
 */
public class DictItem {
    String word;
    String name;
    int level;

    public DictItem(String _word, String _name, int _level){
        setName(_name);
        setWord(_word);
        setLevel(_level);

    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int hashCode() {
        return getWord().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DictItem) && (((DictItem)obj).getWord()).equals(this.getWord());
    }

}
