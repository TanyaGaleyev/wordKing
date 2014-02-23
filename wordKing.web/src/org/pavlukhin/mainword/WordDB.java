package org.pavlukhin.mainword;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan on 23.02.14.
 */
public class WordDB {

    public static final String GET_WORD_COUNT = "SELECT COUNT(*) FROM (SELECT DISTINCT word FROM word) as d";
    public static final String GET_WORDS =
            "SELECT word, COUNT(word) as cnt FROM word GROUP BY word ORDER BY cnt desc";
    public static final String GET_WORDS_PART =
            "SELECT word, COUNT(word) as cnt FROM word GROUP BY word ORDER BY cnt desc LIMIT ?,?";
    public static final String INSERT_WORD = "INSERT INTO word (word) VALUES(?)";
    private final DataSource pool;

    public WordDB() throws NamingException, SQLException {
        pool = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/main_word");
    }

    public int getWordsCount() throws SQLException {
        try (Connection c = pool.getConnection()) {
            ResultSet x = c.prepareStatement(GET_WORD_COUNT).executeQuery();
            x.first();
            return x.getInt(1);
        }
    }

    public void submitWord(String word) throws SQLException {
        try (Connection c = pool.getConnection()) {
            PreparedStatement st = c.prepareStatement(INSERT_WORD);
            st.setString(1, word.toLowerCase());
            st.executeUpdate();
        }
    }

    public Word[] getWords() throws SQLException {
        try (Connection c = pool.getConnection()) {
            List<Word> words = new ArrayList<>();
            ResultSet rs = c.prepareStatement(GET_WORDS).executeQuery();
            while (rs.next()) {
                words.add(new Word(rs.getString(1), rs.getInt(2)));
            }
            return words.toArray(new Word[words.size()]);
        }
    }

    public Word[] getRegionWords(int from, int length) throws SQLException {
        try (Connection c = pool.getConnection()) {
            List<Word> words = new ArrayList<>();
            PreparedStatement ps = c.prepareStatement(GET_WORDS_PART);
            ps.setInt(1, from);
            ps.setInt(2, length);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                words.add(new Word(rs.getString(1), rs.getInt(2)));
            }
            return words.toArray(new Word[words.size()]);
        }
    }

    public Word[] getTopWords(int length) throws SQLException {
        return getRegionWords(0, length);
    }
}