/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sarf_package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bakee
 */
public class Utiles {

    ArrayList results;
    static PreparedStatement ps;
    static Connection conn;
    static ResultSet rs;

    public long performance() {
//        return (LocalTime.now().getSecond() + LocalTime.now().getMinute() * 60);
        return System.nanoTime() / 10000000;
    }

    public static boolean openConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:derby:" + "sarfDB_10");
        } catch (SQLException ex) {
            pl("connection open error:\n" + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean commit() {
        try {
            conn.commit();
        } catch (SQLException ex) {
            pl("commit error:\n" + ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean closeConnection() {
        try {
            conn.close();
        } catch (SQLException ex) {
            pl("connection close error:\n" + ex.getMessage());
            return false;
        }
        return true;
    }

    public static void p(Object o) {
        System.out.print(o);
    }

    public static void pl(Object o) {
        System.out.println(o);
    }

    public static void pt(Object o) {
        System.out.print(o + "\t");
    }

    public static String deDiacritic(String diac) {
        ArrayList<Character> marks = new ArrayList();
        marks.add('َ');//فتحة
        marks.add('ِ');//كسرة
        marks.add('ُ');//ضمة
        marks.add('ْ');//سكون
        marks.add('ّ');//شدة
        marks.add('ٌ');//تنوين مضموم
        marks.add('ً');//تنوين مفتوح
        marks.add('ٍ');//تنوين مكسور

        char[] chars = diac.toCharArray();
        String res = "";
        for (Character ch : chars) {
            if (!marks.contains(ch)) {
                res = res + ch;
            }
        }
        return res;
    }

    public static String pronouns(int index) {
        String[] pronouns = new String[]{"1ms", "1mp", "2ms", "2fs", "2md", "2mp", "2fp", "3ms", "3fs", "3md", "3md", "3mp", "3fp"};
        return pronouns[index];
    }

    List<String> getConjuagations(String root) {
        List<String> conjs = new ArrayList();
        String[] cjs = root.split("C\\d+\\(");
//        pl(cjs.length);
        conjs = Arrays.asList(cjs);
        return conjs;
    }

    public List<String> getConjuagations2(String root) {
        //"C\\d+\\(.*?[()]\\)";
        List<String> conjs = new ArrayList();
        int re = 1;
        String regex = "C\\d+\\(.*?[()]{" + re + "}\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(root);
        while (matcher.find()) {
            conjs.add(matcher.group());
        }
        return conjs;
    }

    public void printRoot(String root) {
        List<String> conjs = getConjuagations2(root);
        conjs.stream().forEach((cj) -> {
            pl(cj);
        });
    }

    public List<String> getGerunds(String conj) {
        List<String> gerunds = new ArrayList();

        pl(conj.split("\\(")[0]);
        String regex = ".\\([^()]*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(conj);
        while (matcher.find()) {
            gerunds.add(matcher.group());
        }

        return gerunds;
    }

    public void printGerund(String root) {
        List<String> conjs = this.getConjuagations2(root);
        List<String> gerunds;
        for (String cj : conjs) {
            gerunds = getGerunds(cj);
            for (String g : gerunds) {
                pl(g);
            }
        }
    }

    public void printElements(String line) {
//        pl(line);
        String regex = "C\\d+\\(.*?[()]{2}\\)";
        regex = ".\\([^()]*\\)";
        regex = "\\([^()]*\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        String[] elms;
        while (matcher.find()) {
            elms = matcher.group().replaceAll("\\(", "").replaceAll("\\)", "").split(",");
            for (String s : elms) {
                pt(s);
            }
        }
    }
}
