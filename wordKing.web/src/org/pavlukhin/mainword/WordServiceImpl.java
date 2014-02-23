package org.pavlukhin.mainword;
import javax.jws.WebService;
import javax.naming.NamingException;
import java.sql.SQLException;

/**
 * Created by ivan on 23.02.14.
 */
@WebService(endpointInterface = "org.pavlukhin.mainword.WordService")
public class WordServiceImpl implements WordService {
    private WordDB db;

    public WordServiceImpl() throws SQLException, NamingException {
        db = new WordDB();
    }

    public int getWordsCount() throws SQLException {
        return db.getWordsCount();
    }

    @Override
    public void submitWord(String word) throws Exception {
        checkWordSyntax(word);
        db.submitWord(word);
    }

    @Override
    public Word[] getWords() throws Exception {
        return db.getWords();
    }

    @Override
    public Word[] getRegionWords(int from, int length) throws Exception {
        return db.getRegionWords(from, length);
    }

    @Override
    public Word[] getTopWords(int length) throws Exception {
        return db.getTopWords(length);
    }

    private void checkWordSyntax(String word) throws WrongFormatException {
        if(!word.matches("\\w+")) throw new WrongFormatException();
    }

    static class WrongFormatException extends Exception {}
}
