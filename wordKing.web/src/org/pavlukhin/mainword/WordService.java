package org.pavlukhin.mainword;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by ivan on 23.02.14.
 */
@WebService
public interface WordService {
    @WebMethod
    int getWordsCount() throws Exception;
    @WebMethod
    int submitWord(String word) throws Exception;
    @WebMethod
    Word[] getWords() throws Exception;
    @WebMethod
    Word[] getRegionWords(int from, int length) throws Exception;
    @WebMethod
    Word[] getTopWords(int length) throws Exception;
    @WebMethod
    Word[] getSpecifiedWords(int... ranks) throws Exception;
    @WebMethod
    int getWordPosition(String word) throws Exception;
 }
