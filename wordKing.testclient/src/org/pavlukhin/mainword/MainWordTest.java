package org.pavlukhin.mainword;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by ivan on 23.02.14.
 */
public class MainWordTest {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://wordking-pavlukhin.rhcloud.com//services/WordService?wsdl");
        QName qname = new QName("http://mainword.pavlukhin.org/", "WordServiceImplService");
        Service service = Service.create(url, qname);
        WordService mainWord = service.getPort(WordService.class);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            try {
                if(line.startsWith("+") && line.length() > 1) {
                    mainWord.submitWord(line.substring(1));
                } else {
                    System.out.println(mainWord.getWordsCount());
                    for(Word w : mainWord.getTopWords(100)) {
                        System.out.println(w.word + " : " + w.count);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
