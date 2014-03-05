package org.ivan.wordking;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ivan on 01.03.14.
 */
public class WSClient {
    private static final String NAMESPACE = "http://mainword.pavlukhin.org/";
    private static final String URL = "http://wordking-pavlukhin.rhcloud.com/services/WordService?wsdl";
    private static final String GET_WORDS_COUNT = "getWordsCount";
    private static final String GET_TOP_WORDS = "getTopWords";
    public static final String GET_REGION_WORDS = "getRegionWords";
    private static final String SUBMIT_WORD = "submitWord";

    public int getWordsCount() throws XmlPullParserException, IOException {
        SoapObject request = new SoapObject(NAMESPACE, GET_WORDS_COUNT);

//        PropertyInfo propInfo=new PropertyInfo();
//        propInfo.name="arg0";
//        propInfo.type=PropertyInfo.STRING_CLASS;
//
//        request.addProperty(propInfo, "John Smith");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        androidHttpTransport.call(NAMESPACE + GET_WORDS_COUNT, envelope);
        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
        return Integer.parseInt(response.toString());
    }

    public List<String> getTopWords(int n) throws XmlPullParserException, IOException {
        SoapObject request = new SoapObject(NAMESPACE, GET_TOP_WORDS);
        request.addProperty("arg0", n);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        androidHttpTransport.call(NAMESPACE + GET_WORDS_COUNT, envelope);
        List<SoapObject> response = parseArrayResponce(envelope.getResponse());
        List<String> ret = new ArrayList<String>(response.size());
        for (SoapObject o : response) {
            ret.add(o.getPrimitivePropertyAsString("word") + " : " +
                    o.getPrimitivePropertyAsString("count"));
        }
        return ret;
    }

    public List<String> getRegionWords(int start, int n) throws XmlPullParserException, IOException {
        SoapObject request = new SoapObject(NAMESPACE, GET_REGION_WORDS);
        request.addProperty("arg0", start);
        request.addProperty("arg1", n);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        androidHttpTransport.call(NAMESPACE + GET_WORDS_COUNT, envelope);
        List<SoapObject> response = parseArrayResponce(envelope.getResponse());
        List<String> ret = new ArrayList<String>(response.size());
        for (SoapObject o : response) {
            ret.add(o.getPrimitivePropertyAsString("word") + " : " +
                    o.getPrimitivePropertyAsString("count"));
        }
        return ret;
    }

    private List<SoapObject> parseArrayResponce(Object responce) {
        if(responce == null) return Collections.emptyList();
        if(responce instanceof SoapObject) return Collections.singletonList((SoapObject) responce);
        if(responce instanceof List) return (List) responce;
        throw new IllegalArgumentException();
    }

    public int submitWord(String word) throws XmlPullParserException, IOException {
        SoapObject request = new SoapObject(NAMESPACE, SUBMIT_WORD);
        request.addProperty("arg0", word);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        androidHttpTransport.call(NAMESPACE + SUBMIT_WORD, envelope);
        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
        return Integer.parseInt(response.toString());
    }
}
