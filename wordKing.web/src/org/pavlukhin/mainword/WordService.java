package org.pavlukhin.mainword;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by ivan on 23.02.14.
 */
@WebService
public interface WordService {
    @WebMethod
    public int getWordsCount() throws Exception;
    @WebMethod
    public void submitWord(String word) throws Exception;
    @WebMethod
    public Word[] getWords() throws Exception;
    @WebMethod
    public Word[] getRegionWords(int from, int length) throws Exception;
    @WebMethod
    public Word[] getTopWords(int length) throws Exception;
 }
