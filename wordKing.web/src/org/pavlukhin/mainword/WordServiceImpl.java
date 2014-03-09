package org.pavlukhin.mainword;
import javax.jws.WebService;
import javax.naming.NamingException;
import java.sql.SQLException;

/**
 * Created by ivan on 23.02.14.
 */
@WebService(endpointInterface = "org.pavlukhin.mainword.WordService")
public class WordServiceImpl implements WordService {
    public static final String WORD_REGEX = "[a-zA-Z]+|[а-яА-Я]+";
    private WordDB db;

    public WordServiceImpl() throws SQLException, NamingException {
        db = new WordDB();
    }

    public int getWordsCount() throws SQLException {
        return db.getWordsCount();
    }

    @Override
    public int submitWord(String word) throws Exception {
        checkWordSyntax(word);
        db.submitWord(word);
        return db.getWordPosition(word);
    }

    @Override
    public Word[] getWords() throws Exception {
        return db.getWords().toArray(new Word[0]);
    }

    @Override
    public Word[] getRegionWords(int from, int length) throws Exception {
        return db.getRegionWords(from, length).toArray(new Word[0]);
    }

    @Override
    public Word[] getTopWords(int length) throws Exception {
        return db.getTopWords(length).toArray(new Word[0]);
    }

    @Override
    public Word[] getSpecifiedWords(int... ranks) throws Exception {
        return db.getSpecifiedWords(ranks).toArray(new Word[0]);
    }

    private void checkWordSyntax(String word) throws WrongFormatException {
        if(!word.matches(WORD_REGEX)) throw new WrongFormatException();
    }

    @Override
    public int getWordPosition(String word) throws Exception {
        return db.getWordPosition(word);
    }

    static class WrongFormatException extends Exception {}
}
