package com.vitor.vgi_monografia;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;


public class WebServiceRequests {
    public static void sendPostRequest(String id,String nome, String mensagem, String categoria, String lat, String lon) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String id = params[0];
                String nome = params[1];
                String mensagem = params[2];
                String categoria = params[3];
                String lat = params[4];
                String lon = params[5];

                System.out.println("*** doInBackground ** id: "
                        + id + "\n usuario: " + nome + "\n mensagem: "  + mensagem + "\n categoria: " + categoria + "\n lat: " + lat + "\n lon" + lon);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(
                        "http://10.0.2.2/vgi-monografia/web/marcacao/enviar-marcacao");// replace with your url
                httpPost.addHeader("Content-type",
                        "application/json");
      /*          BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair(
                        "id", id);  // Make your own key value pair
                BasicNameValuePair nomeBasicNameValuePair = new BasicNameValuePair(
                        "nome", nome);  // Make your own key value pair
                BasicNameValuePair mensagemBasicNameValuePair = new BasicNameValuePair(
                        "mensagem", mensagem);  // Make your own key value pair
                BasicNameValuePair categoriaBasicNameValuePair = new BasicNameValuePair(
                        "categoria", categoria);  // Make your own key value pair
                BasicNameValuePair latBasicNameValuePair = new BasicNameValuePair(
                        "lat", lat);  // Make your own key value pair
                BasicNameValuePair lonBasicNameValuePair = new BasicNameValuePair(
                        "lon", lon);  // Make your own key value pair

                // You can add more parameters like above

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(idBasicNameValuePair);
                nameValuePairList.add(nomeBasicNameValuePair);
                nameValuePairList.add(mensagemBasicNameValuePair);
                nameValuePairList.add(categoriaBasicNameValuePair);
                nameValuePairList.add(latBasicNameValuePair);
                nameValuePairList.add(lonBasicNameValuePair);
*/
                JSONObject data = new JSONObject();
                try {
                    data.put("id", id);
                    data.put("nome", nome);
                    data.put("mensagem", mensagem);
                    data.put("categoria", categoria);
                    data.put("lat", lat);
                    data.put("lon", lon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
                    httpPost.setEntity(entity);

                        HttpResponse httpResponse = httpClient
                                .execute(httpPost);
                        InputStream inputStream = httpResponse.getEntity()
                                .getContent();
                        InputStreamReader inputStreamReader = new InputStreamReader(
                                inputStream);
                        BufferedReader bufferedReader = new BufferedReader(
                                inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String bufferedStrChunk = null;
                        while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                            stringBuilder.append(bufferedStrChunk);
                        }

                        return stringBuilder.toString();

                    } catch (ClientProtocolException cpe) {
                        System.out
                                .println("First Exception coz of HttpResponese :"
                                        + cpe);
                        cpe.printStackTrace();
                    } catch (IOException ioe) {
                        System.out
                                .println("Second Exception coz of HttpResponse :"
                                        + ioe);
                        ioe.printStackTrace();
                    }

              /*catch (UnsupportedEncodingException uee) {
                    System.out
                            .println("An Exception given because of UrlEncodedFormEntity argument :"
                                    + uee);
                    uee.printStackTrace();
                }*/
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(id, nome, mensagem,categoria, lat, lon);
    }
}